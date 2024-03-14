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
import it.carmelolagamba.saveyourtime.service.PreferencesService
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

    @Inject
    lateinit var preferencesService: PreferencesService

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
        createServiceTask(true)

        eventBroadcaster = EventNotifier.getInstance()
        eventBroadcaster.addListener(this)

        //stopForeground(STOP_FOREGROUND_REMOVE)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d("SYT", "Task killed")
        //startService(applicationContext)
        createServiceTask(false)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("SYT", "Task removed")
        //startService(applicationContext)
        createServiceTask(false)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * Create a new background service
     * @param silent true if notification has to be silent, false otherwise
     */
    private fun createServiceTask(silent: Boolean) {

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
            .setSilent(silent)
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

                Log.d("SYT", "${app.name} used ${app.todayUsage}")

                /** Update app on local DB */
                appService.upsert(app)
            }

        }
    }

    /**
     * @param packageName the package of the app that you want get the total daily usage
     * @param start millis of start time of the range @default today's midnight
     * @param end millis of end time of the range @default tomorrow's midnight
     * @return the total usage in minutes
     * TODO substitute with UtilService.getUsageInMinutesByPackage()
     */
    private fun getUsageInMinutesByPackage(
        packageName: String,
        start: Long = utilService.todayMidnightMillis(),
        end: Long = utilService.tomorrowMidnightMillis()
    ): Int {

        val statsManager =
            applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val usageEvents: UsageEvents = statsManager.queryEvents(
            start,
            end
        )

        var singleEventUsage = 0L
        var totalUsage = 0L

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

    fun isAnApplicationRunningNow(packageName: String,
                                  start: Long = System.currentTimeMillis() - 60000,
                                  end: Long = utilService.tomorrowMidnightMillis()): Boolean {

        Log.d("SYT", "$packageName application running checking")

        val statsManager =
            applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val usageEvents: UsageEvents = statsManager.queryEvents(
            start,
            end
        )

        while (usageEvents.hasNextEvent()) {
            val currentEvent: UsageEvents.Event = UsageEvents.Event()
            usageEvents.getNextEvent(currentEvent)

            if (packageName == currentEvent.packageName && currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                Log.d("SYT", "$packageName checking")
                Log.d("SYT", "${currentEvent.eventType} ${currentEvent.timeStamp}")
                return true
            }
        }
        return false
    }

    private fun doWork(channel: String) {

        /** Step 0 Refreshing data */
        refreshData()

        /** Step 1 Check application with time exceeded */
        val exceededApp: List<App> = appService.findExceededApplication(apps)

        /** Optimize DB query */
        val isAppReminderEnabled = preferencesService.isAppReminderEnabled()
        val appReminderTime = preferencesService.findAppReminderTimePreference()
        val events = eventService.findAllActive()

        /** Step 2 if app is already notified to user, do nothing */
        exceededApp.forEach { app: App ->

            val event: Event? = eventService.findEventByPackageName(app.packageName, events)

            /** Step 2.1
             * If there's an event but the variable "notified" is false, update event object and then send a notification
             * If there's an event but the variable "notified" is true, check if user is continuing to use the app and if it's true send a notification
             * If there's no event, create one and then send notification
             * */
            var checkSend = true
            val notificationTitle: String = this.resources.getString(R.string.warn_title_notify_app) + " ${app.name}"
            var notificationDescription: String =
                this.resources.getString(R.string.warn_description_notify_app) + " ${app.name}"

            if (event != null) {
                if (!event.notified) {
                    event.notified = true
                    eventService.upsert(event, app.todayUsage)
                } else {
                    val minutesUsed: Int = app.todayUsage - event.usageAtEvent
                    if (isAppReminderEnabled && minutesUsed >= appReminderTime) {
                        eventService.upsert(event, app.todayUsage)
                        notificationDescription =
                            this.resources.getString(R.string.warn_description_notify_app_recheck) + " ${app.name}"
                    } else {
                        /** App is not used anymore for today */
                        checkSend = false
                    }
                }
            } else {
                eventService.insert(
                    Event(
                        null,
                        channel,
                        app.packageName,
                        System.currentTimeMillis(),
                        app.todayUsage,
                        true
                    )
                )
            }
            /** Step 2.2 Send the notification */
            if (checkSend) {
                Log.d("SYT", "Sending notification for $app")
                sendNotification(
                    this, notificationTitle, notificationDescription
                )
            }

            /** Step 3 Block application */
            // TODO
            /** if(isAnApplicationRunningNow(app.packageName)){} */
        }
    }

    override fun onEvent(channel: String) {
        Log.d("SYT", "Background service event received $channel")

        /** remove old events (events from yesterday) */
        eventService.cleanDB()

        for (i in 1..14) {
            doWork(channel)
            Thread.sleep(60000)
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
                    context.resources,
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