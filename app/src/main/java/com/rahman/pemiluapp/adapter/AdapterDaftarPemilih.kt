package com.rahman.pemiluapp.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.databinding.LayoutRecyclerviewPemilihBinding
import com.rahman.pemiluapp.model.Pemilih
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdapterDaftarPemilih(
    private var modelList: ArrayList<Pemilih>,
    private var listenerPemilh: (Pemilih) -> Unit
) :
    RecyclerView.Adapter<AdapterDaftarPemilih.ViewHolder>() {
    lateinit var context: Context

    class ViewHolder(private val binding: LayoutRecyclerviewPemilihBinding) : RecyclerView.ViewHolder(binding.root) {
        fun data(model: Pemilih, listenerPemilh: (Pemilih) -> Unit, context: Context) {
            val jenisKelamin = if (model.jk == true) context.getString(R.string.laki_laki) else context.getString(R.string.perempuan)
            val backgroundDrawable =
                if (model.jk == true) ContextCompat.getDrawable(context, R.drawable.background_corner_99_men)
                else ContextCompat.getDrawable(context, R.drawable.background_corner_99_women)

            val selectedDateInMillis = model.tanggal!!.toLong()
            val date = Date(selectedDateInMillis)
            val sdf = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

            binding.nik.text = model.nik
            binding.nama.text = model.nama
            binding.nohp.text = model.nohp
            binding.jk.text = jenisKelamin
            binding.jk.background = backgroundDrawable

            binding.tangggal.text = sdf.format(date)
            binding.alamat.text = model.alamat

            Glide.with(context)
                .load(Uri.parse(model.gambar))
                .placeholder(R.drawable.img_broken_image)
                .error(R.drawable.img_broken_image)
                .into(binding.gambar)

            binding.llClicked.setOnClickListener { listenerPemilh(model) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding = LayoutRecyclerviewPemilihBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = modelList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.data(modelList[position], listenerPemilh, context)
    }
}