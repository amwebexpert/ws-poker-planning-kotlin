package com.amwebexpert.app.pokerplanning

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.amwebexpert.app.pokerplanning.databinding.FragmentFirstBinding
import com.amwebexpert.app.pokerplanning.service.PokerPlanningService
import com.amwebexpert.app.pokerplanning.service.VoteChoices
import com.amwebexpert.app.pokerplanning.ws.WebSocketService
import okhttp3.*
import okio.ByteString


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textSessionTitle.text = "${binding.editTextRoomName.text} Session"
        binding.editTextRoomName.doOnTextChanged { inputText, _, _, _ ->
            binding.textSessionTitle.text = "$inputText Session"
        }

        val choices = PokerPlanningService.votesCategories.values.toList()
        val adapter = ArrayAdapter<VoteChoices>(this.requireContext(), android.R.layout.simple_spinner_item, choices)
        binding.ddlVoteCategories.adapter = adapter

        connectToWebSocket()

        binding.btnJoinRoom.setOnClickListener {
            val service = WebSocketService.instance
            service.sendVote("MySuperKotlinPowers", "4")
        }
    }

    private fun connectToWebSocket() {
        if (_binding === null) {
            return
        }

        val service = WebSocketService.instance
        val hostname = binding.editTextHostName.text.toString()

        service.connect(hostname = hostname, wsListener = object: WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                output("Socket opened.")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                output("Receiving : $text")
                requireActivity().runOnUiThread {
                    binding.textSocketResponse.setText(text)
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                output("Receiving bytes : " + bytes!!.hex())
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                output("Closing : $code / $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                output("Closed : $code / $reason")
                connectToWebSocket()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
                output("Error : ${t.message} $response")
                connectToWebSocket() // ensure it's necessary?
            }

            private fun output(txt: String) {
                Log.v("WSS", txt)
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        WebSocketService.instance.disconnect()
    }

}