package com.babytracker.app.ui.feeding

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.babytracker.app.R
import com.babytracker.app.databinding.DialogSessionBinding
import com.babytracker.app.databinding.FragmentFeedingBinding
import com.babytracker.app.data.entities.FeedingSession
import com.babytracker.app.ui.common.DateTimePickerHelper
import com.babytracker.app.utils.DateTimeUtils
import com.babytracker.app.viewmodel.FeedingViewModel

class FeedingFragment : Fragment() {

    private var _binding: FragmentFeedingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FeedingViewModel by viewModels()
    private lateinit var adapter: FeedingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFeedingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FeedingAdapter(
            onEdit = { showSessionDialog(it) },
            onDelete = { confirmDelete(it) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.sessions.observe(viewLifecycleOwner) { sessions ->
            adapter.submitList(FeedingAdapter.buildGroupedList(sessions))
            updateTodayStats(sessions)
        }

        viewModel.isRunning.observe(viewLifecycleOwner) { running ->
            binding.btnToggleTimer.text = if (running) getString(R.string.stop_feeding) else getString(R.string.start_feeding)
        }

        viewModel.elapsedMillis.observe(viewLifecycleOwner) { millis ->
            binding.tvTimer.text = DateTimeUtils.formatTimer(millis)
        }

        binding.btnToggleTimer.setOnClickListener {
            if (viewModel.isRunning.value == true) viewModel.stopTimer()
            else viewModel.startTimer()
        }

        binding.btnAddManual.setOnClickListener { showSessionDialog(null) }
    }

    private fun showSessionDialog(existing: FeedingSession?) {
        val dialogBinding = DialogSessionBinding.inflate(layoutInflater)
        var startMillis = existing?.startTime ?: System.currentTimeMillis()
        var endMillis = existing?.endTime ?: System.currentTimeMillis()

        dialogBinding.tvDialogTitle.text = if (existing == null) getString(R.string.add_session) else getString(R.string.edit_session)
        dialogBinding.btnStartTime.text = DateTimeUtils.formatDateTime(startMillis)
        dialogBinding.btnEndTime.text = DateTimeUtils.formatDateTime(endMillis)
        dialogBinding.etNote.setText(existing?.note ?: "")

        dialogBinding.btnStartTime.setOnClickListener {
            DateTimePickerHelper.pickDateTime(requireContext(), startMillis) {
                startMillis = it
                dialogBinding.btnStartTime.text = DateTimeUtils.formatDateTime(it)
            }
        }
        dialogBinding.btnEndTime.setOnClickListener {
            DateTimePickerHelper.pickDateTime(requireContext(), endMillis) {
                endMillis = it
                dialogBinding.btnEndTime.text = DateTimeUtils.formatDateTime(it)
            }
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                val note = dialogBinding.etNote.text?.toString() ?: ""
                if (existing == null) {
                    viewModel.addManual(startMillis, endMillis, note)
                } else {
                    viewModel.update(existing.copy(startTime = startMillis, endTime = endMillis, note = note))
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun updateTodayStats(sessions: List<com.babytracker.app.data.entities.FeedingSession>) {
        val todayStart = com.babytracker.app.viewmodel.StatisticsViewModel.startOfDay(System.currentTimeMillis())
        val todaySessions = sessions.filter { it.startTime >= todayStart }
        val count = todaySessions.size
        val totalMs = todaySessions.sumOf { it.endTime - it.startTime }
        val statsView = binding.todayStats
        statsView.findViewById<android.widget.TextView>(com.babytracker.app.R.id.tv_today_count).text =
            "$count ${if (count == 1) "раз" else "раз"}"
        statsView.findViewById<android.widget.TextView>(com.babytracker.app.R.id.tv_today_duration).text =
            formatDur(totalMs)
    }

    private fun formatDur(ms: Long): String {
        if (ms <= 0) return "0 мин"
        val h = ms / 3_600_000; val m = (ms % 3_600_000) / 60_000
        return if (h > 0) "$h ч $m мин" else "$m мин"
    }

    private fun confirmDelete(session: FeedingSession) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_confirm_title)
            .setMessage(R.string.delete_confirm_message)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.delete(session) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
