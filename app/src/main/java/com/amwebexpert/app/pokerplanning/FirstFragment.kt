package com.amwebexpert.app.pokerplanning

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.amwebexpert.app.pokerplanning.databinding.FragmentFirstBinding
import com.amwebexpert.app.pokerplanning.service.PokerPlanningService
import com.amwebexpert.app.pokerplanning.service.VoteChoices

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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

        binding.btnJoinRoom.setOnClickListener {
            //
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}