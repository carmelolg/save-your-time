package it.carmelolagamba.saveyourtime.service.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import it.carmelolagamba.saveyourtime.R
import java.util.concurrent.TimeUnit

/**
 * @author carmelolg
 * @since version 1.0
 */
class SYTReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val message = intent?.getStringExtra("message") ?: return
        Log.d("SYT", "broadcast receiver service start: $message")

        WorkManager.getInstance(context).cancelAllWork()
        val inputData = Data.Builder()

        val notificationWorker: WorkRequest = PeriodicWorkRequestBuilder<SYTWorker>(
            context.resources.getInteger(
                R.integer.job_time
            ).toLong(), TimeUnit.MINUTES
        ).setInputData(inputData.build()).build()
        WorkManager.getInstance(context).enqueue(notificationWorker)
    }

    companion object {
    }
}
