package it.carmelolagamba.saveyourtime.service.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * @author carmelolg
 * @since version 1.0
 */
class StartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("SYT", "broadcast receiver service start")
    }

    companion object {
    }
}
