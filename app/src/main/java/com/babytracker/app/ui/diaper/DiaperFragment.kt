package com.babytracker.app.ui.diaper

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.babytracker.app.R
import com.babytracker.app.data.entities.DiaperEntry
import com.babytracker.app.data.entities.DiaperType
import com.babytracker.app.databinding.DialogDiaperBinding
import com.babytracker.app.databinding.FragmentDiaperBinding
import com.babytracker.app.ui.common.DateTimePickerHelper
import com.babytracker.app.utils.DateTimeUtils
import com.babytracker.app.viewmodel.DiaperViewModel

class DiaperFragment : Fragment() {

    private var _binding: FragmentDiaperBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DiaperViewModel by viewModels()
    private lateinit var adapter: DiaperAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDiaperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DiaperAdapter(
            onEdit = { showDiaperDialog(it) },
            onDelete = { confirmDelete(it) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.entries.observe(viewLifecycleOwner) { adapter.submitList(it) }

        binding.btnAddDiaper.setOnClickListener { showDiaperDialog(null) }
    }

    private fun showDiaperDialog(existing: DiaperEntry?) {
        val dialogBinding = DialogDiaperBinding.inflate(layoutInflater)
        var selectedDateTime = existing?.dateTime ?: System.currentTimeMillis()

        dialogBinding.tvDialogTitle.text = if (existing == null) getString(R.string.add_diaper) else getString(R.string.edit_diaper)
        dialogBinding.btnDatetime.text = DateTimeUtils.formatDateTime(selectedDateTime)
        dialogBinding.etNote.setText(existing?.note ?: "")

        when (existing?.type) {
            DiaperType.DIRTY -> dialogBinding.rbDirty.isChecked = true
            DiaperType.BOTH -> dialogBinding.rbBoth.isChecked = true
            else -> dialogBinding.rbWet.isChecked = true
        }

        dialogBinding.btnDatetime.setOnClickListener {
            DateTimePickerHelper.pickDateTime(requireContext(), selectedDateTime) {
                selectedDateTime = it
                dialogBinding.btnDatetime.text = DateTimeUtils.formatDateTime(it)
            }
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                val type = when (dialogBinding.radioGroupType.checkedRadioButtonId) {
                    R.id.rb_dirty -> DiaperType.DIRTY
                    R.id.rb_both -> DiaperType.BOTH
                    else -> DiaperType.WET
                }
                val note = dialogBinding.etNote.text?.toString() ?: ""
                if (existing == null) {
                    viewModel.add(selectedDateTime, type, note)
                } else {
                    viewModel.update(existing.copy(dateTime = selectedDateTime, type = type, note = note))
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun confirmDelete(entry: DiaperEntry) {
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
