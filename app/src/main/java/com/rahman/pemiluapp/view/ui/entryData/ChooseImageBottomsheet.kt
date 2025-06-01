package com.rahman.pemiluapp.view.ui.entryData

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.databinding.BottomsheetChooseImageBinding
import com.rahman.pemiluapp.utils.DisplayMessage.showToast
import com.rahman.pemiluapp.utils.FileDirectoryOperation.generateImageTempFile
import com.rahman.pemiluapp.utils.FileDirectoryOperation.getUriFromFileTemp
import com.rahman.pemiluapp.view.viewmodel.EntryDataViewModel
import com.rahman.pemiluapp.view.viewmodel.ViewModelFactory

class ChooseImageBottomsheet : BottomSheetDialogFragment() {
    private var _binding: BottomsheetChooseImageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EntryDataViewModel by activityViewModels { ViewModelFactory.getInstance(requireContext()) }
    private var currentImageUri: Uri? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri: Uri? ->
        if (imageUri != null) viewModel.saveImageUri(imageUri)

        dismiss()
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        val uri = if (isSuccess) currentImageUri else null
        viewModel.saveImageUri(uri)

        dismiss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomsheetChooseImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCloseBtmsht.setOnClickListener { dismiss() }
        binding.btnDeleteImg.setOnClickListener { resetImageSelection() }
        binding.btnOpenCamera.setOnClickListener { captureImageWithCamera() }
        binding.btnOpenGallery.setOnClickListener {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun captureImageWithCamera() {
        val imageUri = generateImageTempFile(requireContext())?.let { fileDirectory ->
            getUriFromFileTemp(requireContext(), fileDirectory)
        } ?: (return).also {
            showToast(requireContext(), getString(R.string.image_processing_failed))
            resetImageSelection()
        }

        currentImageUri = imageUri
        cameraLauncher.launch(currentImageUri)
    }

    private fun resetImageSelection() {
        viewModel.saveImageUri(null)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}