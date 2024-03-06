package it.carmelolagamba.saveyourtime.service

import android.content.Context
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import it.carmelolagamba.saveyourtime.R
import it.carmelolagamba.saveyourtime.persistence.App
import javax.inject.Inject

class ChartService @Inject constructor() {

    @Inject
    lateinit var utilService: UtilService

    fun buildChartData(
        apps: List<App>
    ): MutableMap<Float, Pair<Float, String>> {
        /** Building VICO graph main object */
        var chartData = mutableMapOf<Float, Pair<Float, String>>()

        /** This index is used in order to formatting graph's axis better */
        var index = 1F

        apps.forEach { app ->

            /** Add app in graph view */
            chartData[index] = Pair(
                (app.todayUsage).toFloat(),
                app.name
            )
            index++
        }

        chartData = chartData.toList().sortedByDescending { it.second.first }
            .toMap() as MutableMap<Float, Pair<Float, String>>

        return chartData
    }

    /**
     * @param map the map to render in charts
     * @return Return a Map. The key it's the y-axes on chart the value it's a Pair (value on chart, label on y-axes)
     */
    fun buildChartData(
        context: Context,
        map: Map<String, Int>
    ): MutableMap<Float, Pair<Float, String>> {
        /** Building VICO graph main object */
        var chartData = mutableMapOf<Float, Pair<Float, String>>()

        /** This index is used in order to formatting graph's axis better */
        var index = 1F

        map.forEach { (day, usage) ->

            var label = utilService.getDateFromToday(index.toInt() - 1)

            if (day == utilService.getCurrentDay().name) {
                label = context.resources.getText(R.string.now).toString()
            }

            /** Add app in graph view */
            chartData[index] = Pair(
                usage.toFloat(),
                label
            )
            index++
        }

        chartData = chartData.toList().sortedByDescending { it.second.first }
            .toMap() as MutableMap<Float, Pair<Float, String>>

        return chartData
    }

    /**
     * @param map the map to render in charts. This map has the order in the key, the value is a Pair(first = label on y-axis, second = usage value)
     * @return Return a Map. The key it's the y-axes on chart the value it's a Pair (value on chart, label on y-axes)
     */
    fun buildChartDataWithOrder(
        context: Context,
        map: Map<Int, Pair<String, Int>>
    ): MutableMap<Float, Pair<Float, String>> {
        /** Building VICO graph main object */
        val chartData = mutableMapOf<Float, Pair<Float, String>>()



        map.forEach { (position, value) ->
            var label = utilService.getDateFromToday(-(7 - position))

            if (value.first == utilService.getCurrentDay().name) {
                label = context.resources.getText(R.string.now).toString()
            }
            /** Add app in graph view */
            chartData[position.toFloat()] = Pair(
                value.second.toFloat(),
                label
            )
        }

        return chartData.toList().sortedByDescending { it.first }
            .toMap() as MutableMap<Float, Pair<Float, String>>
    }


    /**
     * Create the chart model
     * @param chartData a MutableMap<Float, Pair<Float, String>> is required. You can build it using this.buildChartData or this.buildChartDataWithOrder
     * @return a ChartEntryModel (Vico)
     */
    fun buildChartEntryModel(chartData: MutableMap<Float, Pair<Float, String>>): ChartEntryModel {

        val xChartValues = chartData.keys.associateBy { it }

        val chartEntryModel =
            entryModelOf(xChartValues.keys.zip(chartData.values) { k, v ->
                entryOf(
                    k,
                    v.first
                )
            })


        return chartEntryModel
    }

    /**
     * Build a yAxisFormatter
     * @param chartData the MutableMap<Float, Pair<Float, String>> built using this.buildChartData or this.buildChartDataWithOrder
     * @return an AxisValueFormatter instance (for VICO)
     */
    fun buildDefaultYAxisFormatter(chartData: MutableMap<Float, Pair<Float, String>>): AxisValueFormatter<AxisPosition.Horizontal.Bottom> {

        /** Build the custom axis formatter */
        val yAxisValueFormatter =
            AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->

                if (chartData[value] == null) {
                    ""
                } else {
                    chartData[value]!!.second
                }
            }
        /** End build the custom axis formatter */

        return yAxisValueFormatter
    }


}

