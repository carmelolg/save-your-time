package it.carmelolagamba.saveyourtime

import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

abstract class AbstractActivity : AppCompatActivity() {

    protected fun isAllCheckPassed(): Boolean {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), packageName
        )

        val notificationCheck = isNotificationChannelEnabled(this, resources.getString(R.string.notification_channel_id))

        return notificationCheck && mode == AppOpsManager.MODE_ALLOWED
    }

    protected fun isNotificationChannelEnabled(context: Context, channelId: String?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!TextUtils.isEmpty(channelId)) {
                val manager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
                val channel = manager.getNotificationChannel(channelId)
                return channel.importance != NotificationManager.IMPORTANCE_NONE && NotificationManagerCompat.from(context).areNotificationsEnabled()
            }
            false
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }
}