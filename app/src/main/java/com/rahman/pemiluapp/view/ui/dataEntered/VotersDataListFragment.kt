package com.rahman.pemiluapp.view.ui.dataEntered

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.databinding.FragmentVotersDataListBinding
import com.rahman.pemiluapp.domain.util.onFailure
import com.rahman.pemiluapp.domain.util.onSuccess
import com.rahman.pemiluapp.utils.EditInputText.hideKeyboard
import com.rahman.pemiluapp.utils.SpacingDecoration
import com.rahman.pemiluapp.view.adapter.VoterListAdapter
import com.rahman.pemiluapp.view.viewmodel.ShowDataViewModel
import com.rahman.pemiluapp.view.viewmodel.ViewModelFactory

class VotersDataListFragment : Fragment() {
    private var _binding: FragmentVotersDataListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShowDataViewModel by activityViewModels { ViewModelFactory.getInstance(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentVotersDataListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val votersListAdapter = configureVoterListAdapter()
        binding.setupAdapter(votersListAdapter)

        binding.onSwipeToRefresh()
        initializeSearch()

        viewModel.responseLiveData.observe(viewLifecycleOwner) { response ->
            response.onSuccess { data -> votersListAdapter.submitList(data) }
                .onFailure { votersListAdapter.submitList(emptyList()) }
        }
    }

    private fun FragmentVotersDataListBinding.onSwipeToRefresh() {
        swipeRefresh.setOnRefreshListener {
            hideKeyboard(requireContext(), requireActivity().currentFocus ?: View(requireContext()))
            searchVoter.setQuery("", false)
            searchVoter.clearFocus()

            viewModel.getAllVoters()
            swipeRefresh.isRefreshing = false
        }
    }

    private fun initializeSearch() {
        val searchListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard(requireContext(), requireActivity().currentFocus ?: View(requireContext()))
                viewModel.searchVoter(query)

                return true
            }

            override fun onQueryTextChange(s: String?) = false
        }

        binding.searchVoter.setOnQueryTextListener(searchListener)
    }

    private fun configureVoterListAdapter() = VoterListAdapter { selectedVoter ->
        val voterIdString = selectedVoter.nik.toString()
        val action = VotersDataListFragmentDirections.actionVotersDataListFragmentToVoterDataFragment(voterIdString)

        findNavController().navigate(action)
    }

    private fun FragmentVotersDataListBinding.setupAdapter(voterListAdapter: VoterListAdapter) {
        val paddingBottom = resources.getDimensionPixelSize(R.dimen.bottomdp)
        val defaultPadding = resources.getDimensionPixelSize(R.dimen.normaldp)

        rvPemilih.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvPemilih.adapter = voterListAdapter
        rvPemilih.addItemDecoration(SpacingDecoration(defaultPadding, paddingBottom, true))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}