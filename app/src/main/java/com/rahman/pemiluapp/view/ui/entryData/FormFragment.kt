package com.rahman.pemiluapp.view.ui.entryData

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
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
import com.rahman.pemiluapp.domain.util.onError
import com.rahman.pemiluapp.domain.util.onFailure
import com.rahman.pemiluapp.domain.util.onLoading
import com.rahman.pemiluapp.domain.util.onSuccess
import com.rahman.pemiluapp.utils.DateFormatter.formatDate
import com.rahman.pemiluapp.utils.DisplayMessage.showSnackbar
import com.rahman.pemiluapp.utils.DisplayMessage.showToast
import com.rahman.pemiluapp.utils.EditInputText.hideKeyboard
import com.rahman.pemiluapp.utils.ImageOperation.reImaged
import com.rahman.pemiluapp.utils.ImageOperation.reduceFileImage
import com.rahman.pemiluapp.utils.PermissionChecker.checkPermission
import com.rahman.pemiluapp.utils.UriConverter.uriToImageFile
import com.rahman.pemiluapp.view.viewmodel.EntryDataViewModel
import com.rahman.pemiluapp.view.viewmodel.ViewModelFactory

class FormFragment : Fragment() {
    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EntryDataViewModel by activityViewModels { ViewModelFactory.getInstance(requireContext()) }
    private val fusedLocationClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(requireContext()) }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
        val allPermissionsGranted = permission.all { it.value }

        if (allPermissionsGranted) {
            if (permission.keys.contains(CAMERA)) showBottomsheet()
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
        viewModel.timestamp.observe(viewLifecycleOwner) { timestamp ->
            insertDate.setText(formatDate(timestamp))
        }

        viewModel.currentImageUri.observe(viewLifecycleOwner) { uri ->
            Glide.with(requireContext()).load(uri)
                .transform(CenterCrop())
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
        insertEvidencePhoto.setOnClickListener { showBottomsheet() }
    }

    private fun loadingState(isLoading: Boolean) {
        binding.inputDataProgIndic.isVisible = isLoading
    }

    private fun showBottomsheet() {
        if (checkPermission(requireContext(), CAMERA)) {
            val bottomSheet = ChooseImageBottomsheet()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
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

        viewModel.getCurrentLocation(coordinate) { response ->
            response.onLoading { loadingState(true) }
                .onSuccess { data ->
                    loadingState(false)

                    val inputAddress = insertAddress.text.toString().trim()
                    val geoAddress = "$inputAddress\n\n${data}"
                    val combineAddress = if (inputAddress.isEmpty()) data.toString() else geoAddress

                    insertAddress.setText(combineAddress)
                }.onFailure {
                    loadingState(false)

                    showToast(requireContext(), getString(R.string.failed_get_current_location))
                }.onError { msg ->
                    loadingState(false)

                    if (!msg.isNullOrEmpty()) showToast(requireContext(), msg)
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
        datePicker.addOnPositiveButtonClickListener { viewModel.saveTimestamp(it) }
    }

    private fun FragmentFormBinding.notNullChecker() {
        hideKeyboard(requireContext(), requireActivity().currentFocus ?: View(requireContext()))

        val voterId = insertNik.text.toString().trim()
        val voterName = insertName.text.toString().trim()
        val phoneNumber = insertPhone.text.toString().trim()
        val voterAddress = insertAddress.text.toString().trim()
        val isMaleSelected = genderRadio.checkedRadioButtonId == R.id.male
        val isGenderSelected = genderRadio.checkedRadioButtonId

        val votersData = VoterDataModel(
            nik = voterId,
            nama = voterName,
            nohp = phoneNumber,
            alamat = voterAddress,
            jk = isMaleSelected
        )

        viewModel.nullChecker(votersData, isGenderSelected) { result ->
            result.onLoading { loadingState(true) }
                .onSuccess { data ->
                    loadingState(false)

                    addNewDataVoters(data ?: VoterDataModel())
                }.onFailure { msg ->
                    loadingState(false)

                    val emptyFieldResId = when (msg) {
                        EntryDataViewModel.NIK -> R.string.nik
                        EntryDataViewModel.NAMA -> R.string.name
                        EntryDataViewModel.NOHP -> R.string.phone
                        EntryDataViewModel.TANGGAL -> R.string.date
                        EntryDataViewModel.ALAMAT -> R.string.address
                        EntryDataViewModel.GENDER -> R.string.gender
                        EntryDataViewModel.GAMBAR -> R.string.evidence_process
                        else -> null
                    }

                    emptyFieldResId?.let { fieldResId ->
                        val emptyField = getString(fieldResId).lowercase()
                        showSnackbar(root, getString(R.string.fill_your_x, emptyField), getString(R.string.close))
                    }
                }.onError { msg ->
                    loadingState(false)

                    if (!msg.isNullOrEmpty()) showToast(requireContext(), msg)
                }
        }
    }

    private fun addNewDataVoters(voter: VoterDataModel) {
        val currentImageUri = viewModel.currentImageUri.value
        val reducedImage = uriToImageFile(currentImageUri!!, requireContext()).reduceFileImage()
        val img = reImaged(requireContext(), reducedImage)
        val updatedVoter = voter.copy(gambar = img.toString())

        viewModel.addNewVoter(updatedVoter) { result ->
            result.onLoading { loadingState(true) }
                .onSuccess { data ->
                    loadingState(false)

                    showToast(requireContext(), getString(R.string.save_data_success))
                    requireActivity().finish()
                }.onFailure {
                    loadingState(false)

                    showToast(requireContext(), getString(R.string.save_data_failed))
                }.onError { msg ->
                    loadingState(false)

                    if (!msg.isNullOrEmpty()) showToast(requireContext(), msg)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}