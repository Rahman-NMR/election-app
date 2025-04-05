package com.rahman.pemiluapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rahman.pemiluapp.AlertDialogC.showAlertDialog
import com.rahman.pemiluapp.databinding.ActivityMainBinding
import com.rahman.pemiluapp.ui_user.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val nik = sharedPreferences.getString("nik", "")
        val nikTxt = "Halo, $nik"

        binding.nik.text = nikTxt

        binding.btnInformasi.setOnClickListener { startActivity(Intent(this, InformasiPemilihanActivity::class.java)) }
        binding.btnFormEntry.setOnClickListener { startActivity(Intent(this, FormEntryActivity::class.java)) }
        binding.btnLihatData.setOnClickListener { startActivity(Intent(this, LihatDataActivity::class.java)) }
        binding.btnKeluar.setOnClickListener {
            showAlertDialog("Keluar Akun?", "Anda akan keluar dari akun sekarang.", "Keluar", this@MainActivity) {
                val editor = sharedPreferences.edit()
                editor.putBoolean("isLoggedIn", false)
                editor.apply()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

}