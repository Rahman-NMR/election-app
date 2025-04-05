package com.rahman.pemiluapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.MaterialDatePicker
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.databinding.ActivityFormEntryBinding
import com.rahman.pemiluapp.dbhelper.DatabaseHelper
import com.rahman.pemiluapp.model.Pemilih
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FormEntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormEntryBinding
    private lateinit var latLang: FusedLocationProviderClient
    private lateinit var takePicLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private val dbHelper = DatabaseHelper(this)
    private var imageUri: Uri = Uri.parse("")
    private var timestamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        latLang = LocationServices.getFusedLocationProviderClient(this)
        takePicLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap?
                setImgBitmap(imageBitmap)
            }
        }
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    val inputStream = contentResolver.openInputStream(selectedImageUri)
                    val timestamp = System.currentTimeMillis()
                    val fileName = "galeri$timestamp.jpg"
                    val destinationFile = File(cacheDir, fileName)
                    inputStream?.use { input ->
                        destinationFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    loadImageWithGlide(Uri.fromFile(destinationFile), binding.gambar)
                    Log.e("testData", Uri.fromFile(destinationFile).toString())
                }
            }
        }

        binding.appBar.setNavigationOnClickListener { onBackPressed() }
        binding.btnDate.setOnClickListener { selectDate() }
        binding.btnCekLokasi.setOnClickListener { locationPermission() }
        binding.tvLokasiSekarang.setOnLongClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(binding.tvLokasiSekarang.text, binding.tvLokasiSekarang.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Alamat sekarang berhasil disalin", Toast.LENGTH_SHORT).show()
            true
        }
        binding.btnOpenCam.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 3655)
            } else dispatchTakePicIntent()
        }
        binding.btnOpenGalery.setOnClickListener { dispatchPickImageIntent() }

        binding.btnSubmit.setOnClickListener { notNullChecker() }
    }

    private fun selectDate() {
        val materialDatePicker = MaterialDatePicker.Builder.datePicker()
        materialDatePicker.setTitleText("Select date")
        val datePicker = materialDatePicker.build()

        datePicker.show(supportFragmentManager, datePicker.toString())
        datePicker.addOnPositiveButtonClickListener {
            val selectedDateInMillis = it
            val date = Date(selectedDateInMillis)
            val sdf = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
            timestamp = selectedDateInMillis

            binding.tanggal.setText(sdf.format(date))
        }
    }

    private fun loadImageWithGlide(imageUri: Uri?, image: ImageView) {
        val requestOptions = RequestOptions().centerInside()
        Glide.with(this).load(imageUri).apply(requestOptions).into(image)
        this.imageUri = imageUri!!
        image.visibility = View.VISIBLE
        binding.tvGambar.visibility = View.GONE
    }

    private fun setImgBitmap(imageBitmap: Bitmap?) {
        val tempFile = File.createTempFile("image", ".png", cacheDir)
        val outputStream = FileOutputStream(tempFile)
        imageBitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()
        imageUri = Uri.fromFile(tempFile)

        binding.tvGambar.visibility = View.GONE
        binding.gambar.visibility = View.VISIBLE
        binding.gambar.setImageURI(imageUri)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePicIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) takePicLauncher.launch(takePictureIntent)
    }

    private fun dispatchPickImageIntent() {
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(pickImageIntent)
    }

    private fun locationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 45321)
            return
        }

        latLang.lastLocation.addOnSuccessListener {
            if (it != null) {
                val lat = it.latitude
                val lang = it.longitude
                val geoCoder = Geocoder(this, Locale.getDefault())
                val adress = geoCoder.getFromLocation(lat, lang, 10)
                val namaJalan = if (adress!![0].thoroughfare != null) "${adress[0].thoroughfare}," else ""
                val noRumah = if (adress[0].subThoroughfare != null) "${adress[0].subThoroughfare}," else ""
                val komplek = if (adress[0].subLocality != null) "${adress[0].subLocality}," else ""
                val camatKel = if (adress[0].locality != null) "${adress[0].locality}," else ""
                val kotaKab = if (adress[0].subAdminArea != null) "${adress[0].subAdminArea}," else ""
                val provinsi = if (adress[0].adminArea != null) "${adress[0].adminArea}," else ""
                val kodePos = if (adress[0].postalCode != null) adress[0].postalCode else ""

                val fullAlamat = "$namaJalan $noRumah $komplek $camatKel $kotaKab $provinsi $kodePos"
                binding.tvLokasiSekarang.text = fullAlamat
                binding.tvLokasiSekarang.visibility = View.VISIBLE
            }
        }
    }

    private fun notNullChecker() {
        val nik = binding.nik.text.toString().trim()
        val nama = binding.nama.text.toString().trim()
        val nohp = binding.nohp.text.toString().trim()
        val tanggal = binding.tanggal.text.toString().trim()
        val alamat = binding.alamat.text.toString().trim()
        val gender = binding.radioGroup.checkedRadioButtonId
        if (nik.isNotEmpty() && nama.isNotEmpty() && nohp.isNotEmpty() && tanggal.isNotEmpty() && alamat.isNotEmpty()
            && imageUri != Uri.parse("") && gender != -1
        ) saveData(nik, nama, nohp, alamat)
        else Toast.makeText(this, "Harap isi semua bidang", Toast.LENGTH_SHORT).show()
    }

    private fun saveData(nik: String, nama: String, nohp: String, alamat: String) {
        val gender = binding.radioGroup.checkedRadioButtonId == R.id.cowo
        val pemilih = Pemilih(nik, nama, nohp, gender, timestamp.toString(), alamat, imageUri.toString())

        val existingPemilih = dbHelper.getPemilih(nik)
        if (existingPemilih != null) {
            val intent = Intent(this, DataPemilihActivity::class.java)
                .putExtra("nik", "${existingPemilih.nik}").putExtra("isExist", true)
            startActivity(intent)
            Toast.makeText(this, "NIK sudah terdaftar", Toast.LENGTH_SHORT).show()
        } else {
            val idTambah = dbHelper.tambahPemilih(pemilih)
            if (idTambah != -1L) {
                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            } else Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
        }
    }
}