package com.example.unsplash.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.roomdao.dataBase.dao.*
import com.example.unsplash.data.essences.Token
import com.example.unsplash.dataBase.dao.*
import com.example.unsplash.dataBase.dataBaseEssences.*
import com.example.unsplash.dataBase.dataBaseEssences.remoteKeys.RemoteKey


@Database(
    entities = [
        CollectionDB::class,
        CollectionLinksDB::class,
        PhotoDB::class,
        PhotoUrlDB::class,
        ProfileDB::class,
        ProfileImageDB::class,
        UserDB::class,
        UserLinksDB::class,
        RemoteKey::class,
        Token::class],
    version = AppDatabase.DB_VERSION
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun collectionDao(): CollectionDao
    abstract fun collectionLinksDao(): CollectionLinksDao
    abstract fun photoDao(): PhotoDao
    abstract fun photoUrlDao(): PhotoUrlDao
    abstract fun profileDao(): ProfileDao
    abstract fun profileImageDao(): ProfileImageDao
    abstract fun userDao(): UserDao
    abstract fun userLinksDao(): UserLinksDao
    abstract fun photoRemoteKeyDao(): RemoteKeyDao
    abstract fun tokenDao(): TokenDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "Unsplash_database"
    }
}