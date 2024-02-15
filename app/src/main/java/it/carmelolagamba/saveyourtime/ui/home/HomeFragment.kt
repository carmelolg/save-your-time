package it.carmelolagamba.saveyourtime.ui.home

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.R
import it.carmelolagamba.saveyourtime.databinding.FragmentHomeBinding
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.service.AppService
import it.carmelolagamba.saveyourtime.service.InnerNotificationWorker
import it.carmelolagamba.saveyourtime.service.UtilService
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    @Inject
    lateinit var utilService: UtilService

    @Inject
    lateinit var appService: AppService

    @Inject
    lateinit var homeService: HomeService


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = _binding ?: FragmentHomeBinding.inflate(inflater!!, container, false)
        val root: View = binding.root

        return root
    }

    override fun onStart() {
        super.onStart()
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshData() {
        val statsManager =
            requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        /** Get today app usages */
        var statsUsageMap = statsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            utilService.todayMidnightMillis(),
            utilService.tomorrowMidnightMillis()
        )

        /** Sort application for usage in DESC mode */
        statsUsageMap = statsUsageMap
            .sortedByDescending { value -> value.totalTimeInForeground }

        /** Get all checked apps from local DB. A checked app is selected by the final user on the control plane */
        var apps: List<App> = appService.findAllChecked()

        /** notifyApps is an helpful List for notification worker - in evaluation if remove it in the future */
        var notifyApps: ArrayList<App> = ArrayList()


        /** If the final user selected at list one application, then build the page */
        if (apps.isNotEmpty()) {

            val usageTable: TableLayout = binding.usageTable

            //apps = apps.sortedByDescending { value -> value.todayUsage }.sortedBy { value -> value.name }.toList()
            apps = apps.sortedWith(compareBy<App> { it.todayUsage }.reversed().thenBy { it.name })
                .toList()

            /** Add header to the table */
            usageTable.addView(homeService.createTableHeader(requireContext(), resources), 0)

            /** Building VICO graph main object */
            var chartData = mutableMapOf<Float, Pair<Float, String>>()

            /** This index is used in order to formatting graph's axis better */
            var index: Float = 1F

            apps.forEach { app ->

                var statsUsageMapValue: UsageStats

                /**
                 * If a selected app isn't used could not appear in statsUsageMap system object (and catch a NoSuchElementException
                 * We use statsUsageMap to get the total usage time for a single application (in the selected range)
                 * */
                try {
                    statsUsageMapValue =
                        statsUsageMap.last { application -> app.packageName == application.packageName }
                    app.todayUsage = statsUsageMapValue.totalTimeInForeground.toInt()
                } catch (ex: NoSuchElementException) {
                    app.todayUsage = 0
                    Log.d("SYT", "${app.name} unused")
                }

                /** Create a row in table for the selected application */
                usageTable.addView(
                    homeService.createTableRow(
                        requireContext(),
                        resources,
                        app
                    )
                )

                /** If the app usage exceeded the max time, notify the final user by a notify worker */
                if ((app.todayUsage / 1000 / 60) >= app.notifyTime) {
                    notifyApps.add(app)
                }

                /** Add app in graph view */
                chartData[index] = Pair(
                    (app.todayUsage / 1000 / 60).toFloat(),
                    app.name
                )
                index++

                /** Update app on local DB */
                appService.upsert(app)
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

            (binding.chartView.bottomAxis as? HorizontalAxis<AxisPosition.Horizontal.Bottom>)?.valueFormatter =
                yAxisValueFormatter

            /** End build the custom axis formatter */

            /** Set VICO graph view */
            binding.chartView.setModel(chartEntryModel)

            startWorker(notifyApps)

        } else {
            binding.caringMessage.text = resources.getText(R.string.apps_empty_caring_message)
            binding.chartLabel.visibility = View.INVISIBLE
            binding.tableLabel.visibility = View.INVISIBLE
        }
    }

    private fun startWorker(apps: List<App>) {

        WorkManager.getInstance(requireContext()).cancelAllWork()
        val inputData = Data.Builder()
        apps.forEach { app ->
            inputData.putBoolean(app.name, true)
        }
        val myWorker: WorkRequest = PeriodicWorkRequestBuilder<InnerNotificationWorker>(
            resources.getInteger(
                R.integer.job_time
            ).toLong(), TimeUnit.MINUTES
        )
            .setInputData(inputData.build())
            .build()
        WorkManager.getInstance(requireContext()).enqueue(myWorker)

    }
}