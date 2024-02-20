package it.carmelolagamba.saveyourtime.service.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class StartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("SYT", "broadcast receiver service start")
//        NotificationUtils().sendNotification(3000, context)
    }

    companion object {
    }
}
