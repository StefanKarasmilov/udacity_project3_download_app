package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat

// Notification ID.
private val NOTIFICATION_ID = 10
private val REQUEST_CODE = 0
private val FLAGS = 0

val DOWNLOAD_ID_EXTRA = "id"
val DOWNLOAD_FILE_NAME_EXTRA = "name"

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, downloadId: Long, title: String) {
    // Create the content intent for the notification, which launches
    // this activity
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(DOWNLOAD_ID_EXTRA, downloadId)
    contentIntent.putExtra(DOWNLOAD_FILE_NAME_EXTRA, title)

    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val cloudImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.download_cloud
    )

    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(cloudImage)
        .bigLargeIcon(null)

    val builder = NotificationCompat.Builder(
        applicationContext,
        MainActivity.CHANNEL_ID
    )

        .setSmallIcon(R.drawable.ic_cloud_download)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)

        .setContentIntent(pendingIntent)
        .setStyle(bigPicStyle)
        .setLargeIcon(cloudImage)
        .addAction(
            R.drawable.ic_play_arrow,
            applicationContext.getString(R.string.detail),
            pendingIntent
        )
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())

}