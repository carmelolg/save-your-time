package it.carmelolagamba.saveyourtime.service.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import it.carmelolagamba.saveyourtime.R
import it.carmelolagamba.saveyourtime.service.streaming.EventNotifier
import javax.inject.Inject

/**
 * @author carmelolg
 * @since version 1.0
 */
class InnerNotificationWorker @Inject constructor(val context: Context,
                                                  workerParams: WorkerParameters): Worker(context, workerParams) {

    override fun doWork(): Result {
        innerDoWork()
        return Result.success()
    }

    /**
     * The worker emit a new event to all listener every 15 minutes
     * The event check_notify it's used for checking app with time exceeded
     */
    private fun innerDoWork() {
        Log.d("SYT", "Notification Worker emit event " + context.resources.getString(R.string.check_notifiy))
        val eventBroadcaster = EventNotifier.getInstance()
        eventBroadcaster.notifyEvent(context.resources.getString(R.string.check_notifiy))
    }


}

