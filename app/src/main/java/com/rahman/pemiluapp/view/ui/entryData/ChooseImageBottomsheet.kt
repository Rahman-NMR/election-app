package com.rahman.pemiluapp.view.ui.entryData

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rahman.pemiluapp.databinding.BottomsheetChooseImageBinding
import com.rahman.pemiluapp.utils.ImageOperation.getImageDir
import com.rahman.pemiluapp.view.viewmodel.EntryDataViewModel
import com.rahman.pemiluapp.view.viewmodel.ViewModelFactory
import kotlin.getValue

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
        if (isSuccess) viewModel.saveImageUri(currentImageUri)
        else viewModel.saveImageUri(null)

        dismiss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomsheetChooseImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCloseBtmsht.setOnClickListener { dismiss() }
        binding.btnDeleteImg.setOnClickListener {
            dismiss()
            viewModel.deleteImageUri()
        }
        binding.btnOpenCamera.setOnClickListener {
            currentImageUri = getImageDir(requireContext())
            cameraLauncher.launch(currentImageUri)
        }
        binding.btnOpenGallery.setOnClickListener {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialog.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}