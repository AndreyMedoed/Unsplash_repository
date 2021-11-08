package com.example.unsplash.Notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat

object NotificationChannels {

    const val NEWS_CHANNEL_ID = "news"

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNewsChannel(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNewsChannel(context: Context) {
        val name = "News"
        val channelDescription = "App news"
        val priority = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(NEWS_CHANNEL_ID, name, priority).apply {
            description = channelDescription
        }

        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }


}