package com.example.unsplash.screens.main.tabs.profile_fragment


import androidx.room.withTransaction
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.user.Profile
import com.example.unsplash.data.essences.user.User
import com.example.unsplash.Network.NetworkConfig
import com.example.unsplash.dataBase.Database

class ProfileRepository {

    private val collectionDao = Database.instance.collectionDao()
    private val collectionLinksDao = Database.instance.collectionLinksDao()
    private val photoDao = Database.instance.photoDao()
    private val photoUrlDao = Database.instance.photoUrlDao()
    private val profileDao = Database.instance.profileDao()
    private val profileImageDao = Database.instance.profileImageDao()
    private val remoteKeyDao = Database.instance.remoteKeyDao()
    private val tokenDao = Database.instance.tokenDao()
    private val userDao = Database.instance.userDao()
    private val userLinksDao = Database.instance.userLinksDao()

    suspend fun getMyProfile(): Profile? = NetworkConfig.unsplashApi.getMyProfile()

    suspend fun getUser(username: String): User? = NetworkConfig.unsplashApi.getUser(username)

    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }

    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }

    suspend fun getCollectionPhotos(collectionId: String): List<Photo>? =
        NetworkConfig.unsplashApi.getCollectionPhotos(collectionId)

    suspend fun clearAllDatabase() {
        Database.instance.withTransaction {
            collectionDao.clearAll()
            collectionLinksDao.clearAll()
            photoDao.clearAll()
            profileDao.clearAll()
            remoteKeyDao.clearAll()
            tokenDao.clearAll()
            userDao.clearAll()
            userLinksDao.clearAll()
            photoUrlDao.clearAll()
            profileImageDao.clearAll()
        }
    }

}