package com.rahman.pemiluapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.databinding.ActivityInformasiPemilihanBinding

class InformasiPemilihanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInformasiPemilihanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformasiPemilihanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.appBar.setNavigationOnClickListener { onBackPressed() }
        binding.btnBukaWeb.setOnClickListener {
            val url = getString(R.string.web_info_pemilu)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
}