package com.rahman.pemiluapp.view.ui.dataEntered

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.data.util.JsonUtil
import com.rahman.pemiluapp.databinding.ActivityShowDataBinding
import com.rahman.pemiluapp.domain.util.onError
import com.rahman.pemiluapp.domain.util.onFailure
import com.rahman.pemiluapp.domain.util.onLoading
import com.rahman.pemiluapp.domain.util.onSuccess
import com.rahman.pemiluapp.utils.DateFormatter.currentTime
import com.rahman.pemiluapp.utils.DateFormatter.timeStampFilename
import com.rahman.pemiluapp.utils.DialogPopup
import com.rahman.pemiluapp.utils.DisplayMessage.showToast
import com.rahman.pemiluapp.utils.UriConverter
import com.rahman.pemiluapp.view.ui.dataEntered.activityResult.FilePickerLauncher
import com.rahman.pemiluapp.view.viewmodel.DataIOViewModel
import com.rahman.pemiluapp.view.viewmodel.ShowDataViewModel
import com.rahman.pemiluapp.view.viewmodel.ViewModelFactory
import java.io.File

class ShowDataActivity : AppCompatActivity() {
    private var _binding: ActivityShowDataBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShowDataViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private val ioViewModel: DataIOViewModel by viewModels { ViewModelFactory.getInstance(this) }

    private var filePickerLauncher: FilePickerLauncher = FilePickerLauncher(this) { uri -> uriToFile(uri) }
    private val onBackCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackNavigation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityShowDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        onBackPressedDispatcher.addCallback(this, onBackCallback)

        loadVoterData()

        binding.appBar.setNavigationOnClickListener { onBackNavigation() }
        binding.appBar.setOnMenuItemClickListener { menuItem -> menuItemClick(menuItem) }
    }

    private fun menuItemClick(menu: MenuItem): Boolean = when (menu.itemId) {
        R.id.menu_import -> ioConfirmationDialog(isImport = true)
        R.id.menu_export -> ioConfirmationDialog(isImport = false)
        else -> false
    }

    private fun ioConfirmationDialog(isImport: Boolean): Boolean {
        val title =
            if (isImport) getString(R.string.import_dialog_title)
            else getString(R.string.export_dialog_title)
        val msg =
            if (isImport) getString(R.string.import_dialog_message, getString(R.string.nik))
            else getString(R.string.export_dialog_message)
        val positiveBtnText =
            if (isImport) getString(R.string.import_data)
            else getString(R.string.export_data)

        DialogPopup.showAlertDialog(title, msg, positiveBtnText, this) {
            if (isImport) filePickerLauncher.launchPicker()
            else handleExportAction()
        }

        return true
    }

    private fun uriToFile(uri: Uri?) {
        uri?.let {
            val destinationFile = File(cacheDir, "imported_data_$currentTime.json")
            UriConverter.writeUriToFile(it, this, destinationFile)?.let { file ->
                handleImportAction(file)
            } ?: run { showToast(this, getString(R.string.convert_uri_fail)) }
        } ?: showToast(this, getString(R.string.file_uri_null))
    }

    private fun handleImportAction(file: File) {
        ioViewModel.importFromJson(file) { response ->
            response.onLoading { loadingState(true, isIOLoading = true) }
                .onFailure { loadingState(false, message = it ?: getString(R.string.empty_file)) }
                .onError { loadingState(false, message = it) }
                .onSuccess { success ->
                    val resultMessage =
                        if (success == true) getString(R.string.io_data_success, getString(R.string.import_data))
                        else getString(R.string.same_data)

                    loadingState(false, fullyGone = true, isIOLoading = true, message = resultMessage)
                    onBackNavigation(false)
                }
        }
    }

    private fun handleExportAction() {
        val baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val exportFolder = File(baseDir, getString(R.string.app_name))
        val jsonOutputFile = File(exportFolder, "${getString(R.string.election_data)} - $timeStampFilename.json")
        val imageDir = File(exportFolder, JsonUtil.IMAGE_DIR)

        ioViewModel.exportToJson(jsonOutputFile) { response ->
            response.onLoading { loadingState(true, isIOLoading = true) }
                .onFailure { handleExportError(it, jsonOutputFile, imageDir, exportFolder) }
                .onError { handleExportError(it, jsonOutputFile, imageDir, exportFolder) }
                .onSuccess { success ->
                    loadingState(false, fullyGone = true, isIOLoading = true)
                    onBackNavigation(false)

                    if (success == true) {
                        showToast(this, getString(R.string.io_data_success, getString(R.string.export_data)))
                        showToast(this, getString(R.string.file_path, baseDir.path))
                    } else handleExportError(null, jsonOutputFile, imageDir, exportFolder)
                }
        }
    }

    private fun handleExportError(msg: String?, jsonFile: File, imageDir: File, mainExportDirectory: File) {
        loadingState(false, isIOLoading = true, message = msg ?: getString(R.string.export_data_failed))

        if (jsonFile.exists()) jsonFile.delete()
        if (imageDir.exists()) imageDir.deleteRecursively()

        val filesInMainDir = mainExportDirectory.listFiles()
        if (mainExportDirectory.exists() && (filesInMainDir == null || filesInMainDir.isEmpty())) {
            mainExportDirectory.delete()
        }
    }

    private fun loadVoterData() {
        viewModel.getAllVoters()
        viewModel.responseLiveData.observe(this) { response ->
            response.onLoading { loadingState(true) }
                .onSuccess { loadingState(false, fullyGone = true) }
                .onFailure { loadingState(false, message = it) }
                .onError { loadingState(false, message = it) }
        }
    }

    private fun loadingState(isVisible: Boolean, fullyGone: Boolean = false, isIOLoading: Boolean = false, message: String? = null) {
        if (!message.isNullOrEmpty()) showToast(this, message)
        binding.loadingIndicator.isVisible = if (fullyGone) false else isVisible
        if (!isIOLoading) binding.rvKosong.isVisible = if (fullyGone) false else !isVisible
    }

    private fun onBackNavigation(isNotFromImport: Boolean = true) {
        val navController = findNavController(R.id.nav_show_fragment)

        if (isNotFromImport) {
            if (navController.currentDestination?.id == R.id.votersDataListFragment) finish()
            else navController.popBackStack()
        } else {
            viewModel.getAllVoters()

            if (navController.currentDestination?.id != R.id.votersDataListFragment)
                navController.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}