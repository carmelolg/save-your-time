package it.carmelolagamba.saveyourtime.ui.home

import android.app.usage.UsageStats
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import it.carmelolagamba.saveyourtime.R
import it.carmelolagamba.saveyourtime.service.AppService
import javax.inject.Inject

class HomeService @Inject constructor(){


    @Inject
    lateinit var appService: AppService

    fun createTableHeader(context: Context, resources: Resources): TableRow {
        val header = TableRow(context)

        val firstCol = TextView(context)
        firstCol.text = resources.getText(R.string.application_label)

        val secondCol = TextView(context)
        secondCol.text = resources.getText(R.string.time_usage_label)

        val thirdCol = TextView(context)
        thirdCol.text = resources.getText(R.string.time_remaining_label)
        thirdCol.gravity = Gravity.CENTER

        // Set text color
        val primaryColor = context.getColor(R.color.primary)
        firstCol.setTextColor(primaryColor)
        secondCol.setTextColor(primaryColor)
        thirdCol.setTextColor(primaryColor)

        // Set text size
        val textSize = resources.getDimension(R.dimen.table_header_size_default)
        firstCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        secondCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        thirdCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

        header.addView(firstCol)
        header.addView(secondCol)
        header.addView(thirdCol)

        return header
    }

    fun createTableRow(context: Context, resources: Resources, value: UsageStats): TableRow {
        val row = TableRow(context)

        val firstCol = TextView(context)
        firstCol.text = appService.findNameByPackageName(value.packageName)

        val secondCol = TextView(context)
        val time = value.totalTimeInForeground / 1000 / 60
        secondCol.text = "$time min"

        val thirdCol = TextView(context)
        thirdCol.text = resources.getText(R.string.time_exceeded)

        // Set text color
        var secondaryColor = context.getColor(R.color.secondary)
        firstCol.setTextColor(secondaryColor)
        secondCol.setTextColor(secondaryColor)
        val timeRemaining = 60 - time
        if (timeRemaining < 0) {
            context?.let { thirdCol.setTextColor(it.getColor(R.color.tertiary)) }
        } else {
            thirdCol.text = "$timeRemaining min"
            thirdCol.setTextColor(secondaryColor)
        }

        // Set text size
        val textSize = resources.getDimension(R.dimen.text_size_default)
        firstCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        secondCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        thirdCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

        // Set options on cols
        secondCol.gravity = Gravity.CENTER
        thirdCol.gravity = Gravity.CENTER

        // Add views to row
        row.addView(firstCol)
        row.addView(secondCol)
        row.addView(thirdCol)

        return row
    }

}