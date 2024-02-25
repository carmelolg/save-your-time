package it.carmelolagamba.saveyourtime.service.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import it.carmelolagamba.saveyourtime.R
import it.carmelolagamba.saveyourtime.service.streaming.EventNotifier
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * @author carmelolg
 * @since version 1.0
 */
class SYTWorker @Inject constructor(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        for (i in 1..30) {
            delay(30000)
            innerDoWork()
        }
        return Result.success()
    }

    /**
     * The worker emit a new event to all listener every 30 seconds
     * The event check_notify it's used for checking app with time exceeded
     */
    private fun innerDoWork() {
        Log.d(
            "SYT",
            "Notification Worker emit event " + context.resources.getString(R.string.check_notify)
        )
        val eventBroadcaster = EventNotifier.getInstance()
        eventBroadcaster.notifyEvent(context.resources.getString(R.string.check_notify))
    }


}

