package com.mcafi.calcetto.src

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class MatchNotificationManager : BroadcastReceiver() {

    companion object {
        const val MATCH_CHANNEL_NAME = "Notifiche partite"
        const val MATCH_CHANNEL_DESCRIPTION = "Notifiche partite in arrivo"
        const val MATCH_CHANNEL_ID = "MATCH_CHANNEL"
    }

    override fun onReceive(context: Context, intent: Intent) {

        Log.d("T", "Ricevuto!")

        val notification = intent.getParcelableExtra("NOTIFICATION") as Notification
        val notificatonId = intent.getIntExtra("NOTIFICATION_ID", 1)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(MATCH_CHANNEL_ID, MATCH_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply { description = MATCH_CHANNEL_DESCRIPTION }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(context)) {
            notify(notificatonId, notification)
        }
    }
}