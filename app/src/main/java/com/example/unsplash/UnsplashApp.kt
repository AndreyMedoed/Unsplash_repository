package com.example.unsplash

import android.app.Application
import com.example.roomdao.dataBase.Database

class UnsplashApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Database.init(this)
    }
}