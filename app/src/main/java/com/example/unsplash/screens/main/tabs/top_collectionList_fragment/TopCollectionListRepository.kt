package com.example.unsplash.screens.main.tabs.top_collectionList_fragment

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.unsplash.dataBase.Database
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.paging.CollectionRemoteMediator
import com.example.unsplash.Network.NetworkConfig

class TopCollectionListRepository(private val context: Context) {
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
            pageSize,context
        )
    ) {
        collectionDao.postsByTopCollections(marker)
    }.flow

}