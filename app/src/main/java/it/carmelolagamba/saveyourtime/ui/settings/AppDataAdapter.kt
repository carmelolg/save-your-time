package it.carmelolagamba.saveyourtime.ui.settings

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.patrykandpatrick.vico.core.extension.setFieldValue
import it.carmelolagamba.saveyourtime.R
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.service.AppService

/**
 * @author carmelolg
 * @since version 1.0
 */
class AppDataAdapter(
    private val applications: List<AppDataModel>,
    private val appService: AppService,
    context: Context
) :
    ArrayAdapter<Any?>(context, R.layout.app_data_model, applications) {

    private class ViewHolder {
        lateinit var appIcon: ImageView
        lateinit var appName: TextView
        lateinit var appNotifyTime: EditText
        lateinit var appChecked: CheckBox
        lateinit var packageName: String
    }

    override fun getCount(): Int {
        return applications.size
    }

    override fun getItem(position: Int): AppDataModel {
        return applications[position]
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var view = convertView
        val viewHolder: ViewHolder
        val result: View
        if (view == null) {
            viewHolder = ViewHolder()
            view =
                LayoutInflater.from(parent.context).inflate(R.layout.app_data_model, parent, false)
            viewHolder.appIcon = view.findViewById(R.id.app_icon)
            viewHolder.appName =
                view.findViewById(R.id.app_name)
            viewHolder.appChecked =
                view.findViewById(R.id.app_checked)
            viewHolder.appNotifyTime = view.findViewById(R.id.notify_time)
            result = view
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
            result = view
        }

        val item: AppDataModel = getItem(position)
        viewHolder.appIcon.setImageDrawable(item.icon)
        viewHolder.appName.text = item.name
        viewHolder.appChecked.isChecked = item.checked
        viewHolder.packageName = item.packageName
        if (item.notifyTime > 0) {
            viewHolder.appNotifyTime.setText(item.notifyTime.toString())
        }

        viewHolder.appNotifyTime.imeOptions = EditorInfo.IME_ACTION_DONE
        viewHolder.appNotifyTime.setSelection(viewHolder.appNotifyTime.length())
        viewHolder.appNotifyTime.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateItem(item, viewHolder)
                if (view != null) {
                    context.hideKeyboard(view)
                }
                true
            } else {
                false
            }
        }

        viewHolder.appNotifyTime.setOnClickListener {
            updateItem(item, viewHolder)
        }

        viewHolder.appChecked.setOnClickListener {
            item.setFieldValue("checked", viewHolder.appChecked.isChecked)
            updateItem(item, viewHolder)
        }

        return result
    }

    private fun updateItem(
        item: AppDataModel,
        viewHolder: ViewHolder
    ) {

        val minutes: Int = getMinutesFromText(viewHolder.appNotifyTime.text.toString())

        item.setFieldValue("notifyTime", minutes)
        appService.upsert(
            App(
                viewHolder.appName.text.toString(),
                viewHolder.packageName,
                viewHolder.appChecked.isChecked,
                minutes,
                item.todayUsage
            )
        )
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getMinutesFromText(text: String): Int {
        return text.filter { it.isDigit() }.toInt()
    }
}

