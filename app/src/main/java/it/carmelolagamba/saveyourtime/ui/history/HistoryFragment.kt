package it.carmelolagamba.saveyourtime.ui.history

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.databinding.FragmentHistoryDashboardBinding
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.service.AppService
import it.carmelolagamba.saveyourtime.service.HistoryService
import it.carmelolagamba.saveyourtime.service.UtilService
import javax.inject.Inject


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

    private var apps: List<App> = mutableListOf()
    private var weeklyMap: Map<String, Int> = mutableMapOf()
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
            requireContext(),
            R.layout.simple_spinner_dropdown_item, apps.map { it.name }
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

    private fun refreshChart() {

        if (weeklyMap.isNotEmpty()) {

            /** Building VICO graph main object */
            var chartData = mutableMapOf<Float, Pair<Float, String>>()

            /** This index is used in order to formatting graph's axis better */
            var index = 1F

            weeklyMap.forEach { (day, usage) ->

                /** Add app in graph view */
                chartData[index] = Pair(
                    usage.toFloat(),
                    day
                )
                index++
            }

            /** Build the custom axis formatter */
            chartData = chartData.toList().sortedByDescending { it.second.first }
                .toMap() as MutableMap<Float, Pair<Float, String>>
            val xChartValues = chartData.keys.associateBy { it }
            val chartEntryModel =
                entryModelOf(xChartValues.keys.zip(chartData.values) { k, v ->
                    entryOf(
                        k,
                        v.first
                    )
                })

            val yAxisValueFormatter =
                AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->

                    if (chartData[value] == null) {
                        ""
                    } else {
                        chartData[value]!!.second
                    }
                }

            (binding.weeklyChart.bottomAxis as? HorizontalAxis<AxisPosition.Horizontal.Bottom>)?.valueFormatter =
                yAxisValueFormatter

            /** End build the custom axis formatter */

            /** Set VICO graph view */
            binding.weeklyChart.setModel(chartEntryModel)

            if (apps.sumOf { app -> app.todayUsage } < 1) {
                binding.weeklyChart.visibility = View.INVISIBLE
                binding.containerWeekly.visibility = View.GONE
            } else {
                binding.weeklyChart.visibility = View.VISIBLE
                binding.containerWeekly.visibility = View.VISIBLE
            }

        }
    }

    private fun refreshUI(packageName: String) {

        /** Retrieve usage data for this week and last week */
        weeklyMap = historyService.getWeeklyDetails(requireContext(), packageName)
        val lastWeeklyMap = historyService.getWeeklyDetails(requireContext(), packageName, -1)

        /** Calculate weekly usage */
        val currentDay = utilService.getCurrentDay()
        var isToday = false
        var yesterdayUsage = 0
        var totalWeekUsage = 0
        var todayUsage = 0
        var countDays = 0
        weeklyMap.forEach { (day, usage) ->
            totalWeekUsage += usage
            if (day == currentDay.name) {
                todayUsage = usage
                isToday = true
            }
            if (!isToday) {
                yesterdayUsage = usage
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
        Log.d("SYT", "$weeklyMap")
        Log.d("SYT", "$lastWeeklyMap")
        Log.d(
            "SYT",
            "avg $weeklyAvg trend yesterday $comparedToYesterdayInPercentage trend last week $comparedToLastWeekInPercentage"
        )

        refreshChart()
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