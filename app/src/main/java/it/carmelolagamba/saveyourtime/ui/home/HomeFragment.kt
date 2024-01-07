package it.carmelolagamba.saveyourtime.ui.home

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
import it.carmelolagamba.saveyourtime.service.UtilService
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

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val statsManager =
            requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        var statsUsageMap = statsManager.queryAndAggregateUsageStats(
            utilService.todayMidnightMillis(),
            utilService.tomorrowMidnightMillis()
        )

        statsUsageMap = statsUsageMap.toList()
            .sortedByDescending { (_, value) -> value.totalTimeInForeground }
            .toMap()

        statsUsageMap = statsUsageMap.toList()
            .filter { (application, _) -> utilService.appListPackageName().contains(application) }
            .toMap()


        var apps: List<App> = appService.findAllChecked()

        if (apps.isNotEmpty()) {
            val usageTable: TableLayout = binding.usageTable
            usageTable.addView(homeService.createTableHeader(requireContext(), resources), 0)

            val chartData = mutableMapOf<Float, Pair<Float, String>>()

            var index: Float = 1F
            statsUsageMap.forEach { (_, value) ->
                usageTable.addView(homeService.createTableRow(requireContext(), resources, value))
                if (value.totalTimeInForeground > 0) {
                    chartData[index] = Pair(
                        (value.totalTimeInForeground / 1000 / 60).toFloat(),
                        apps.find { it.packageName == value.packageName }!!.name
                    )
                    index++
                }
            }

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

            binding.chartView.setModel(chartEntryModel)

        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}