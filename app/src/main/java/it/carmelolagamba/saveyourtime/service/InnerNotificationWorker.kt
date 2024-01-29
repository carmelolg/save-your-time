package it.carmelolagamba.saveyourtime.service

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import javax.inject.Inject

class InnerNotificationWorker @Inject constructor(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {

    @Inject
    lateinit var innerNotificationManager: InnerNotificationManager
    override fun doWork(): Result {
        innerDoWork()
        return Result.success()
    }

    private fun innerDoWork() {
        Log.i("Worker X", "Entro")
        /**
        innerNotificationManager.createNotificationChannel(context)
        innerNotificationManager.sendNotification(context, "Notifica", "Descrizione")
        */
    }
}