package com.rahman.pemiluapp.ui_user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.rahman.pemiluapp.databinding.ActivityRegisterBinding
import com.rahman.pemiluapp.dbhelper.UserDatabaseHelper
import com.rahman.pemiluapp.ui.MainActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val userDbHelper = UserDatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnRegister.setOnClickListener {
            val nik = binding.nikDaftar.text.toString().trim()
            val password = binding.passwordDaftar.text.toString().trim()
            val konfPassword = binding.konfirmasiPasswordDaftar.text.toString().trim()

            if (nik.isNotEmpty() && password.isNotEmpty() && konfPassword.isNotEmpty())
                if (password == konfPassword) {
                    if (!userDbHelper.isNikExist(nik))registerData(nik, password)
                    else Toast.makeText(this, "Akun dengan NIK tersebut sudah terdaftar", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(this, "Password berbeda", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this, "Harap isi semua bidang", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerData(nik: String, password: String) {
        userDbHelper.addUser(nik, password)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        Toast.makeText(this, "Berhasil mendaftarkan akun, silahkan login ulang", Toast.LENGTH_SHORT).show()
    }
}