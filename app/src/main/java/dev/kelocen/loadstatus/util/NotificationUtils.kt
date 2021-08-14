package dev.kelocen.loadstatus.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import dev.kelocen.loadstatus.R
import dev.kelocen.loadstatus.detail.DetailActivity
import dev.kelocen.loadstatus.util.Constants.NOTIFICATION_ID

/**
 * Sends a notification with the given message and [Context].
 */
fun NotificationManager.sendNotification(message: String, applicationContext: Context) {
    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    val notificationAction = NotificationCompat.Action(null,
            applicationContext.getString(R.string.notification_button), pendingIntent)
    notify(NOTIFICATION_ID,
            getNotification(message, applicationContext, notificationAction, pendingIntent))
}

/**
 * Returns a [Notification] with the given message, context, action, and intent.
 */
private fun getNotification(message: String, applicationContext: Context,
                            notificationAction: NotificationCompat.Action,
                            pendingIntent: PendingIntent): Notification {
    return NotificationCompat.Builder(applicationContext,
            applicationContext.getString(R.string.notification_channel_id))
        .setSmallIcon(R.drawable.ic_download_assistant)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(message)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.DEFAULT_ALL)
        .addAction(notificationAction).build()
}

/**
 * Creates a [NotificationChannel] with the given [Context], channel ID, and channel name.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun createChannel(context: Context, channelId: String, channelName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(channelId, channelName,
                NotificationManager.IMPORTANCE_DEFAULT).apply {
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