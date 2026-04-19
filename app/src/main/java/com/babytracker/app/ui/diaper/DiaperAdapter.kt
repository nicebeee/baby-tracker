package com.babytracker.app.ui.diaper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.babytracker.app.data.entities.DiaperEntry
import com.babytracker.app.data.entities.DiaperType
import com.babytracker.app.databinding.ItemDiaperBinding
import com.babytracker.app.utils.DateTimeUtils

class DiaperAdapter(
    private val onEdit: (DiaperEntry) -> Unit,
    private val onDelete: (DiaperEntry) -> Unit
) : ListAdapter<DiaperEntry, DiaperAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val b: ItemDiaperBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: DiaperEntry) {
            b.tvDatetime.text = DateTimeUtils.formatDateTime(item.dateTime)
            b.tvType.text = when (item.type) {
                DiaperType.WET -> "Мокрый"
                DiaperType.DIRTY -> "Грязный"
                DiaperType.BOTH -> "Оба"
            }
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
        return ViewHolder(ItemDiaperBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object DiffCallback : DiffUtil.ItemCallback<DiaperEntry>() {
        override fun areItemsTheSame(a: DiaperEntry, b: DiaperEntry) = a.id == b.id
        override fun areContentsTheSame(a: DiaperEntry, b: DiaperEntry) = a == b
    }
}
