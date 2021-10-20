package com.example.unsplash.fragmens.top_photo_list_fragment

import androidx.lifecycle.LiveData
import com.example.unsplash.data.Collection
import com.example.unsplash.data.Photo
import com.skillbox.github.data.NetworkConfig

class TopPhotoListRepository {
    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }

    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }

    suspend fun getCollectionPhotos(collectionId: String): List<Photo>? =
        NetworkConfig.unsplashApi.getCollectionPhotos(collectionId)

    suspend fun getTopPhotos(): List<Photo>? =
        NetworkConfig.unsplashApi.getTopPhotos("1")


}