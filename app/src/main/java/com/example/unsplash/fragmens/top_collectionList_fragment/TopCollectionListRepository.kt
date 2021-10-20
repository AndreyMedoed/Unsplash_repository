package com.example.unsplash.fragmens.top_collectionList_fragment

import com.example.unsplash.data.Collection
import com.example.unsplash.data.Photo
import com.skillbox.github.data.NetworkConfig

class TopCollectionListRepository {
    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }

    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }

    suspend fun getCollectionPhotos(collectionId: String): List<Photo>? =
        NetworkConfig.unsplashApi.getCollectionPhotos(collectionId)

    suspend fun getTopCollections(): List<Collection>? =
        NetworkConfig.unsplashApi.getTopCollections()

}