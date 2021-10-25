package com.example.unsplash.fragmens.photo_detail_fragment

import com.example.unsplash.data.essences.collection.Collection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.photo.photo_detail.PhotoDetail
import com.example.unsplash.data.essences.user.Profile
import com.example.unsplash.data.essences.user.User
import com.skillbox.github.data.NetworkConfig

class PhotoDetailRepository {

    suspend fun getPhotoDetail(photoId: String) : PhotoDetail? = NetworkConfig.unsplashApi.getPhotoDetails(photoId)

    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }
    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }


}