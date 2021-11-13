package com.example.unsplash.screens.splash.fragmens.top_collectionList_fragment

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.roomdao.dataBase.Database
import com.example.unsplash.data.essences.collection.Collection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.paging.CollectionRemoteMediator
import com.skillbox.github.data.NetworkConfig

class TopCollectionListRepository {
    private val collectionDao = Database.instance.collectionDao()

    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }

    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }

    suspend fun getCollectionPhotos(collectionId: String): List<Photo>? =
        NetworkConfig.unsplashApi.getCollectionPhotos(collectionId)


    @ExperimentalPagingApi
    fun postsOfCollections(marker: String, pageSize: Int) = Pager(
        config = PagingConfig(pageSize),
        remoteMediator = CollectionRemoteMediator(
            Database,
            NetworkConfig.unsplashApi,
            marker,
            pageSize
        )
    ) {
        collectionDao.postsByTopCollections(marker)
    }.flow

}