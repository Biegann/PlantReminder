package com.example.plantreminder.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES.R
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/*

fun showNotifications(context: Context, title: String, content: String) {
    val channelId = "plant_reminder_channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Plant Reminder"
        val descriptionText = "Plant reminder notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
*/
