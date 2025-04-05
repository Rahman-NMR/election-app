package com.rahman.pemiluapp.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.rahman.pemiluapp.AlertDialogC
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.databinding.ActivityDataPemilihBinding
import com.rahman.pemiluapp.dbhelper.DatabaseHelper
import com.rahman.pemiluapp.model.Pemilih
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataPemilihActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataPemilihBinding
    private val dbHelper = DatabaseHelper(this)
    private lateinit var onePemilih: Pemilih
    private var dataNik: String = ""
    private var isExist: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataPemilihBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        onePemilih = intent.getParcelableExtra("pemilihData") ?: Pemilih()
        dataNik = intent.getStringExtra("nik") ?: ""
        isExist = intent.getBooleanExtra("isExist", true)
        val pemilih = if (!isExist) onePemilih else dbHelper.getPemilih(dataNik)

        with(binding) {
            appBar.setNavigationOnClickListener { onBackPressed() }
            appBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_delete -> {
                        if (pemilih != null) {
                            AlertDialogC.showAlertDialog(
                                "Hapus data pemilih?",
                                "Data pemilih akan dihapus dari database.",
                                "Hapus",
                                this@DataPemilihActivity
                            ) {
                                dbHelper.deletePemilihByNik(pemilih.nik.toString())
                                Toast.makeText(this@DataPemilihActivity, "Data pemilih ${pemilih.nik} dihapus", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        } else Toast.makeText(this@DataPemilihActivity, "Data pemilih tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }

            if (pemilih != null) {
                nikPemilih.text = pemilih.nik
                namaPemilih.text = pemilih.nama
                nohpPemilih.text = pemilih.nohp
                jkPemilih.text = if (pemilih.jk == true) getString(R.string.laki_laki) else getString(R.string.perempuan)
                val selectedDateInMillis = pemilih.tanggal!!.toLong()
                val date = Date(selectedDateInMillis)
                val sdf = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
                tanggalPemilih.text = sdf.format(date)
                alamatPemilih.text = pemilih.alamat

                Glide.with(this@DataPemilihActivity)
                    .load(Uri.parse(pemilih.gambar))
                    .placeholder(R.drawable.img_broken_image)
                    .error(R.drawable.img_broken_image)
                    .into(gambarPemilihNotnull)
                gambarPemilihNotnull.visibility = View.VISIBLE
                gambarPemilih.text = ""
            } else {
                gambarPemilihNotnull.visibility = View.GONE
                gambarPemilih.text = getString(R.string.strip)
            }
        }
    }
}