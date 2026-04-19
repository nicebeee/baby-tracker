package com.babytracker.app.ui.sleep

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.babytracker.app.data.entities.SleepSession
import com.babytracker.app.databinding.ItemSessionBinding
import com.babytracker.app.utils.DateTimeUtils

class SleepAdapter(
    private val onEdit: (SleepSession) -> Unit,
    private val onDelete: (SleepSession) -> Unit
) : ListAdapter<SleepSession, SleepAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val b: ItemSessionBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: SleepSession) {
            b.tvDate.text = DateTimeUtils.formatDate(item.startTime)
            b.tvTimeRange.text = "${DateTimeUtils.formatTime(item.startTime)} — ${DateTimeUtils.formatTime(item.endTime)}"
            b.tvDuration.text = DateTimeUtils.formatDuration(item.startTime, item.endTime)
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
        return ViewHolder(ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object DiffCallback : DiffUtil.ItemCallback<SleepSession>() {
        override fun areItemsTheSame(a: SleepSession, b: SleepSession) = a.id == b.id
        override fun areContentsTheSame(a: SleepSession, b: SleepSession) = a == b
    }
}
