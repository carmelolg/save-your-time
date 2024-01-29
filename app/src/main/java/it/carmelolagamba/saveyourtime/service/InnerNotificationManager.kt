package it.carmelolagamba.saveyourtime.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getString
import it.carmelolagamba.saveyourtime.R
import javax.inject.Inject
import kotlin.random.Random

class InnerNotificationManager @Inject constructor() {

    /** TODO delete
    lifecycleScope.launch {
    //val str: String? = FirebaseMessaging.getInstance().token.await<String?>()
    FirebaseMessaging.getInstance().token.addOnCompleteListener {
    val srt: String = it.result
    Log.i("Token FCM", srt)
    }
    }
     */
    fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(context, R.string.notification_channel_name)
            val descriptionText = getString(context, R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                getString(context, R.string.notification_channel_id),
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


    fun sendNotification(context: Context, title: String, description: String) {
        createNotificationChannel(context)

        var builder = NotificationCompat.Builder(
            context,
            getString(context, R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        with(NotificationManagerCompat.from(context)) {
            notificationManager.notify(Random.nextInt(), builder)
        }
    }
}