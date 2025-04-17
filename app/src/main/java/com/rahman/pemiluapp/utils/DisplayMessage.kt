package com.rahman.pemiluapp.utils

import android.content.Context
import android.widget.Toast

object DisplayMessage {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}