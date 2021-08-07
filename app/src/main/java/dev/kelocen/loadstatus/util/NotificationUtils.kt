package dev.kelocen.loadstatus.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat

private lateinit var notificationManager: NotificationManager
private lateinit var pendingIntent: PendingIntent
private lateinit var action: NotificationCompat.Action

/**
 * TODO
 */
fun NotificationManager.sendNotification(message: String, applicationContext: Context){

}

/**
 * TODO
 */
fun NotificationManager.cancelNotifications(){
    cancelAll()
}