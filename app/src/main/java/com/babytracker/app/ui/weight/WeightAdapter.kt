package com.babytracker.app.ui.weight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.babytracker.app.data.entities.WeightEntry
import com.babytracker.app.databinding.ItemWeightBinding
import com.babytracker.app.utils.DateTimeUtils

class WeightAdapter(
    private val onEdit: (WeightEntry) -> Unit,
    private val onDelete: (WeightEntry) -> Unit
) : ListAdapter<WeightEntry, WeightAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val b: ItemWeightBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: WeightEntry) {
            b.tvDate.text = DateTimeUtils.formatDate(item.date)
            b.tvWeight.text = "%.3f кг".format(item.weightKg)
            if (item.note.isNotBlank()) {
                b.tvNote.text = item.note
                b.tvNote.visibility = View.VISIBLE
            } else {
                b.tvNote.visibility = View.GONE
            }
            b.btnEdit.setOnClickListener { onEdit(item) }
            b.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemWeightBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object DiffCallback : DiffUtil.ItemCallback<WeightEntry>() {
        override fun areItemsTheSame(a: WeightEntry, b: WeightEntry) = a.id == b.id
        override fun areContentsTheSame(a: WeightEntry, b: WeightEntry) = a == b
    }
}
