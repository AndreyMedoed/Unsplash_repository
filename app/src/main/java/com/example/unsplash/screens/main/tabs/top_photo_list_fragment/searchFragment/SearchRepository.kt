package com.example.unsplash.screens.main.tabs.top_photo_list_fragment.searchFragment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.unsplash.paging.PhotoPagingSource
import com.example.unsplash.Network.NetworkConfig

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