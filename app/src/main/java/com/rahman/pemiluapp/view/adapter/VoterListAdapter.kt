package com.rahman.pemiluapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.databinding.LayoutRecyclerviewVotersBinding

class VoterListAdapter(private var voterDataHandler: (VoterDataModel) -> Unit) : ListAdapter<VoterDataModel, VoterListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutRecyclerviewVotersBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), voterDataHandler)
    }

    class ViewHolder(private val binding: LayoutRecyclerviewVotersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: VoterDataModel, voterCallback: (VoterDataModel) -> Unit) {
            with(binding) {
                val context = root.context

                val drawable = if (model.jk == true) R.drawable.background_corner_99_men else R.drawable.background_corner_99_women
                val backgroundDrawable = ContextCompat.getDrawable(context, drawable)

                nik.background = backgroundDrawable
                nik.text = context.getString(R.string.nik_x, model.nik)
                dataName.text = model.nama?.ifEmpty { context.getString(R.string.data_not_found) }
                dataAddress.text = model.alamat?.ifEmpty { context.getString(R.string.data_not_found) }
                dataRegistrationDate.text = model.tanggal?.ifEmpty { context.getString(R.string.data_not_found) }

                Glide.with(context)
                    .load(model.gambar?.toUri())
                    .placeholder(R.drawable.img_broken_image)
                    .error(R.drawable.img_broken_image)
                    .into(evidencePhoto)

                root.setOnClickListener { voterCallback(model) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<VoterDataModel>() {
        override fun areItemsTheSame(oldItem: VoterDataModel, newItem: VoterDataModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: VoterDataModel, newItem: VoterDataModel): Boolean {
            return oldItem.nik == newItem.nik
        }
    }
}