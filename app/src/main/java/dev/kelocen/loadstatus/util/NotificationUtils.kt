package dev.kelocen.loadstatus.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import dev.kelocen.loadstatus.R
import dev.kelocen.loadstatus.main.MainActivity
import dev.kelocen.loadstatus.util.Constants.NOTIFICATION_ID

private lateinit var pendingIntent: PendingIntent

/**
 * Sends a notification with the given message and [Context].
 */
fun NotificationManager.sendNotification(message: String, applicationContext: Context) {
    val downloadIntent = Intent(applicationContext, MainActivity::class.java)
    pendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, downloadIntent,
            PendingIntent.FLAG_IMMUTABLE)
    notify(NOTIFICATION_ID, getNotification(message, applicationContext).build())
}

/**
 * Returns a [NotificationCompat.Builder] with the given message and context.
 */
private fun getNotification(message: String,
                            applicationContext: Context): NotificationCompat.Builder {
    return NotificationCompat.Builder(applicationContext,
            applicationContext.getString(R.string.notification_channel_id))
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(message)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
}

/**
 * Creates a [NotificationChannel] with the given [Context], channel ID, and channel name.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun createChannel(context: Context, channelId: String, channelName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(channelId, channelName,
                NotificationManager.IMPORTANCE_HIGH).apply {
            setShowBadge(true)
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

/**
 * Cancels all previous notifications.
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}