package com.example.unsplash.data.adapters

import com.example.roomdao.dataBase.Database
import com.example.unsplash.data.essences.photo.PhotoUrl
import com.example.unsplash.dataBase.dataBaseEssences.PhotoUrlDB

class DatabasePhotoUrlAdapter {
    private val photoUrlDao = Database.instance.photoUrlDao()

    suspend fun fromPhotoUrlToDBPhotoUrl(photoUrl: PhotoUrl): Long? {
        val photoUrlDB = PhotoUrlDB(
            id = 0,
            raw = photoUrl.raw,
            full = photoUrl.full,
            regular = photoUrl.regular,
            small = photoUrl.small,
            thumb = photoUrl.thumb
        )
        photoUrlDao.insertPhotoUrl(photoUrlDB)
        val photoUrlDBWithId = photoUrlDao.getPhotoUrl(photoUrl.small, photoUrl.full)

        return photoUrlDBWithId?.id
    }


    suspend fun fromDBPhotoUrlToPhotoUrl(photoUrlDB_id: Long): PhotoUrl? {
        val photoUrlDB = photoUrlDao.getPhotoUrlById(photoUrlDB_id)

        return photoUrlDB?.let {
            PhotoUrl(
                raw = photoUrlDB.raw,
                full = photoUrlDB.full,
                regular = photoUrlDB.regular,
                small = photoUrlDB.small,
                thumb = photoUrlDB.thumb
            )
        }
    }

}