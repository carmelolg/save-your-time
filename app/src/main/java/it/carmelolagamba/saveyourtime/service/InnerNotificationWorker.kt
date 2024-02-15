package it.carmelolagamba.saveyourtime.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import it.carmelolagamba.saveyourtime.R
import javax.inject.Inject
import kotlin.random.Random

class InnerNotificationWorker @Inject constructor(val context: Context,
                                                  workerParams: WorkerParameters): Worker(context, workerParams) {

    override fun doWork(): Result {
        innerDoWork()
        //context.getSharedPreferences()
        //Log.i("TEST", utilService.todayMidnightMillis().toString())
        return Result.success()
    }

    private fun innerDoWork() {
        Log.i("Worker X", "Enter worker")
        Log.i("Worker X", inputData.keyValueMap.keys.toString())
        createNotificationChannel(context)

        inputData.keyValueMap.keys.forEach{ appLabel ->
            //sendNotification(context, context.resources.getString(R.string.warn_title_notify_app), context.resources.getString(R.string.warn_description_notify_app) + " $appLabel")
        }

    }

    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = ContextCompat.getString(context, R.string.notification_channel_name)
            val descriptionText =
                ContextCompat.getString(context, R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                ContextCompat.getString(context, R.string.notification_channel_id),
                name,
                importance
            ).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun sendNotification(context: Context, title: String, description: String) {

        var builder = NotificationCompat.Builder(
            context,
            ContextCompat.getString(context, R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            //.setContentIntent()
            .build()

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        with(NotificationManagerCompat.from(context)) {
            notificationManager.notify(Random.nextInt(), builder)
        }
    }
}

