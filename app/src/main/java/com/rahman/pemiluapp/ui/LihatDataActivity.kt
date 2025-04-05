package com.rahman.pemiluapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rahman.pemiluapp.adapter.AdapterDaftarPemilih
import com.rahman.pemiluapp.databinding.ActivityLihatDataBinding
import com.rahman.pemiluapp.dbhelper.DatabaseHelper
import com.rahman.pemiluapp.model.Pemilih

class LihatDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLihatDataBinding
    private lateinit var adapterDaftarPemilih: AdapterDaftarPemilih
    private val dbHelper = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLihatDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.appBar.setNavigationOnClickListener { onBackPressed() }
        binding.rvPemilih.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        setupAdapter()
    }

    private fun setupAdapter() {
        val pemilihList = dbHelper.getAllPemilih()

        adapterDaftarPemilih = AdapterDaftarPemilih(pemilihList as ArrayList<Pemilih>) { data ->
            val intent = Intent(this, DataPemilihActivity::class.java)
                .putExtra("pemilihData", data).putExtra("nik", "${data.nik}")
            startActivity(intent)
        }
        binding.rvPemilih.adapter = adapterDaftarPemilih
        pemilihList.sortBy { it.tanggal }

        if (adapterDaftarPemilih.itemCount == 0) {
            binding.rvKosong.visibility = View.VISIBLE
            binding.rvPemilih.visibility = View.GONE
        } else {
            binding.rvKosong.visibility = View.GONE
            binding.rvPemilih.visibility = View.VISIBLE
        }
    }

    override fun onRestart() {
        super.onRestart()
        setupAdapter()
    }
}