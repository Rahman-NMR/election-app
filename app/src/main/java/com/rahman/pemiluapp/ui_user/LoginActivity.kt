package com.rahman.pemiluapp.ui_user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rahman.pemiluapp.databinding.ActivityLoginBinding
import com.rahman.pemiluapp.dbhelper.UserDatabaseHelper
import com.rahman.pemiluapp.ui.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val userDbHelper = UserDatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnDaftarBaru.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        binding.btnLogin.setOnClickListener {
            val nik = binding.nikLogin.text.toString().trim()
            val password = binding.passwordLogin.text.toString().trim()

            if (nik.isNotEmpty() && password.isNotEmpty()) {
                loginUser(nik, password)
            } else Toast.makeText(this, "Harap isi NIK dan password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser(nik: String, password: String) {
        if (userDbHelper.getUser(nik, password)) {
            val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString("nik", nik)
            editor.putBoolean("isLoggedIn", true)
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else Toast.makeText(this, "NIK atau passoword salah", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}