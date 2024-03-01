package it.carmelolagamba.saveyourtime.service

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import it.carmelolagamba.saveyourtime.R
import it.carmelolagamba.saveyourtime.persistence.App
import javax.inject.Inject

/**
 * @author carmelolg
 * @since version 1.0
 */
class HomeService @Inject constructor() {

    @Inject
    lateinit var appService: AppService

    /**
     * @param context the application Context
     * @param resources the Resources object
     * @return the header of the table with all headers set
     */
    fun createTableHeader(context: Context, resources: Resources): TableRow {
        val header = TableRow(context)

        val firstCol = TextView(context)
        firstCol.text = resources.getText(R.string.application_label)

        val secondCol = TextView(context)
        secondCol.text = resources.getText(R.string.time_usage_label)
        secondCol.gravity = Gravity.RIGHT


        val thirdCol = TextView(context)
        thirdCol.text = resources.getText(R.string.time_remaining_label)
        thirdCol.gravity = Gravity.RIGHT

        // Set text color
        firstCol.setTextAppearance(R.style.Theme_SaveYourTime_Table_Header)
        secondCol.setTextAppearance(R.style.Theme_SaveYourTime_Table_Header)
        thirdCol.setTextAppearance(R.style.Theme_SaveYourTime_Table_Header)

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

    /**
     * @param context the application Context
     * @param resources the Resources object
     * @param app the current app to show on table
     * @return the TableRow object to add on table
     */
    fun createTableRow(context: Context, resources: Resources, app: App): TableRow {
        val row = TableRow(context)

        val firstCol = TextView(context)
        firstCol.text = appService.findNameByPackageName(app.packageName)

        val secondCol = TextView(context)
        val time = app.todayUsage
        secondCol.text = "$time min"

        val thirdCol = TextView(context)
        thirdCol.text = resources.getText(R.string.time_exceeded)

        // Set text color
        firstCol.setTextAppearance(R.style.Theme_SaveYourTime_Table_Row)
        secondCol.setTextAppearance(R.style.Theme_SaveYourTime_Table_Row)
        val timeRemaining = app.notifyTime - time
        if (timeRemaining <= 0) {
            context?.let { thirdCol.setTextColor(it.getColor(R.color.fifth)) }
        } else {
            thirdCol.text = "$timeRemaining min"
            thirdCol.setTextAppearance(R.style.Theme_SaveYourTime_Table_Row)
        }

        // Set text size
        val textSize = resources.getDimension(R.dimen.text_size_default)
        firstCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        secondCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        thirdCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

        // Set options on cols
        secondCol.gravity = Gravity.RIGHT
        thirdCol.gravity = Gravity.RIGHT

        // Add views to row
        row.addView(firstCol)
        row.addView(secondCol)
        row.addView(thirdCol)

        return row
    }

}