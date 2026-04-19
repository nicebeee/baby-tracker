package com.babytracker.app.ui.diaper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.babytracker.app.R
import com.babytracker.app.data.entities.DiaperEntry
import com.babytracker.app.data.entities.DiaperType
import com.babytracker.app.databinding.ItemDiaperBinding
import com.babytracker.app.utils.DateTimeUtils

sealed class DiaperListItem {
    data class Header(val dateStr: String) : DiaperListItem()
    data class Item(val entry: DiaperEntry) : DiaperListItem()
}

class DiaperAdapter(
    private val onEdit: (DiaperEntry) -> Unit,
    private val onDelete: (DiaperEntry) -> Unit
) : ListAdapter<DiaperListItem, RecyclerView.ViewHolder>(DiffCallback) {

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHeader: TextView = view.findViewById(R.id.tv_header)
    }

    inner class ItemViewHolder(val b: ItemDiaperBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(entry: DiaperEntry) {
            b.tvDatetime.text = DateTimeUtils.formatDateTime(entry.dateTime)
            b.tvType.text = when (entry.type) {
                DiaperType.WET -> "Мокрый"
                DiaperType.DIRTY -> "Грязный"
                DiaperType.BOTH -> "Оба"
            }
            if (entry.note.isNotBlank()) { b.tvNote.text = entry.note; b.tvNote.visibility = View.VISIBLE }
            else b.tvNote.visibility = View.GONE
            b.btnEdit.setOnClickListener { onEdit(entry) }
            b.btnDelete.setOnClickListener { onDelete(entry) }
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is DiaperListItem.Header -> TYPE_HEADER
        is DiaperListItem.Item -> TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false))
        } else {
            ItemViewHolder(ItemDiaperBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is DiaperListItem.Header -> (holder as HeaderViewHolder).tvHeader.text = item.dateStr
            is DiaperListItem.Item -> (holder as ItemViewHolder).bind(item.entry)
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1

        val DiffCallback = object : DiffUtil.ItemCallback<DiaperListItem>() {
            override fun areItemsTheSame(a: DiaperListItem, b: DiaperListItem) = when {
                a is DiaperListItem.Header && b is DiaperListItem.Header -> a.dateStr == b.dateStr
                a is DiaperListItem.Item && b is DiaperListItem.Item -> a.entry.id == b.entry.id
                else -> false
            }
            override fun areContentsTheSame(a: DiaperListItem, b: DiaperListItem) = a == b
        }

        fun buildGroupedList(entries: List<DiaperEntry>): List<DiaperListItem> {
            val result = mutableListOf<DiaperListItem>()
            entries.groupBy { DateTimeUtils.formatDate(it.dateTime) }.forEach { (date, items) ->
                result.add(DiaperListItem.Header(date))
                items.forEach { result.add(DiaperListItem.Item(it)) }
            }
            return result
        }
    }
}
