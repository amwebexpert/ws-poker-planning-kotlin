package com.amwebexpert.app.pokerplanning

import android.os.Bundle
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
import com.amwebexpert.app.pokerplanning.ws.WsTextMessageListener


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    companion object {
        private val TAG = FirstFragment::class.java.simpleName
    }

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val webSocketService get() = WebSocketService.instance

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup drop down list
        val choices = PokerPlanningService.votesCategories.values.toList()
        val adapter = ArrayAdapter<VoteChoices>(
            this.requireContext(),
            android.R.layout.simple_spinner_item,
            choices
        )
        binding.ddlVoteCategories.adapter = adapter

        // setup room action buttons
        binding.btnJoinRoom.isEnabled = false
        binding.btnJoinRoom.setOnClickListener {
            webSocketService.sendVote("MySuperKotlinPowers", "4")
        }

        // bind room name to title
        binding.editTextRoomName.doOnTextChanged { inputText, _, _, _ ->
            binding.textSessionTitle.text = "$inputText Session"
        }

        connectToWebSocket()
    }

    private fun connectToWebSocket() {
        if (_binding === null) {
            return
        }

        webSocketService.connect(isSecure = true,
            hostname = binding.editTextHostName.text.toString(),
            roomUUID = "e78caaee-a1a2-4298-860d-81d7752226ae",
            listener = object : WsTextMessageListener {
                override fun onConnectSuccess() {
                    requireActivity().runOnUiThread {
                        _binding?.btnJoinRoom?.isEnabled = true
                    }
                }

                override fun onConnectFailed() {
                    requireActivity().runOnUiThread {
                        _binding?.btnJoinRoom?.isEnabled = false
                    }
                    connectToWebSocket()
                }

                override fun onClose() {
                    requireActivity().runOnUiThread {
                        _binding?.btnJoinRoom?.isEnabled = false
                    }
                    connectToWebSocket()
                }

                override fun onMessage(text: String) {
                    requireActivity().runOnUiThread {
                        _binding?.textSocketResponse?.setText(text)
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        webSocketService.disconnect()
    }

}