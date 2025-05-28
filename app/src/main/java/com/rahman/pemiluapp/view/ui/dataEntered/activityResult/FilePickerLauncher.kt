package com.rahman.pemiluapp.view.ui.dataEntered.activityResult

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class FilePickerLauncher(activity: ComponentActivity, private val onFilePicked: (Uri) -> Unit) {
    val launcher = activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { onFilePicked(it) }
    }

    fun launchPicker() {
        launcher.launch("application/json")
    }
}