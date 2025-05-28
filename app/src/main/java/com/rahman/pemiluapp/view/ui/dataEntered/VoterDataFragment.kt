package com.rahman.pemiluapp.view.ui.dataEntered

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.databinding.FragmentVoterDataBinding
import com.rahman.pemiluapp.utils.DialogPopup
import com.rahman.pemiluapp.utils.DisplayMessage.showToast
import com.rahman.pemiluapp.domain.util.onError
import com.rahman.pemiluapp.domain.util.onFailure
import com.rahman.pemiluapp.domain.util.onLoading
import com.rahman.pemiluapp.domain.util.onSuccess
import com.rahman.pemiluapp.view.viewmodel.ShowDataViewModel
import com.rahman.pemiluapp.view.viewmodel.ViewModelFactory

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
        viewModel.getDataVoter(safeArgsData) { response ->
            response.onLoading { loadingIndicator(true) }
                .onSuccess { data ->
                    loadingIndicator(false)

                    binding.voterUI(data)
                }.onFailure {
                    loadingIndicator(false)

                    showToast(requireContext(), getString(R.string.data_not_found))
                }.onError { msg ->
                    loadingIndicator(false)

                    if (!msg.isNullOrEmpty()) showToast(requireContext(), msg)
                }
        }
    }

    private fun FragmentVoterDataBinding.voterUI(voter: VoterDataModel?) {
        val dataNotFound = getString(R.string.data_not_found)

        voter?.let {
            subbheaderCard.text = getString(R.string.nik_x, it.nik?.ifEmpty { dataNotFound })
            nameBody.text = it.nama?.ifEmpty { dataNotFound }
            phoneBody.text = it.nohp?.ifEmpty { dataNotFound }
            genderBody.text = it.jk?.let { isMale ->
                if (isMale) getString(R.string.male) else getString(R.string.female)
            } ?: getString(R.string.data_not_found)
            dateBody.text = it.tanggal?.ifEmpty { dataNotFound }
            addressBody.text = it.alamat?.ifEmpty { dataNotFound }

            Glide.with(requireContext())
                .load(it.gambar?.toUri())
                .placeholder(R.drawable.img_broken_image)
                .error(R.drawable.img_broken_image)
                .into(evidenceImage)
        } ?: showToast(requireContext(), dataNotFound)

    }

    private fun showDeleteVoterConfirmation(safeArgsData: String?) {
        val nik = safeArgsData ?: getString(R.string.strip)

        DialogPopup.showAlertDialog(
            getString(R.string.delete_dialog_title),
            getString(R.string.delete_dialog_message, getString(R.string.nik), nik),
            getString(R.string.delete),
            requireContext()
        ) { processVoterDeletion(safeArgsData) }
    }

    private fun processVoterDeletion(nik: String?) {
        viewModel.deleteDataVoter(nik) { response ->
            response.onLoading { loadingIndicator(true) }
                .onSuccess {
                    loadingIndicator(false)

                    showToast(requireContext(), getString(R.string.delete_success))
                    findNavController().popBackStack()
                }.onFailure {
                    loadingIndicator(false)

                    showToast(requireContext(), getString(R.string.delete_failed))
                }.onError { msg ->
                    loadingIndicator(false)

                    if (!msg.isNullOrEmpty()) showToast(requireContext(), msg)
                }
        }
    }

    private fun loadingIndicator(isLoading: Boolean) {
        binding.dataDetailProgIndic.isVisible = isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}