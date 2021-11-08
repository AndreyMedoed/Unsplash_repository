package com.example.unsplash.data.adapters

import com.example.roomdao.dataBase.Database
import com.example.unsplash.data.essences.photo.Downloads
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.photo.Statistics
import com.example.unsplash.dataBase.adapters.DatabaseUserAdapter
import com.example.unsplash.dataBase.dataBaseEssences.PhotoDB

class DatabasePhotoAdapter {
    private val photoDao = Database.instance.photoDao()
    private val databasePhotoUrlAdapter = DatabasePhotoUrlAdapter()
    private val databaseUserAdapter = DatabaseUserAdapter()

    suspend fun fromPhotoToDBPhoto(photo: Photo, mark: String?): String {
        val photoDB = PhotoDB(
            id = photo.id,
            description = photo.description,
            photo_urls_id = photo.urls?.let {
                databasePhotoUrlAdapter.fromPhotoUrlToDBPhotoUrl(it)
            },
            likes = photo.likes,
            liked_by_user = photo.liked_by_user,
            user_id = photo.user?.let {
                databaseUserAdapter.fromUserToDBUser(it, null)
            },
            total_downloads = photo.statistics?.downloads?.total,
            mark = mark
        )
        photoDao.insertPhoto(photoDB)
        return photo.id
    }


    suspend fun fromDBPhotoToPhoto(photoDB_id: String): Photo? {
        val photoDB = photoDao.getPhotoById(photoDB_id)
        return photoDB?.let {
            Photo(
                id = photoDB.id,
                description = photoDB.description,
                urls = photoDB.photo_urls_id?.let {
                    databasePhotoUrlAdapter.fromDBPhotoUrlToPhotoUrl(it)
                },
                likes = photoDB.likes,
                liked_by_user = photoDB.liked_by_user,
                user = photoDB.user_id?.let {
                    databaseUserAdapter.fromDBUserToUser(it)
                },
                statistics = Statistics(Downloads(photoDB.total_downloads))
            )
        }
    }

    suspend fun deletePhotosByMarker(marker: String){
        photoDao.deleteByMarker(marker)
    }

}