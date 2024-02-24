package it.carmelolagamba.saveyourtime.service.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.saveyourtime.MainActivity
import it.carmelolagamba.saveyourtime.R
import it.carmelolagamba.saveyourtime.StartActivity
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.persistence.Event
import it.carmelolagamba.saveyourtime.service.AppService
import it.carmelolagamba.saveyourtime.service.EventService
import it.carmelolagamba.saveyourtime.service.UtilService
import it.carmelolagamba.saveyourtime.service.streaming.EventListener
import it.carmelolagamba.saveyourtime.service.streaming.EventNotifier
import javax.inject.Inject
import kotlin.random.Random

/**
 * @author carmelolg
 * @since version 1.0
 */
@AndroidEntryPoint
class SYTBackgroundService : Service(), EventListener {


    private lateinit var eventBroadcaster: EventNotifier

    @Inject
    lateinit var utilService: UtilService

    @Inject
    lateinit var appService: AppService

    @Inject
    lateinit var eventService: EventService

    private var apps: List<App> = mutableListOf()

    companion object {
        fun startService(context: Context) {
            val startIntent = Intent(context, SYTBackgroundService::class.java)
            ContextCompat.startForegroundService(context, startIntent)

        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, SYTBackgroundService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createServiceTask()

        eventBroadcaster = EventNotifier.getInstance()
        eventBroadcaster.addListener(this)

        //stopForeground(STOP_FOREGROUND_REMOVE)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d("SYT", "Task killed")
        eventBroadcaster.removeListener(this)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("SYT", "Task removed")
        eventBroadcaster.removeListener(this)
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

        val notification = NotificationCompat.Builder(
            this,
            resources.getString(R.string.notification_service_channel_id)
        )
            .setContentTitle(resources.getText(R.string.notification_service_title))
            .setContentText(resources.getText(R.string.notification_service_description))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_launcher_foreground
                )
            )
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }
    private fun refreshData() {

        /** Get all checked apps from local DB. A checked app is selected by the final user on the control plane */
        apps = appService.findAllChecked()

        /** If the final user selected at list one application, then build the page */
        if (apps.isNotEmpty()) {

            apps.forEach { app ->

                app.todayUsage = getUsageInMinutesByPackage(app.packageName)
                if (app.todayUsage > 0) {
                    app.lastUpdate = System.currentTimeMillis()
                }

                /** Update app on local DB */
                appService.upsert(app)
            }

        }
    }

    /**
     * @param packageName the package of the app that you want get the total daily usage
     * @return the total usage in minutes
     */
    private fun getUsageInMinutesByPackage(packageName: String): Int {

        val statsManager =
            applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val usageEvents: UsageEvents = statsManager.queryEvents(
            utilService.todayMidnightMillis(),
            utilService.tomorrowMidnightMillis()
        )

        var singleEventUsage: Long = 0L
        var totalUsage: Long = 0L

        while (usageEvents.hasNextEvent()) {
            val currentEvent: UsageEvents.Event = UsageEvents.Event()
            usageEvents.getNextEvent(currentEvent)

            if (packageName == currentEvent.packageName) {
                val time = currentEvent.timeStamp

                if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED
                    || currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED
                ) {
                    if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                        singleEventUsage = time
                    } else {
                        if (singleEventUsage > 0) {
                            totalUsage = totalUsage.plus(time - singleEventUsage)
                        }
                        singleEventUsage = 0L
                    }
                }
            }
        }
        if (singleEventUsage > 0) {
            totalUsage += (System.currentTimeMillis() - singleEventUsage)
        }

        return (totalUsage / 1000 / 60).toInt()
    }


    override fun onEvent(channel: String) {
        Log.d("SYT", "Background service event received $channel")

        /** Refreshing data */
        eventService.cleanDB()
        refreshData()

        /** Step 1 Check application with time exceeded */
        val exceededApp: List<App> = appService.findExceededApplication()

        /** Step 2 if app is already notified to user, do nothing */
        exceededApp.forEach { app: App ->
            if (!eventService.isAppNotified(app.packageName)) {
                /** Step 3 if app isn't notified to user, create Event and save it on DB */
                val event: Event? = eventService.findEventByPackageName(app.packageName)
                if (event != null) {
                    event.notified = true
                    event.insertDate = System.currentTimeMillis()
                    eventService.upsert(event)
                } else {
                    eventService.insert(
                        Event(
                            null,
                            channel,
                            app.packageName,
                            System.currentTimeMillis(),
                            true
                        )
                    )
                }
                /** Step 3.1 Send the notification */
                Log.d("SYT", "Sending notification for $app")
                sendNotification(
                    this,
                    this.resources.getString(R.string.warn_title_notify_app),
                    this.resources.getString(R.string.warn_description_notify_app) + " ${app.name}"
                )

                /** Step 4 Block application */
                // TODO
            }
        }
    }


    private fun sendNotification(context: Context, title: String, description: String) {

        val startActivityIntent = Intent(context, StartActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }


        val startActivityPendingIntent = PendingIntent.getActivity(
            context,
            Random.nextInt(),
            startActivityIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(
            context,
            ContextCompat.getString(context, R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_launcher_foreground
                )
            )
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(startActivityPendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        with(NotificationManagerCompat.from(context)) {
            notificationManager.notify(Random.nextInt(), builder)
        }
    }


}