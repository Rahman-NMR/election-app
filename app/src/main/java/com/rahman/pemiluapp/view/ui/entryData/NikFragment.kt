package com.rahman.pemiluapp.view.ui.entryData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.databinding.FragmentNikBinding
import com.rahman.pemiluapp.utils.DisplayMessage.showToast
import com.rahman.pemiluapp.utils.EditInputText.hideKeyboard
import com.rahman.pemiluapp.domain.util.onError
import com.rahman.pemiluapp.domain.util.onFailure
import com.rahman.pemiluapp.domain.util.onLoading
import com.rahman.pemiluapp.domain.util.onSuccess
import com.rahman.pemiluapp.view.viewmodel.EntryDataViewModel
import com.rahman.pemiluapp.view.viewmodel.ViewModelFactory

class NikFragment : Fragment() {
    private var _binding: FragmentNikBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EntryDataViewModel by activityViewModels { ViewModelFactory.getInstance(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentNikBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actionUI()
    }

    private fun FragmentNikBinding.actionUI() {
        nikContainer.setOnClickListener { hideKeyboard(requireContext(), requireActivity().currentFocus ?: View(requireContext())) }
        btnCheckNik.setOnClickListener { checkExistanceData() }
        btnShowData.setOnClickListener { navigateToVoterDataFragment() }

        nikValidator.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                nikValidator.helperText = null
                nikValidator.error = null
                btnNikRegistredState(false)
            }
        }
    }

    private fun FragmentNikBinding.checkExistanceData() {
        nikInputText.clearFocus()
        hideKeyboard(requireContext(), nikInputText)

        viewModel.isDataExist(nikInputText.text.toString()) { response ->
            response.onLoading { loadingState(true) }
                .onFailure { validateInput() }
                .onError { loadingState(false, message = it) }
                .onSuccess {
                    loadingState(false)

                    nikValidator.helperText = getString(R.string.nik_registered)
                    btnNikRegistredState(true)
                }
        }
    }

    private fun FragmentNikBinding.btnNikRegistredState(isRegistered: Boolean) {
        btnShowData.isVisible = isRegistered
        btnCheckNik.isEnabled = !isRegistered
        nikValidator.endIconMode = if (isRegistered) TextInputLayout.END_ICON_NONE else TextInputLayout.END_ICON_CLEAR_TEXT
    }

    private fun FragmentNikBinding.validateInput() {
        loadingState(false)

        when {
            nikInputText.text.isNullOrEmpty() -> nikValidator.error = getString(R.string.nik_cant_null)
            nikInputText.text.toString().length < 16 -> nikValidator.error = getString(R.string.nik_length_error)
            !nikInputText.text.toString().isDigitsOnly() -> nikValidator.error = getString(R.string.nik_digit_error)
            else -> navigateToFormFragment()
        }
    }

    private fun loadingState(isLoading: Boolean, message: String? = null) {
        binding.checkNikProgIndic.isVisible = isLoading
        if (!message.isNullOrEmpty()) showToast(requireContext(), message)
    }

    private fun FragmentNikBinding.navigateToVoterDataFragment() {
        val action = NikFragmentDirections.actionNikFragmentToVoterDataFragment(nikInputText.text.toString())
        findNavController().navigate(action)
    }

    private fun FragmentNikBinding.navigateToFormFragment() {
        val action = NikFragmentDirections.actionNikFragmentToFormFragment(nikInputText.text.toString())
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}