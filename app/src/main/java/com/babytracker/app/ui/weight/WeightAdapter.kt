package com.babytracker.app.ui.weight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.babytracker.app.R
import com.babytracker.app.data.entities.WeightEntry
import com.babytracker.app.databinding.ItemWeightBinding
import com.babytracker.app.utils.DateTimeUtils

sealed class WeightListItem {
    data class Header(val dateStr: String) : WeightListItem()
    data class Item(val entry: WeightEntry) : WeightListItem()
}

class WeightAdapter(
    private val onEdit: (WeightEntry) -> Unit,
    private val onDelete: (WeightEntry) -> Unit
) : ListAdapter<WeightListItem, RecyclerView.ViewHolder>(DiffCallback) {

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHeader: TextView = view.findViewById(R.id.tv_header)
    }

    inner class ItemViewHolder(val b: ItemWeightBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(entry: WeightEntry) {
            b.tvDate.text = DateTimeUtils.formatDate(entry.date)
            b.tvWeight.text = "%.3f кг".format(entry.weightKg)
            if (entry.note.isNotBlank()) { b.tvNote.text = entry.note; b.tvNote.visibility = View.VISIBLE }
            else b.tvNote.visibility = View.GONE
            b.btnEdit.setOnClickListener { onEdit(entry) }
            b.btnDelete.setOnClickListener { onDelete(entry) }
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is WeightListItem.Header -> TYPE_HEADER
        is WeightListItem.Item -> TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false))
        } else {
            ItemViewHolder(ItemWeightBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is WeightListItem.Header -> (holder as HeaderViewHolder).tvHeader.text = item.dateStr
            is WeightListItem.Item -> (holder as ItemViewHolder).bind(item.entry)
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1

        val DiffCallback = object : DiffUtil.ItemCallback<WeightListItem>() {
            override fun areItemsTheSame(a: WeightListItem, b: WeightListItem) = when {
                a is WeightListItem.Header && b is WeightListItem.Header -> a.dateStr == b.dateStr
                a is WeightListItem.Item && b is WeightListItem.Item -> a.entry.id == b.entry.id
                else -> false
            }
            override fun areContentsTheSame(a: WeightListItem, b: WeightListItem) = a == b
        }

        fun buildGroupedList(entries: List<WeightEntry>): List<WeightListItem> {
            val result = mutableListOf<WeightListItem>()
            entries.groupBy { DateTimeUtils.formatDate(it.date) }.forEach { (date, items) ->
                result.add(WeightListItem.Header(date))
                items.forEach { result.add(WeightListItem.Item(it)) }
            }
            return result
        }
    }
}
