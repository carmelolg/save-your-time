package it.carmelolagamba.saveyourtime.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.databinding.FragmentHistoryDashboardBinding
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.service.AppService
import it.carmelolagamba.saveyourtime.service.ChartService
import it.carmelolagamba.saveyourtime.service.HistoryService
import it.carmelolagamba.saveyourtime.service.UtilService
import javax.inject.Inject
import android.R as AndroidR


/**
 * @author carmelolg
 * @since version 1.0
 */
@AndroidEntryPoint
class HistoryFragment : Fragment() /*AbstractFragment()*/, AdapterView.OnItemSelectedListener {

    private var _binding: FragmentHistoryDashboardBinding? = null

    @Inject
    lateinit var appService: AppService

    @Inject
    lateinit var utilService: UtilService

    @Inject
    lateinit var historyService: HistoryService

    @Inject
    lateinit var chartService: ChartService

    private var apps: List<App> = mutableListOf()
    private var weeklyMap: Map<String, Int> = mutableMapOf()
    private var lastWeeklyMap: Map<String, Int> = mutableMapOf()
    private var last7DaysMap: Map<Int, Pair<String, Int>> = mutableMapOf()
    private var packageName: String? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = _binding ?: FragmentHistoryDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onStart() {
        super.onStart()

        retrieveApps()

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(), AndroidR.layout.simple_spinner_dropdown_item, apps.map { it.name }
        )

        binding.appHistoryChoice.adapter = adapter
        binding.appHistoryChoice.onItemSelectedListener = this


        if (apps.isEmpty()) {
            binding.containerCaring.visibility = View.VISIBLE
            binding.containerTop.visibility = View.GONE
        } else {
            binding.containerCaring.visibility = View.GONE
            binding.containerTop.visibility = View.VISIBLE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshCharts() {

        if (last7DaysMap.isNotEmpty()) {

            /** Building VICO graph main object */
            val chartData = chartService.buildChartDataWithOrder(requireContext(), last7DaysMap)
            val chartEntryModel = chartService.buildChartEntryModel(chartData)

            (binding.lastWeekChart.bottomAxis as? HorizontalAxis<AxisPosition.Horizontal.Bottom>)?.valueFormatter =
                chartService.buildDefaultYAxisFormatter(chartData)

            /** Set VICO graph view */
            binding.lastWeekChart.setModel(chartEntryModel)

        }

        if (weeklyMap.isNotEmpty()) {

            /** Building VICO graph main object */
            val chartData = chartService.buildChartData(requireContext(), weeklyMap)
            val chartEntryModel = chartService.buildChartEntryModel(chartData)

            (binding.weeklyChart.bottomAxis as? HorizontalAxis<AxisPosition.Horizontal.Bottom>)?.valueFormatter =
                chartService.buildDefaultYAxisFormatter(chartData)


            /** Set VICO graph view */
            binding.weeklyChart.setModel(chartEntryModel)

        }
    }

    private fun refreshUI(packageName: String) {

        /** Retrieve usage data for this week and last week */
        weeklyMap = historyService.getWeeklyDetails(requireContext(), packageName)
        lastWeeklyMap = historyService.getWeeklyDetails(requireContext(), packageName, -1)
        last7DaysMap = historyService.getLast7DaysUsage(
            requireContext(),
            packageName,
            weeklyMap,
            lastWeeklyMap
        )

        /** Calculate weekly usage */
        val currentDay = utilService.getCurrentDay()
        var isToday = false
        var yesterdayUsage = historyService.getYesterdayUsage(requireContext(), packageName)
        var totalWeekUsage = 0
        var todayUsage = 0
        var countDays = 1
        weeklyMap.forEach { (day, usage) ->
            totalWeekUsage += usage
            if (day == currentDay.name) {
                todayUsage = usage
                isToday = true
            }
            if (!isToday) {
                countDays++
            }
        }


        val weeklyAvg = totalWeekUsage / countDays
        val comparedToYesterdayInPercentage: Int = if (yesterdayUsage > 0) {
            ((todayUsage * 100) / yesterdayUsage) - 100
        } else {
            todayUsage * 100
        }

        /** Calculate last weekly usage  */
        var isLikeToday = false
        var totalLastWeekUsage = 0
        lastWeeklyMap.forEach { (day, usage) ->
            if (day == currentDay.name) {
                totalLastWeekUsage += usage
                isLikeToday = true
            }

            if (!isLikeToday) {
                totalLastWeekUsage += usage
            }
        }

        val comparedToLastWeekInPercentage: Int = if (totalLastWeekUsage > 0) {
            ((totalWeekUsage * 100) / totalLastWeekUsage) - 100
        } else {
            totalWeekUsage * 100
        }

        /** Refresh UI */

        if (totalWeekUsage > 0) {
            binding.containerWeekly.visibility = View.VISIBLE
        } else {
            binding.containerWeekly.visibility = View.GONE
        }

        val last7DayTotalUsage = last7DaysMap.values.sumOf { it.second }
        val last7DayAvgUsage = last7DayTotalUsage / 7

        if (last7DayTotalUsage > 0) {
            binding.containerLastWeek.visibility = View.VISIBLE
            binding.totalUsageContainerLast7.visibility = View.VISIBLE
            binding.totalWeeklyUsageLast7.text = "$last7DayTotalUsage min"
            binding.totalAvgUsageLast7.text = "$last7DayAvgUsage min"
        } else {
            binding.containerLastWeek.visibility = View.GONE
            binding.totalUsageContainerLast7.visibility = View.GONE
        }

        binding.totalAvgUsage.text = "$weeklyAvg min"
        binding.totalWeeklyUsage.text = "${weeklyMap.values.sumOf { it }} min"

        val prefixYesterday = if (comparedToYesterdayInPercentage > 0) {
            "+"
        } else {
            ""
        }

        val prefixLastWeek = if (comparedToLastWeekInPercentage > 0) {
            "+"
        } else {
            ""
        }

        binding.differenceFromLastWeek.text = "$prefixLastWeek $comparedToLastWeekInPercentage %"
        binding.differenceFromYesterday.text = "$prefixYesterday $comparedToYesterdayInPercentage %"

        refreshCharts()
    }

    private fun retrieveApps() {

        apps = appService.findAllChecked()

        apps = apps.sortedWith(compareBy<App> { it.todayUsage }.reversed()
            .thenBy { it.name.lowercase() })
            .toList()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val name = binding.appHistoryChoice.selectedItem.toString()
        packageName = appService.findPackageByName(name, apps)

        packageName?.let { refreshUI(it) }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}