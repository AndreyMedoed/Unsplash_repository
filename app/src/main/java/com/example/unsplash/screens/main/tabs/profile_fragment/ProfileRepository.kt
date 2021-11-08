package com.example.unsplash.screens.splash.fragmens.profile_fragment

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.roomdao.dataBase.Database
import com.example.unsplash.data.essences.collection.Collection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.user.Profile
import com.example.unsplash.data.essences.user.User
import com.example.unsplash.paging.CollectionRemoteMediator
import com.example.unsplash.paging.PhotoRemoteMediator
import com.skillbox.github.data.NetworkConfig

class ProfileRepository {
    private val photoDao = Database.instance.photoDao()
    private val collectionDao = Database.instance.collectionDao()


    suspend fun getMyProfile(): Profile? = NetworkConfig.unsplashApi.getMyProfile()

    suspend fun getUser(username: String): User? = NetworkConfig.unsplashApi.getUser(username)

    suspend fun getMyPhotos(username: String): List<Photo>? =
        NetworkConfig.unsplashApi.getMyPhotoList(username)

    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }

    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }

    suspend fun getMyLikeList(username: String): List<Photo>? =
        NetworkConfig.unsplashApi.getMyLikesList(username)

    suspend fun getMyCollections(username: String): List<Collection>? =
        NetworkConfig.unsplashApi.getMyCollections(username)

    suspend fun getCollectionPhotos(collectionId: String): List<Photo>? =
        NetworkConfig.unsplashApi.getCollectionPhotos(collectionId)


    @ExperimentalPagingApi
    fun postsOfPhotos(marker: String, pageSize: Int) = Pager(
        config = PagingConfig(pageSize),
        remoteMediator = PhotoRemoteMediator(Database, NetworkConfig.unsplashApi, marker, pageSize)
    ) {
        photoDao.postsByTopPhotos(marker)
    }.flow

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