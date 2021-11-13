package com.example.unsplash

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.roomdao.dataBase.Database
import com.example.unsplash.Notifications.NotificationChannels
import com.example.unsplash.utils.ForegroundBackgroundListener

class UnsplashApp : Application(), LifecycleObserver {
    private lateinit var appObserver: ForegroundBackgroundListener

    override fun onCreate() {
        super.onCreate()
        Database.init(this)
        NotificationChannels.create(this)

        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(
                ForegroundBackgroundListener()
                    .also { appObserver = it })
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        // App in foreground
    }
}