package it.carmelolagamba.saveyourtime.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.R
import it.carmelolagamba.saveyourtime.databinding.FragmentHomeBinding
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.service.AppService
import it.carmelolagamba.saveyourtime.service.ChartService
import it.carmelolagamba.saveyourtime.service.HomeService
import it.carmelolagamba.saveyourtime.service.UtilService
import it.carmelolagamba.saveyourtime.service.worker.SYTAlarmItem
import it.carmelolagamba.saveyourtime.service.worker.SYTAlarmScheduler
import it.carmelolagamba.saveyourtime.service.worker.SYTAlarmSchedulerImpl
import javax.inject.Inject


/**
 * @author carmelolg
 * @since version 1.0
 */
@AndroidEntryPoint
class HomeFragment : Fragment() /*AbstractFragment()*/ {

    private var _binding: FragmentHomeBinding? = null

    @Inject
    lateinit var appService: AppService

    @Inject
    lateinit var homeService: HomeService

    @Inject
    lateinit var chartService: ChartService

    @Inject
    lateinit var utilService: UtilService

    private var apps: List<App> = mutableListOf()

    lateinit var alarmScheduler: SYTAlarmScheduler

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

        alarmScheduler = SYTAlarmSchedulerImpl(requireContext())
        alarmScheduler.schedule(
            SYTAlarmItem(
                resources.getText(R.string.notification_service_channel_name).toString()
            )
        )

        /**
        root.setOnTouchListener { _, event ->
        gestureDetector.onTouchEvent(event)
        }*/

        return root
    }

    override fun onStart() {
        super.onStart()
        retrieveApps()
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
            binding.containerCaring.visibility = View.GONE

            val usageTable: TableLayout = binding.usageTable
            usageTable.removeAllViews()

            /** Add header to the table */
            usageTable.addView(homeService.createTableHeader(requireContext(), resources), 0)

            apps.forEach { app ->

                /** Update current usage */
                app.todayUsage =
                    utilService.getUsageInMinutesByPackage(requireContext(), app.packageName)

                /** Create a row in table for the selected application */
                usageTable.addView(
                    homeService.createTableRow(
                        requireContext(),
                        resources,
                        app
                    )
                )
            }

            /** Building VICO graph main object */
            val chartData = chartService.buildChartData(apps)
            val chartEntryModel = chartService.buildChartEntryModel(chartData)

            /** Build the custom axis formatter */
            (binding.chartView.bottomAxis as? HorizontalAxis<AxisPosition.Horizontal.Bottom>)?.valueFormatter =
                chartService.buildDefaultYAxisFormatter(chartData)

            /** Set VICO graph view */
            binding.chartView.setModel(chartEntryModel)

            if (apps.sumOf { app -> app.todayUsage } < 1) {
                binding.chartLabel.visibility = View.INVISIBLE
                binding.chartView.visibility = View.INVISIBLE
                binding.containerChart.visibility = View.GONE
            } else {
                binding.chartLabel.visibility = View.VISIBLE
                binding.chartView.visibility = View.VISIBLE
                binding.containerChart.visibility = View.VISIBLE
            }

        } else {
            binding.caringMessage.visibility = View.VISIBLE
            binding.caringImage.visibility = View.VISIBLE
            binding.containerCaring.visibility = View.VISIBLE
            binding.chartLabel.visibility = View.INVISIBLE
            binding.tableLabel.visibility = View.INVISIBLE
        }
    }

    private fun retrieveApps() {

        apps = appService.findAllChecked()

        apps = apps.sortedWith(compareBy<App> { it.todayUsage }.reversed()
            .thenBy { it.name.lowercase() })
            .toList()
    }
}