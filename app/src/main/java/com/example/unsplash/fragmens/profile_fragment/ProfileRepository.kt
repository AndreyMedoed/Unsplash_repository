package com.example.unsplash.fragmens.profile_fragment

import com.example.unsplash.data.Collection
import com.example.unsplash.data.Photo
import com.example.unsplash.data.Profile
import com.example.unsplash.data.User
import com.skillbox.github.data.NetworkConfig

class ProfileRepository {

    suspend fun getMyProfile(): Profile? = NetworkConfig.unsplashApi.getMyProfile()

    suspend fun getUser(username: String): User? = NetworkConfig.unsplashApi.getUser(username)

    suspend fun getMyPhotos(username: String): List<Photo>? =
        NetworkConfig.unsplashApi.getMyPhotoList(username)

    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }
    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }

    suspend fun getMyLikeList(username: String): List<Photo>? =
        NetworkConfig.unsplashApi.getMyLikesList(username)

    suspend fun getMyCollections(username: String): List<Collection>? =
        NetworkConfig.unsplashApi.getMyCollections(username)

    suspend fun getCollectionPhotos(collectionId: String): List<Photo>? =
        NetworkConfig.unsplashApi.getCollectionPhotos(collectionId)

}