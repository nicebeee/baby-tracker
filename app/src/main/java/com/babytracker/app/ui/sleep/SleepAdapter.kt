package com.babytracker.app.ui.sleep

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.babytracker.app.R
import com.babytracker.app.data.entities.SleepSession
import com.babytracker.app.databinding.ItemSessionBinding
import com.babytracker.app.utils.DateTimeUtils

sealed class SleepListItem {
    data class Header(val dateStr: String) : SleepListItem()
    data class Item(val session: SleepSession) : SleepListItem()
}

class SleepAdapter(
    private val onEdit: (SleepSession) -> Unit,
    private val onDelete: (SleepSession) -> Unit
) : ListAdapter<SleepListItem, RecyclerView.ViewHolder>(DiffCallback) {

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHeader: TextView = view.findViewById(R.id.tv_header)
    }

    inner class ItemViewHolder(val b: ItemSessionBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(session: SleepSession) {
            b.tvDate.text = DateTimeUtils.formatDate(session.startTime)
            b.tvTimeRange.text = "${DateTimeUtils.formatTime(session.startTime)} — ${DateTimeUtils.formatTime(session.endTime)}"
            b.tvDuration.text = DateTimeUtils.formatDuration(session.startTime, session.endTime)
            if (session.note.isNotBlank()) { b.tvNote.text = session.note; b.tvNote.visibility = View.VISIBLE }
            else b.tvNote.visibility = View.GONE
            b.btnEdit.setOnClickListener { onEdit(session) }
            b.btnDelete.setOnClickListener { onDelete(session) }
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is SleepListItem.Header -> TYPE_HEADER
        is SleepListItem.Item -> TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false))
        } else {
            ItemViewHolder(ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is SleepListItem.Header -> (holder as HeaderViewHolder).tvHeader.text = item.dateStr
            is SleepListItem.Item -> (holder as ItemViewHolder).bind(item.session)
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1

        val DiffCallback = object : DiffUtil.ItemCallback<SleepListItem>() {
            override fun areItemsTheSame(a: SleepListItem, b: SleepListItem) = when {
                a is SleepListItem.Header && b is SleepListItem.Header -> a.dateStr == b.dateStr
                a is SleepListItem.Item && b is SleepListItem.Item -> a.session.id == b.session.id
                else -> false
            }
            override fun areContentsTheSame(a: SleepListItem, b: SleepListItem) = a == b
        }

        fun buildGroupedList(sessions: List<SleepSession>): List<SleepListItem> {
            val result = mutableListOf<SleepListItem>()
            sessions.groupBy { DateTimeUtils.formatDate(it.startTime) }.forEach { (date, items) ->
                result.add(SleepListItem.Header(date))
                items.forEach { result.add(SleepListItem.Item(it)) }
            }
            return result
        }
    }
}
