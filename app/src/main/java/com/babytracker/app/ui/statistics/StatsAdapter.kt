package com.babytracker.app.ui.statistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.babytracker.app.R
import com.babytracker.app.data.entities.FeedingSession
import com.babytracker.app.data.entities.SleepSession
import com.babytracker.app.databinding.ItemSessionBinding
import com.babytracker.app.utils.DateTimeUtils

sealed class StatsListItem {
    data class SectionHeader(val title: String) : StatsListItem()
    data class FeedingItem(val session: FeedingSession) : StatsListItem()
    data class SleepItem(val session: SleepSession) : StatsListItem()
    object Empty : StatsListItem()
}

class StatsAdapter : ListAdapter<StatsListItem, RecyclerView.ViewHolder>(DiffCallback) {

    inner class HeaderVH(view: View) : RecyclerView.ViewHolder(view) {
        val tv: TextView = view.findViewById(R.id.tv_header)
    }

    inner class SessionVH(val b: ItemSessionBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(start: Long, end: Long, note: String) {
            b.tvDate.text = DateTimeUtils.formatTime(start)
            b.tvTimeRange.text = "${DateTimeUtils.formatTime(start)} — ${DateTimeUtils.formatTime(end)}"
            b.tvDuration.text = DateTimeUtils.formatDuration(start, end)
            if (note.isNotBlank()) { b.tvNote.text = note; b.tvNote.visibility = View.VISIBLE }
            else b.tvNote.visibility = View.GONE
            b.btnEdit.visibility = View.GONE
            b.btnDelete.visibility = View.GONE
        }
    }

    inner class EmptyVH(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is StatsListItem.SectionHeader -> 0
        is StatsListItem.FeedingItem -> 1
        is StatsListItem.SleepItem -> 1
        is StatsListItem.Empty -> 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> HeaderVH(inflater.inflate(R.layout.item_date_header, parent, false))
            1 -> SessionVH(ItemSessionBinding.inflate(inflater, parent, false))
            else -> EmptyVH(inflater.inflate(R.layout.item_empty, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is StatsListItem.SectionHeader -> (holder as HeaderVH).tv.text = item.title
            is StatsListItem.FeedingItem -> (holder as SessionVH).bind(item.session.startTime, item.session.endTime, item.session.note)
            is StatsListItem.SleepItem -> (holder as SessionVH).bind(item.session.startTime, item.session.endTime, item.session.note)
            is StatsListItem.Empty -> {}
        }
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<StatsListItem>() {
            override fun areItemsTheSame(a: StatsListItem, b: StatsListItem) = when {
                a is StatsListItem.SectionHeader && b is StatsListItem.SectionHeader -> a.title == b.title
                a is StatsListItem.FeedingItem && b is StatsListItem.FeedingItem -> a.session.id == b.session.id
                a is StatsListItem.SleepItem && b is StatsListItem.SleepItem -> a.session.id == b.session.id
                a is StatsListItem.Empty && b is StatsListItem.Empty -> true
                else -> false
            }
            override fun areContentsTheSame(a: StatsListItem, b: StatsListItem) = a == b
        }
    }
}
