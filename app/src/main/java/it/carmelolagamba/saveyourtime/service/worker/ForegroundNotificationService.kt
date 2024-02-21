package it.carmelolagamba.saveyourtime.service.worker

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import it.carmelolagamba.saveyourtime.MainActivity
import it.carmelolagamba.saveyourtime.R

/**
 * @author carmelolg
 * @since version 1.0
 */
class ForegroundNotificationService : Service() {

        companion object {
            fun startService(context: Context) {
                val startIntent = Intent(context, ForegroundNotificationService::class.java)
                ContextCompat.startForegroundService(context, startIntent)
            }

            fun stopService(context: Context) {
                val stopIntent = Intent(context, ForegroundNotificationService::class.java)
                context.stopService(stopIntent)
            }
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            createServiceTask()

            //stopForeground(STOP_FOREGROUND_REMOVE)
            return START_NOT_STICKY
        }

        override fun onDestroy() {
            Log.d("SYT", "Task killed")
            super.onDestroy()
        }
        override fun onTaskRemoved(rootIntent: Intent?) {
            super.onTaskRemoved(rootIntent)
            Log.d("SYT", "Task removed")
            createServiceTask()
        }

        override fun onBind(intent: Intent?): IBinder? {
            return null
        }

        private fun createServiceTask() {

            val notificationIntent = Intent(this, MainActivity::class.java)

            val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notification = NotificationCompat.Builder(this, resources.getString(R.string.notification_service_channel_id))
                .setContentTitle(resources.getText(R.string.notification_service_title))
                .setContentText(resources.getText(R.string.notification_service_description))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground))
                .setContentIntent(pendingIntent)
                .build()

            startForeground(1, notification)
        }


}