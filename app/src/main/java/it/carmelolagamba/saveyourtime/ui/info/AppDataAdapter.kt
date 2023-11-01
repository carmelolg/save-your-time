package it.carmelolagamba.saveyourtime.ui.info

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import it.carmelolagamba.saveyourtime.R
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.service.AppService

class AppDataAdapter (private val applications: List<AppDataModel>, private val appService: AppService, context: Context) :
    ArrayAdapter<Any?>(context, R.layout.app_data_model, applications) {

    private class ViewHolder {
        lateinit var appIcon: ImageView
        lateinit var appName: TextView
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

        viewHolder.appChecked.setOnClickListener {
            appService.upsert(App(viewHolder.appName.text.toString(), viewHolder.packageName, viewHolder.appChecked.isChecked))
        }

        return result
    }
}

