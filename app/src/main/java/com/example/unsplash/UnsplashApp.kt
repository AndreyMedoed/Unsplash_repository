package com.example.unsplash

import android.app.Application
import com.example.roomdao.dataBase.Database
import com.example.unsplash.Notifications.NotificationChannels

class UnsplashApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Database.init(this)
        NotificationChannels.create(this)
    }
}