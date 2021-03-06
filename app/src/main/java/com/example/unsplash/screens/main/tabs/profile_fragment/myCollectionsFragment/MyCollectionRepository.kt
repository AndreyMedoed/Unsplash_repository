package com.example.unsplash.screens.main.tabs.profile_fragment.myCollectionsFragment

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.unsplash.dataBase.Database
import com.example.unsplash.paging.CollectionRemoteMediator
import com.example.unsplash.Network.NetworkConfig

class MyCollectionRepository(private val context: Context) {
    private val collectionDao = Database.instance.collectionDao()

    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }

    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }


    @ExperimentalPagingApi
    fun postsOfCollections(marker: String, pageSize: Int) = Pager(
        config = PagingConfig(pageSize),
        remoteMediator = CollectionRemoteMediator(
            Database,
            NetworkConfig.unsplashApi,
            marker,
            pageSize,
            context
        )
    ) {
        collectionDao.postsByTopCollections(marker)
    }.flow


}