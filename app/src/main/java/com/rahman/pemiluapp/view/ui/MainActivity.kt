package com.rahman.pemiluapp.view.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rahman.pemiluapp.databinding.ActivityMainBinding
import com.rahman.pemiluapp.view.ui.dataEntered.ShowDataActivity
import com.rahman.pemiluapp.view.ui.entryData.FormEntryActivity
import com.rahman.pemiluapp.view.ui.information.InformationActivity

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val onBackCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        onBackPressedDispatcher.addCallback(this, onBackCallback)

        binding.btnInformasi.setOnClickListener { startActivity(Intent(this, InformationActivity::class.java)) }
        binding.btnFormEntry.setOnClickListener { startActivity(Intent(this, FormEntryActivity::class.java)) }
        binding.btnLihatData.setOnClickListener { startActivity(Intent(this, ShowDataActivity::class.java)) }
        binding.btnKeluar.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}