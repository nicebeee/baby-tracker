package com.babytracker.app.ui.statistics

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.babytracker.app.data.entities.FeedingSession
import com.babytracker.app.data.entities.SleepSession
import com.babytracker.app.databinding.FragmentStatisticsBinding
import com.babytracker.app.utils.DateTimeUtils
import com.babytracker.app.viewmodel.StatisticsViewModel
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var adapter: StatsAdapter

    private val dayFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
    private val todayFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StatsAdapter()
        binding.recyclerStats.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerStats.adapter = adapter

        binding.btnPrevDay.setOnClickListener { viewModel.previousDay() }
        binding.btnNextDay.setOnClickListener { viewModel.nextDay() }

        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            val label = if (viewModel.isToday()) "Сегодня, ${dayFormat.format(Date(date))}"
                        else dayFormat.format(Date(date))
            binding.tvSelectedDate.text = label
            binding.btnNextDay.isEnabled = !viewModel.isToday()
            binding.btnNextDay.alpha = if (viewModel.isToday()) 0.3f else 1f
        }

        viewModel.feedings.observe(viewLifecycleOwner) { feedings ->
            val sleeps = viewModel.sleeps.value ?: emptyList()
            updateStats(feedings, sleeps)
        }

        viewModel.sleeps.observe(viewLifecycleOwner) { sleeps ->
            val feedings = viewModel.feedings.value ?: emptyList()
            updateStats(feedings, sleeps)
        }
    }

    private fun updateStats(feedings: List<FeedingSession>, sleeps: List<SleepSession>) {
        // Сводка — кормление
        val feedingCount = feedings.size
        val feedingTotalMs = feedings.sumOf { it.endTime - it.startTime }
        binding.tvFeedingCount.text = pluralTimes(feedingCount)
        binding.tvFeedingDuration.text = formatTotalDuration(feedingTotalMs)

        // Сводка — сон
        val sleepCount = sleeps.size
        val sleepTotalMs = sleeps.sumOf { it.endTime - it.startTime }
        binding.tvSleepCount.text = pluralTimes(sleepCount)
        binding.tvSleepDuration.text = formatTotalDuration(sleepTotalMs)

        // История дня
        val items = mutableListOf<StatsListItem>()

        if (feedings.isEmpty() && sleeps.isEmpty()) {
            items.add(StatsListItem.Empty)
        } else {
            if (feedings.isNotEmpty()) {
                items.add(StatsListItem.SectionHeader("Кормление"))
                feedings.forEach { items.add(StatsListItem.FeedingItem(it)) }
            }
            if (sleeps.isNotEmpty()) {
                items.add(StatsListItem.SectionHeader("Сон"))
                sleeps.forEach { items.add(StatsListItem.SleepItem(it)) }
            }
        }

        adapter.submitList(items)
    }

    private fun pluralTimes(count: Int): String {
        val suffix = when {
            count % 100 in 11..19 -> "раз"
            count % 10 == 1 -> "раз"
            count % 10 in 2..4 -> "раза"
            else -> "раз"
        }
        return "$count $suffix"
    }

    private fun formatTotalDuration(ms: Long): String {
        if (ms <= 0) return "0 мин"
        val hours = ms / 3_600_000
        val minutes = (ms % 3_600_000) / 60_000
        return when {
            hours > 0 && minutes > 0 -> "$hours ч $minutes мин"
            hours > 0 -> "$hours ч"
            else -> "$minutes мин"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
