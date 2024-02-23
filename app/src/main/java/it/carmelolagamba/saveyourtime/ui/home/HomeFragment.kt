package it.carmelolagamba.saveyourtime.ui.home

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.databinding.FragmentHomeBinding
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.service.AppService
import it.carmelolagamba.saveyourtime.service.EventService
import it.carmelolagamba.saveyourtime.service.UtilService
import javax.inject.Inject


/**
 * @author carmelolg
 * @since version 1.0
 */
@AndroidEntryPoint
class HomeFragment : Fragment() /*AbstractFragment()*/ {

    private var _binding: FragmentHomeBinding? = null

    @Inject
    lateinit var utilService: UtilService

    @Inject
    lateinit var appService: AppService

    @Inject
    lateinit var homeService: HomeService

    @Inject
    lateinit var eventService: EventService

    private var apps: List<App> = mutableListOf()

    private val binding get() = _binding!!

    /**
    override fun innerOnScroll(x: Float, y: Float) {
    //TODO("Not yet implemented")
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = _binding ?: FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /**
        root.setOnTouchListener { _, event ->
        gestureDetector.onTouchEvent(event)
        }*/

        return root
    }

    override fun onStart() {
        super.onStart()
        refreshData()
        refreshUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshUI() {

        if (apps.isNotEmpty()) {

            binding.caringMessage.visibility = View.INVISIBLE
            binding.caringImage.visibility = View.INVISIBLE

            /** Building VICO graph main object */
            var chartData = mutableMapOf<Float, Pair<Float, String>>()

            /** This index is used in order to formatting graph's axis better */
            var index = 1F

            val usageTable: TableLayout = binding.usageTable
            usageTable.removeAllViews()

            /** Add header to the table */
            usageTable.addView(homeService.createTableHeader(requireContext(), resources), 0)

            apps.forEach { app ->

                /** Create a row in table for the selected application */
                usageTable.addView(
                    homeService.createTableRow(
                        requireContext(),
                        resources,
                        app
                    )
                )

                /** Add app in graph view */
                chartData[index] = Pair(
                    (app.todayUsage).toFloat(),
                    app.name
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

            (binding.chartView.bottomAxis as? HorizontalAxis<AxisPosition.Horizontal.Bottom>)?.valueFormatter =
                yAxisValueFormatter

            /** End build the custom axis formatter */

            /** Set VICO graph view */
            binding.chartView.setModel(chartEntryModel)
        } else {
            binding.caringMessage.visibility = View.VISIBLE
            binding.caringImage.visibility = View.VISIBLE
            binding.chartLabel.visibility = View.INVISIBLE
            binding.tableLabel.visibility = View.INVISIBLE
        }
    }

    private fun refreshData() {

        /** Clean old data */
        appService.resetOldData()

        /** Get all checked apps from local DB. A checked app is selected by the final user on the control plane */
        apps = appService.findAllChecked()

        /** If the final user selected at list one application, then build the page */
        if (apps.isNotEmpty()) {

            apps.forEach { app ->

                app.todayUsage = getUsageInMinutesByPackage(app.packageName)
                if (app.todayUsage > 0) {
                    app.lastUpdate = System.currentTimeMillis()
                }

                /** Update app on local DB */
                appService.upsert(app)
            }

            apps = apps.sortedWith(compareBy<App> { it.todayUsage }.reversed()
                .thenBy { it.name.lowercase() })
                .toList()
        }
    }

    /**
     * @param packageName the package of the app that you want get the total daily usage
     * @return the total usage in minutes
     */
    private fun getUsageInMinutesByPackage(packageName: String): Int {

        val statsManager =
            requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val usageEvents: UsageEvents = statsManager.queryEvents(
            utilService.todayMidnightMillis(),
            utilService.tomorrowMidnightMillis()
        )

        var singleEventUsage: Long = 0L
        var totalUsage: Long = 0L

        while (usageEvents.hasNextEvent()) {
            val currentEvent: UsageEvents.Event = UsageEvents.Event()
            usageEvents.getNextEvent(currentEvent)

            if (packageName == currentEvent.packageName) {
                val time = currentEvent.timeStamp

                if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED
                    || currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED
                ) {
                    if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                        singleEventUsage = time
                    } else {
                        if (singleEventUsage > 0) {
                            totalUsage = totalUsage.plus(time - singleEventUsage)
                        }
                        singleEventUsage = 0L
                    }
                }
            }
        }
        if (singleEventUsage > 0) {
            totalUsage += (System.currentTimeMillis() - singleEventUsage)
        }

        return (totalUsage / 1000 / 60).toInt()
    }

}