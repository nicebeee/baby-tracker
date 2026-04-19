package com.babytracker.app.ui.feeding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.babytracker.app.data.entities.FeedingSession
import com.babytracker.app.databinding.ItemSessionBinding
import com.babytracker.app.utils.DateTimeUtils
import android.view.View

class FeedingAdapter(
    private val onEdit: (FeedingSession) -> Unit,
    private val onDelete: (FeedingSession) -> Unit
) : ListAdapter<FeedingSession, FeedingAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val b: ItemSessionBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: FeedingSession) {
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

    companion object DiffCallback : DiffUtil.ItemCallback<FeedingSession>() {
        override fun areItemsTheSame(a: FeedingSession, b: FeedingSession) = a.id == b.id
        override fun areContentsTheSame(a: FeedingSession, b: FeedingSession) = a == b
    }
}
