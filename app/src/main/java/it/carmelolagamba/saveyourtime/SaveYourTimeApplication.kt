package it.carmelolagamba.saveyourtime

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import dagger.hilt.android.HiltAndroidApp
import it.carmelolagamba.saveyourtime.service.worker.SYTWorker
import java.util.concurrent.TimeUnit

/**
 * @author carmelolg
 * @since version 1.0
 */
@HiltAndroidApp
class SaveYourTimeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        startWorker()
        createNotificationChannel()
        createNotificationServiceChannel()
    }

    companion object {
        lateinit var context: Context
    }

    private fun startWorker() {

        WorkManager.getInstance(applicationContext).cancelAllWork()
        val inputData = Data.Builder()

        val notificationWorker: WorkRequest = PeriodicWorkRequestBuilder<SYTWorker>(
            resources.getInteger(
                R.integer.job_time
            ).toLong(), TimeUnit.MINUTES
        ).setInputData(inputData.build()).build()
        WorkManager.getInstance(applicationContext).enqueue(notificationWorker)

    }

    private fun createNotificationChannel() {

        val name = ContextCompat.getString(applicationContext, R.string.notification_channel_name)
        val descriptionText =
            ContextCompat.getString(applicationContext, R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            ContextCompat.getString(applicationContext, R.string.notification_channel_id),
            name,
            importance
        ).apply {
            description = descriptionText
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }

    private fun createNotificationServiceChannel() {

        val name = ContextCompat.getString(applicationContext, R.string.notification_service_channel_name)
        val descriptionText =
            ContextCompat.getString(applicationContext, R.string.notification_service_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            ContextCompat.getString(applicationContext, R.string.notification_service_channel_id),
            name,
            importance
        ).apply {
            description = descriptionText
        }

        channel.setSound(null, null)
        channel.setShowBadge(false)

        // Register the channel with the system.
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

