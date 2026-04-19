package com.babytracker.app.ui.weight

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.babytracker.app.R
import com.babytracker.app.data.entities.WeightEntry
import com.babytracker.app.databinding.DialogWeightBinding
import com.babytracker.app.databinding.FragmentWeightBinding
import com.babytracker.app.ui.common.DateTimePickerHelper
import com.babytracker.app.utils.DateTimeUtils
import com.babytracker.app.viewmodel.WeightViewModel
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter

class WeightFragment : Fragment() {

    private var _binding: FragmentWeightBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeightViewModel by viewModels()
    private lateinit var adapter: WeightAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WeightAdapter(
            onEdit = { showWeightDialog(it) },
            onDelete = { confirmDelete(it) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.entries.observe(viewLifecycleOwner) { entries ->
            adapter.submitList(entries)
            updateChart(entries)
        }

        binding.btnAddWeight.setOnClickListener { showWeightDialog(null) }

        setupChart()
    }

    private fun setupChart() {
        binding.weightChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            legend.isEnabled = false
            axisRight.isEnabled = false
        }
    }

    private fun updateChart(entries: List<WeightEntry>) {
        val sorted = entries.sortedBy { it.date }
        val dataEntries = sorted.mapIndexed { i, e -> Entry(i.toFloat(), e.weightKg.toFloat()) }
        if (dataEntries.isEmpty()) {
            binding.weightChart.clear()
            return
        }
        val dataSet = LineDataSet(dataEntries, "").apply {
            color = resources.getColor(R.color.purple_500, null)
            setCircleColor(resources.getColor(R.color.purple_500, null))
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(true)
        }
        binding.weightChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val idx = value.toInt()
                return if (idx in sorted.indices) DateTimeUtils.formatDate(sorted[idx].date) else ""
            }
        }
        binding.weightChart.data = LineData(dataSet)
        binding.weightChart.invalidate()
    }

    private fun showWeightDialog(existing: WeightEntry?) {
        val dialogBinding = DialogWeightBinding.inflate(layoutInflater)
        var selectedDate = existing?.date ?: System.currentTimeMillis()

        dialogBinding.tvDialogTitle.text = if (existing == null) getString(R.string.add_weight) else getString(R.string.edit_weight)
        dialogBinding.btnDate.text = DateTimeUtils.formatDate(selectedDate)
        dialogBinding.etWeight.setText(existing?.weightKg?.toString() ?: "")
        dialogBinding.etNote.setText(existing?.note ?: "")

        dialogBinding.btnDate.setOnClickListener {
            DateTimePickerHelper.pickDate(requireContext(), selectedDate) {
                selectedDate = it
                dialogBinding.btnDate.text = DateTimeUtils.formatDate(it)
            }
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                val weightStr = dialogBinding.etWeight.text?.toString()
                val weight = weightStr?.toDoubleOrNull()
                if (weight == null || weight <= 0) {
                    Toast.makeText(requireContext(), R.string.invalid_weight, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val note = dialogBinding.etNote.text?.toString() ?: ""
                if (existing == null) {
                    viewModel.add(selectedDate, weight, note)
                } else {
                    viewModel.update(existing.copy(date = selectedDate, weightKg = weight, note = note))
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun confirmDelete(entry: WeightEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_confirm_title)
            .setMessage(R.string.delete_confirm_message)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.delete(entry) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
