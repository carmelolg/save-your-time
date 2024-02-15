package it.carmelolagamba.saveyourtime.ui.info

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
        return applications[position] as AppDataModel
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        val result: View
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView =
                LayoutInflater.from(parent.context).inflate(R.layout.app_data_model, parent, false)
            viewHolder.appIcon = convertView.findViewById(R.id.app_icon)
            viewHolder.appName =
                convertView.findViewById(R.id.app_name)
            viewHolder.appChecked =
                convertView.findViewById(R.id.app_checked)
            viewHolder.appNotifyTime = convertView.findViewById(R.id.notify_time)
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }

        val item: AppDataModel = getItem(position)
        viewHolder.appIcon.setImageDrawable(item.icon)
        viewHolder.appName.text = item.name
        viewHolder.appChecked.isChecked = item.checked
        viewHolder.packageName = item.packageName
        if (item.notifyTime != null && item.notifyTime > 0) {
            viewHolder.appNotifyTime.setText(item.notifyTime.toString())
        }

        viewHolder.appNotifyTime.imeOptions = EditorInfo.IME_ACTION_DONE
        viewHolder.appNotifyTime.setSelection(viewHolder.appNotifyTime.length())
        viewHolder.appNotifyTime.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateItem(item, viewHolder)
                if (convertView != null) {
                    context.hideKeyboard(convertView)
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

        var minutes: Int = getMinutesFromText(viewHolder.appNotifyTime.text.toString())
            //if (viewHolder.appChecked.isChecked) getMinutesFromText(viewHolder.appNotifyTime.text.toString()) else 60

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
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun getMinutesFromText(text: String): Int {
        return text.filter { it.isDigit() }.toInt()
    }
}

