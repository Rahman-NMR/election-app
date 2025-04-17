package com.rahman.pemiluapp.view.ui.entryData

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.data.model.CoordinateModel
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.databinding.FragmentFormBinding
import com.rahman.pemiluapp.utils.DateFormatter.formatDate
import com.rahman.pemiluapp.utils.DisplayMessage.showToast
import com.rahman.pemiluapp.utils.EditInputText.hideKeyboard
import com.rahman.pemiluapp.utils.ImageOperation.getImageDir
import com.rahman.pemiluapp.utils.ImageOperation.reImaged
import com.rahman.pemiluapp.utils.ImageOperation.reduceFileImage
import com.rahman.pemiluapp.utils.ImageOperation.uriToFile
import com.rahman.pemiluapp.utils.PermissionChecker.checkPermission
import com.rahman.pemiluapp.utils.Response
import com.rahman.pemiluapp.view.viewmodel.EntryDataViewModel
import com.rahman.pemiluapp.view.viewmodel.ViewModelFactory

class FormFragment : Fragment() {
    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null
    private var timestamp: Long = 0

    private val viewModel: EntryDataViewModel by activityViewModels { ViewModelFactory.getInstance(requireContext()) }
    private val fusedLocationClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(requireContext()) }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) viewModel.saveImageUri(currentImageUri)
        else viewModel.saveImageUri(null)
    }
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri: Uri? ->
        if (imageUri != null) {
            currentImageUri = imageUri
            viewModel.saveImageUri(imageUri)
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
        val allPermissionsGranted = permission.all { it.value }

        if (allPermissionsGranted) {
            if (permission.keys.contains(CAMERA)) openCamera()
            if (permission.keys.contains(ACCESS_FINE_LOCATION) || permission.keys.contains(ACCESS_COARSE_LOCATION)) permissionLocation()
        } else showToast(requireContext(), getString(R.string.permission_denied))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = FormFragmentArgs.fromBundle(requireArguments())
        binding.insertNik.setText(args.inputData)

        binding.viewModelObserver()
        binding.actionUI()
    }

    private fun FragmentFormBinding.viewModelObserver() {
        viewModel.currentImageUri.observe(viewLifecycleOwner) { uri ->
            currentImageUri = uri

            Glide.with(requireContext()).load(uri)
                .apply(RequestOptions().centerInside())
                .placeholder(R.drawable.img_broken_image)
                .error(R.drawable.img_broken_image)
                .into(insertEvidencePhoto)
        }
    }

    private fun FragmentFormBinding.actionUI() {
        formContainer.setOnClickListener { hideKeyboard(requireContext(), requireActivity().currentFocus ?: View(requireContext())) }
        btnSubmit.setOnClickListener { notNullChecker() }

        layoutDate.setEndIconOnClickListener { selectDate() }
        layoutAddress.setEndIconOnClickListener { permissionLocation() }
        insertEvidencePhoto.setOnClickListener { v ->
            val popup = PopupMenu(v.context, v)

            popup.menuInflater.inflate(R.menu.menu_choose_image, popup.menu)
            popup.setOnMenuItemClickListener { item -> actionMenu(item) }
            popup.show()
        }
    }

//    todo: ui menu difficult to use
    private fun actionMenu(item: MenuItem) = when (item.itemId) {
        R.id.from_camera -> {
            openCamera()
            true
        }

        R.id.from_gallery -> {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            true
        }

        else -> false
    }

    private fun openCamera() {
        if (checkPermission(requireContext(), CAMERA)) {
            currentImageUri = getImageDir(requireContext())
            cameraLauncher.launch(currentImageUri)
        } else requestPermissionLauncher.launch(arrayOf(CAMERA))
    }

    private fun permissionLocation() {
        if (checkPermission(requireContext(), ACCESS_FINE_LOCATION) && checkPermission(requireContext(), ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { result -> binding.getCurrentLocation(result) }
                .addOnFailureListener { showToast(requireContext(), getString(R.string.failed_get_current_location)) }
        } else requestPermissionLauncher.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
    }

    private fun FragmentFormBinding.getCurrentLocation(result: Location) {
        val coordinate = CoordinateModel(result.latitude, result.longitude)

        viewModel.getCurrentLocation(coordinate) { result ->
            when (result) {
                is Response.Success -> {
                    val inputAddress = insertAddress.text.toString().trim()
                    val geoAddress = "$inputAddress\n\n${result.data}"
                    val combineAddress = if (inputAddress.isEmpty()) result.data.toString() else geoAddress

                    insertAddress.setText(combineAddress)
                }

                is Response.Failure -> showToast(requireContext(), getString(R.string.failed_get_current_location))
                is Response.Error -> if (!result.message.isNullOrEmpty()) showToast(requireContext(), result.message)
                is Response.Loading -> {}
            }
        }
    }

    private fun FragmentFormBinding.selectDate() {
        val constraintBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())
        val materialDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_date))
            .setCalendarConstraints(constraintBuilder.build())
        val datePicker = materialDatePicker.build()

        datePicker.show(childFragmentManager, datePicker.toString())
        datePicker.addOnPositiveButtonClickListener { timestampInMillis ->
            timestamp = timestampInMillis
            insertDate.setText(formatDate(timestampInMillis, "Asia/Jakarta"))
        }
    }

    private fun FragmentFormBinding.notNullChecker() {
        val voterId = insertNik.text.toString().trim()
        val voterName = insertName.text.toString().trim()
        val phoneNumber = insertPhone.text.toString().trim()
        val creationTimestamp = timestamp.toString()
        val voterAddress = insertAddress.text.toString().trim()
        val isMaleSelected = genderRadio.checkedRadioButtonId == R.id.male
        val isGenderSelected = genderRadio.checkedRadioButtonId

        val votersData = VoterDataModel(
            nik = voterId,
            nama = voterName,
            nohp = phoneNumber,
            tanggal = creationTimestamp,
            alamat = voterAddress,
            jk = isMaleSelected
        )

        viewModel.nullChecker(votersData, isGenderSelected) { result ->
            when (result) {
                is Response.Success -> addNewDataVoters(result.data as VoterDataModel)
                is Response.Failure -> {
                    val emptyField = when (result.message) {
                        EntryDataViewModel.NIK -> getString(R.string.nik)
                        EntryDataViewModel.NAMA -> getString(R.string.name)
                        EntryDataViewModel.NOHP -> getString(R.string.phone)
                        EntryDataViewModel.TANGGAL -> getString(R.string.date)
                        EntryDataViewModel.ALAMAT -> getString(R.string.address)
                        EntryDataViewModel.GENDER -> getString(R.string.gender)
                        EntryDataViewModel.GAMBAR -> getString(R.string.evidence_process)
                        else -> ""
                    }.lowercase()

                    if (!result.message.isNullOrEmpty() && emptyField.isNotEmpty())
                        showToast(requireContext(), getString(R.string.fill_your_x, emptyField))
                }

                is Response.Error -> if (!result.message.isNullOrEmpty()) showToast(requireContext(), result.message)
                is Response.Loading -> {}
            }
        }
    }

    private fun addNewDataVoters(voter: VoterDataModel) {
        val reducedImage = uriToFile(currentImageUri!!, requireContext()).reduceFileImage()
        val img = reImaged(requireContext(), reducedImage)
        val updatedVoter = voter.copy(gambar = img.toString())

        viewModel.addNewVoter(updatedVoter) { result ->
            when (result) {
                is Response.Success -> {
                    showToast(requireContext(), getString(R.string.save_data_success))
                    requireActivity().finish()
                }

                is Response.Failure -> showToast(requireContext(), getString(R.string.save_data_failed))
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