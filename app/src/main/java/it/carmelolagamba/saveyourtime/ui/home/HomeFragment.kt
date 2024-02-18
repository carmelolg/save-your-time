package it.carmelolagamba.saveyourtime.ui.home

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.R
import it.carmelolagamba.saveyourtime.StartActivity
import it.carmelolagamba.saveyourtime.databinding.FragmentHomeBinding
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.persistence.Event
import it.carmelolagamba.saveyourtime.service.AppService
import it.carmelolagamba.saveyourtime.service.EventService
import it.carmelolagamba.saveyourtime.service.UtilService
import it.carmelolagamba.saveyourtime.service.streaming.EventListener
import it.carmelolagamba.saveyourtime.service.streaming.EventNotifier
import javax.inject.Inject
import kotlin.random.Random


/**
 * @author carmelolg
 * @since version 1.0
 */
@AndroidEntryPoint
class HomeFragment : Fragment(), EventListener {

    private var _binding: FragmentHomeBinding? = null

    @Inject
    lateinit var utilService: UtilService

    @Inject
    lateinit var appService: AppService

    @Inject
    lateinit var homeService: HomeService

    @Inject
    lateinit var eventService: EventService

    private lateinit var eventBroadcaster: EventNotifier

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = _binding ?: FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        eventBroadcaster = EventNotifier.getInstance()

        return root
    }

    override fun onStart() {
        super.onStart()
        eventBroadcaster.addListener(this)
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        eventBroadcaster.removeListener(this)
        _binding = null
    }

    private fun refreshData() {
        val statsManager =
            requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        binding.usageTable.removeAllViews()

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
            var index = 1F

            apps.forEach { app ->

                val statsUsageMapValue: UsageStats

                /**
                 * If a selected app isn't used could not appear in statsUsageMap system object (and catch a NoSuchElementException
                 * We use statsUsageMap to get the total usage time for a single application (in the selected range)
                 * */
                try {
                    statsUsageMapValue =
                        statsUsageMap.last { application -> app.packageName == application.packageName }
                    app.todayUsage = statsUsageMapValue.totalTimeInForeground.toInt() / 1000 / 60
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

                /** Add app in graph view */
                chartData[index] = Pair(
                    (app.todayUsage).toFloat(),
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

        } else {
            binding.caringMessage.text = resources.getText(R.string.apps_empty_caring_message)
            binding.chartLabel.visibility = View.INVISIBLE
            binding.tableLabel.visibility = View.INVISIBLE
        }
    }


    override fun onEvent(channel: String) {
        Log.d("SYT", "Event received")
        Log.d("SYT", channel)

        /** Step 0 If it's midnight, reset all and clean DB */
        if (utilService.isNearMidnight()) {
            eventService.cleanDB()
            appService.resetAllUsages()
        }


        /** Step 1 Check application with time exceeded */
        val exceededApp: List<App> = appService.findExceededApplication()

        /** Step 2 if app is already notified to user, do nothing */
        exceededApp.forEach { app: App ->
            if (!eventService.isAppNotified(app.packageName)) {
                /** Step 3 if app isn't notified to user, create Event and save it on DB */
                eventService.insert(
                    Event(
                        null,
                        channel,
                        app.packageName,
                        System.currentTimeMillis(),
                        true
                    )
                )
                /** Step 3.1 Send the notification */
                Log.d("SYT", "Sending notification for $app")
                sendNotification(
                    requireContext(),
                    requireContext().resources.getString(R.string.warn_title_notify_app),
                    requireContext().resources.getString(R.string.warn_description_notify_app) + " ${app.name}"
                )
            }
        }

    }

    private fun sendNotification(context: Context, title: String, description: String) {

        val startActivityIntent = Intent(context, StartActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }


        val startActivityPendingIntent = PendingIntent.getActivity(
            context,
            Random.nextInt(),
            startActivityIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(
            context,
            ContextCompat.getString(context, R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(startActivityPendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        with(NotificationManagerCompat.from(context)) {
            notificationManager.notify(Random.nextInt(), builder)
        }
    }
}