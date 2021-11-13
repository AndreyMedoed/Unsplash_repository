package com.example.unsplash.screens.main.tabs.top_photo_list_fragment.searchFragment

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.roomdao.dataBase.Database
import com.example.unsplash.paging.PhotoPagingSource
import com.skillbox.github.data.NetworkConfig

class SearchRepository {

    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }

    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }


    fun searchPhotos(query: String) = Pager(
        config = PagingConfig(25)
    ) { PhotoPagingSource(NetworkConfig.unsplashApi, query) }.flow

}