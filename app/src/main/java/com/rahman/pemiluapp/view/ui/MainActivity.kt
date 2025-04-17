package com.rahman.pemiluapp.view.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rahman.pemiluapp.databinding.ActivityMainBinding
import com.rahman.pemiluapp.utils.DialogPopup.showAlertDialog
import com.rahman.pemiluapp.view.ui.auth.LoginActivity
import com.rahman.pemiluapp.view.ui.dataEntered.ShowDataActivity
import com.rahman.pemiluapp.view.ui.entryData.FormEntryActivity
import com.rahman.pemiluapp.view.ui.information.InformationActivity

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val nik = sharedPreferences.getString("nik", "")
        val nikTxt = "Halo, $nik"

        binding.currentAccount.text = nikTxt

        binding.btnInformasi.setOnClickListener { startActivity(Intent(this, InformationActivity::class.java)) }
        binding.btnFormEntry.setOnClickListener { startActivity(Intent(this, FormEntryActivity::class.java)) }
        binding.btnLihatData.setOnClickListener { startActivity(Intent(this, ShowDataActivity::class.java)) }
        binding.btnKeluar.setOnClickListener {
            showAlertDialog("Keluar Akun?", "Anda akan keluar dari akun yang digunakan sekarang.", "Keluar", this@MainActivity) {
                val editor = sharedPreferences.edit()
                editor.putBoolean("isLoggedIn", false)
                editor.apply()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}