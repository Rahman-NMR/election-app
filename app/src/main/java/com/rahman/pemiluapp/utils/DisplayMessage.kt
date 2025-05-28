package com.rahman.pemiluapp.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

object DisplayMessage {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackbar(v: View, msg: String, actionTxt: String, listener: View.OnClickListener? = null) {
        val snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
        snackbar.setAction(actionTxt) { listener?.onClick(v) ?: snackbar.dismiss() }.show()
    }
}