package com.rahman.pemiluapp.view.ui.dataEntered

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.databinding.FragmentVoterDataBinding
import com.rahman.pemiluapp.utils.DialogPopup
import com.rahman.pemiluapp.utils.DisplayMessage.showToast
import com.rahman.pemiluapp.utils.Response
import com.rahman.pemiluapp.view.viewmodel.ViewModelFactory
import com.rahman.pemiluapp.view.viewmodel.ShowDataViewModel
import kotlin.getValue

class VoterDataFragment : Fragment() {
    private var _binding: FragmentVoterDataBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShowDataViewModel by activityViewModels { ViewModelFactory.getInstance(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentVoterDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgsData = VoterDataFragmentArgs.fromBundle(requireArguments()).inputData
        loadVoterData(safeArgsData)

        binding.btnDeleteData.setOnClickListener { showDeleteVoterConfirmation(safeArgsData) }
    }

    private fun loadVoterData(safeArgsData: String) {
        viewModel.getDataVoter(safeArgsData) { result ->
            when (result) {
                is Response.Success -> binding.voterUI(result)
                is Response.Failure -> showToast(requireContext(), getString(R.string.data_not_found))
                is Response.Error -> if (!result.message.isNullOrEmpty()) showToast(requireContext(), result.message)
                is Response.Loading -> {}
            }
        }
    }

    private fun FragmentVoterDataBinding.voterUI(result: Response.Success) {
        val voter = result.data as VoterDataModel

        nikPemilih.text = voter.nik
        namaPemilih.text = voter.nama
        nohpPemilih.text = voter.nohp
        jkPemilih.text = if (voter.jk == true) getString(R.string.male) else getString(R.string.female)
        tanggalPemilih.text = voter.tanggal
        alamatPemilih.text = voter.alamat

        Glide.with(requireContext())
            .load(voter.gambar?.toUri())
            .placeholder(R.drawable.img_broken_image)
            .error(R.drawable.img_broken_image)
            .into(gambarPemilihNotnull)
    }

    private fun showDeleteVoterConfirmation(safeArgsData: String) {
        DialogPopup.showAlertDialog(
            getString(R.string.delete_dialog_title),
            getString(R.string.delete_dialog_message),
            getString(R.string.delete),
            requireContext()
        ) { processVoterDeletion(safeArgsData) }
    }

    private fun processVoterDeletion(safeArgsData: String) {
        viewModel.deleteDataVoter(safeArgsData) { result ->
            when (result) {
                is Response.Success -> {
                    showToast(requireContext(), getString(R.string.delete_success))
                    findNavController().popBackStack()
                }

                is Response.Failure -> showToast(requireContext(), getString(R.string.delete_failed))
                is Response.Error -> if (!result.message.isNullOrEmpty()) showToast(requireContext(), result.message)
                is Response.Loading -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}