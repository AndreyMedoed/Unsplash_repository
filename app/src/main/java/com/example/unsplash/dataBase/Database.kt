package com.example.unsplash.dataBase

import android.content.Context
import androidx.room.Room

object Database {

    lateinit var instance: AppDatabase
        private set

    fun init(context: Context) {
        instance =  Room.databaseBuilder(context,
        AppDatabase::class.java,
        AppDatabase.DB_NAME)
            .build()
    }
}