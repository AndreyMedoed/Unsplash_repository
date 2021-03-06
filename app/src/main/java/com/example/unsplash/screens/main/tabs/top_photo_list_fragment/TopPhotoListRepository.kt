package com.example.unsplash.screens.main.tabs.top_photo_list_fragment

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.unsplash.dataBase.Database
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.paging.PhotoRemoteMediator
import com.example.unsplash.Network.NetworkConfig

class TopPhotoListRepository(private val context: Context) {
    private val photoDao = Database.instance.photoDao()

    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }

    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }

    suspend fun getCollectionPhotos(collectionId: String): List<Photo>? =
        NetworkConfig.unsplashApi.getCollectionPhotos(collectionId)


    @ExperimentalPagingApi
    fun postsOfPhotos(marker: String, pageSize: Int) = Pager(
        config = PagingConfig(pageSize),
        remoteMediator = PhotoRemoteMediator(Database, NetworkConfig.unsplashApi, marker, pageSize,context)
    ) {
        photoDao.postsByTopPhotos(marker)
    }.flow


}