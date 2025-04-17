package com.rahman.pemiluapp.utils

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rahman.pemiluapp.R

object DialogPopup {
    fun showAlertDialog(title: String, msg: String, plus: String, context: Context, positiveBtnClickListener: () -> Unit) {
        val alertdialog = MaterialAlertDialogBuilder(context, R.style.alertDialog)
            .setTitle(title)
            .setMessage(msg)
            .setCancelable(true)
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(plus) { _, _ ->
                positiveBtnClickListener.invoke()
            }.show()

        val negativeBtn = alertdialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        val positiveBtn = alertdialog.getButton(DialogInterface.BUTTON_POSITIVE)

        negativeBtn.apply {
            textSize = 16f
            setTextColor(context.getColor(R.color.blue_grey_700))
        }

        positiveBtn.apply {
            textSize = 16f
            setTextColor(context.getColor(R.color.blue_grey_700))
        }
    }
}