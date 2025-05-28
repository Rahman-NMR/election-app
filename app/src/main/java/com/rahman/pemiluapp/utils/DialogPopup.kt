package com.rahman.pemiluapp.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rahman.pemiluapp.R

object DialogPopup {
    fun showAlertDialog(title: String, message: String, positiveBtnText: String, context: Context, positiveBtnClickListener: () -> Unit) {
        MaterialAlertDialogBuilder(context, R.style.alertDialog)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(positiveBtnText) { _, _ ->
                positiveBtnClickListener.invoke()
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}