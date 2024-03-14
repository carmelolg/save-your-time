package it.carmelolagamba.saveyourtime.service.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * @author carmelolg
 * @since version 1.2.3
 */
class SYTAlarmSchedulerImpl(private val context: Context) : SYTAlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(alarmItem: SYTAlarmItem) {

        val intent = Intent(context, SYTReceiver::class.java).apply {
            putExtra("message", alarmItem.message)
        }

        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            alarmItem.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            1000 * 1800, /* every 30 min */
            pendingIntent
        )

        Log.e("Alarm", "Alarm set at ${System.currentTimeMillis()}")
    }

    override fun cancel(alarmItem: SYTAlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarmItem.hashCode(),
                Intent(context, SYTReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

}