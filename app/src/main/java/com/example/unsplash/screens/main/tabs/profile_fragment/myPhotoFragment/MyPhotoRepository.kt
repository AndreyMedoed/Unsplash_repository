package com.example.unsplash.screens.splash.fragmens.profile_fragment.myPhotoFragment

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.unsplash.dataBase.Database
import com.example.unsplash.paging.PhotoRemoteMediator
import com.example.unsplash.Network.NetworkConfig

class MyPhotoRepository {
    private val photoDao = Database.instance.photoDao()

    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }

    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }


    @ExperimentalPagingApi
    fun postsOfPhotos(marker: String, pageSize: Int) = Pager(
        config = PagingConfig(pageSize),
        remoteMediator = PhotoRemoteMediator(Database, NetworkConfig.unsplashApi, marker, pageSize)
    ) {
        photoDao.postsByTopPhotos(marker)
    }.flow



}