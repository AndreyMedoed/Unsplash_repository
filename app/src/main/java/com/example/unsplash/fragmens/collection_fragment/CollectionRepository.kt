package com.example.unsplash.fragmens.collection_fragment

import com.example.unsplash.data.Photo
import com.skillbox.github.data.NetworkConfig

class CollectionRepository {

    suspend fun getCollectionPhotos(collectionId: String): List<Photo>? =
        NetworkConfig.unsplashApi.getCollectionPhotos(collectionId)

    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }
    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }
}