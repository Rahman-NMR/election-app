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
import com.rahman.pemiluapp.utils.DateFormatter.timeStamp
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
        R.id.menu_import -> ioConfirmationDialog(true)
        R.id.menu_export -> ioConfirmationDialog(false)
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
            UriConverter.uriToCachedFile(it, this)?.let { file ->
                handleImportAction(file)
            } ?: run {
                showToast(this, getString(R.string.convert_uri_fail))
            }
        } ?: showToast(this, getString(R.string.uri_is_null))
    }

    private fun handleImportAction(file: File) {
        ioViewModel.importFromJson(file) { response ->
            response.onSuccess { success ->
                loadingState(false, true)

                if (success == true) {
                    showToast(this, getString(R.string.io_data_success, getString(R.string.import_data)))

                    onBackNavigation(false)
                } else {
                    showToast(this, getString(R.string.same_data))
                }
            }.onFailure {
                loadingState(false, true)

                showToast(this, getString(R.string.empty_file))
            }.onError { msg ->
                loadingState(false, true)

                if (!msg.isNullOrEmpty()) showToast(this, msg)
            }.onLoading { loadingState(true) }
        }
    }

    private fun handleExportAction() {
        val str = getString(R.string.election_data)
        val strApp = getString(R.string.app_name)

        val baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val exportFolder = File(baseDir, "$strApp - Export").apply { mkdirs() }
        val outputFile = File(exportFolder, "$str - $timeStamp.json")

        ioViewModel.exportToJson(outputFile) { response ->
            response.onLoading { loadingState(true) }
                .onFailure { msg -> handleExportError(msg, outputFile) }
                .onError { msg -> handleExportError(msg, outputFile) }
                .onSuccess { success ->
                    loadingState(false, true)

                    if (success == true) {
                        showToast(this, getString(R.string.io_data_success, getString(R.string.export_data)))
                        showToast(this, getString(R.string.file_path, baseDir.path))
                    } else showToast(this, getString(R.string.export_data_failed))
                }
        }
    }

    private fun handleExportError(msg: String?, outputFile: File) {
        loadingState(false, true)

        showToast(this, msg ?: getString(R.string.export_data_failed))
        if (outputFile.exists()) outputFile.delete()

        val imageDir = File(outputFile.parentFile, JsonUtil.IMAGE_DIR)
        if (imageDir.exists()) {
            imageDir.deleteRecursively()
        }
    }

    private fun loadVoterData() {
        viewModel.getAllVoters()
        viewModel.responseLiveData.observe(this) { response ->
            response.onLoading { loadingState(true) }
                .onSuccess { loadingState(false, true) }
                .onFailure { loadingState(false, true) }
                .onError { msg ->
                    loadingState(false, true)

                    if (!msg.isNullOrEmpty()) showToast(this, msg)
                }
        }
    }

    private fun loadingState(isVisible: Boolean, fullyGone: Boolean = false) {
        binding.loadingIndicator.isVisible = if (fullyGone) false else isVisible
        binding.rvKosong.isVisible = if (fullyGone) false else !isVisible
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