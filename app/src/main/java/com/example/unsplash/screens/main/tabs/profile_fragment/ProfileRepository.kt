package com.example.unsplash.screens.main.tabs.profile_fragment


import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.user.Profile
import com.example.unsplash.data.essences.user.User
import com.skillbox.github.data.NetworkConfig

class ProfileRepository {

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

}