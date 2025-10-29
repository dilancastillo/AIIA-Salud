package com.aiia.hospital.aiia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aiia.hospital.aiia.databinding.ItemPatientBinding

class PatientAdapter(
    private val items: List<Patient>,
    private val onClick: (Patient) -> Unit
) : RecyclerView.Adapter<PatientAdapter.VH>() {

    inner class VH(val binding: ItemPatientBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Patient) {
            binding.tvName.text = item.name
            binding.imgAvatar.setImageResource(item.photoRes)
            binding.root.setOnClickListener { onClick(item) }
            binding.imgAvatar.contentDescription = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inf = LayoutInflater.from(parent.context)
        val binding = ItemPatientBinding.inflate(inf, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size
}