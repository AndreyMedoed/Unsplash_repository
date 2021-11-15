package com.example.unsplash.Network


import com.example.unsplash.data.essences.SearchResult
import com.example.unsplash.data.essences.collection.Collection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.photo.photo_detail.PhotoDetail
import com.example.unsplash.data.essences.user.Profile
import com.example.unsplash.data.essences.user.User
import okhttp3.ResponseBody

import retrofit2.Response
import retrofit2.http.*

interface UnsplashApi {

    //Запрос на Инфу о пользователе
    @GET("/me")
    suspend fun getMyProfile(
    ): Profile?

    @GET("/users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): User?

    @GET("/users/{username}/photos")
    suspend fun getMyPhotoList(
        @Path("username") username: String,
        @Query("stats") stats: Boolean = true
    ): List<Photo>?


    @POST("/photos/{photoId}/like")
    suspend fun setLike(
        @Path("photoId") photoId: String
    ): Response<Unit>

    @DELETE("/photos/{photoId}/like")
    suspend fun deleteLike(
        @Path("photoId") photoId: String
    ): Response<Unit>


    @GET("/users/{username}/likes")
    suspend fun getMyLikesList(
        @Path("username") username: String
    ): List<Photo>?

    @GET("/users/{username}/collections")
    suspend fun getMyCollections(
        @Path("username") username: String
    ): List<Collection>?


    @GET("/collections/{collectionId}/photos")
    suspend fun getCollectionPhotos(
        @Path("collectionId") collectionId: String
    ): List<Photo>?

    @GET("/collections")
    suspend fun getTopCollections(): List<Collection>?


    @GET("/photos")
    suspend fun getTopPhotos(
        @Query("page") page: String? = null,
        @Query("per_page") pageSize: String? = "25",
        @Query("order_by") order_by: String = "popular"
    ): Response<List<Photo>>

    @GET("/photos/{photoId}")
    suspend fun getPhotoDetails(
        @Path("photoId") photoId: String
    ): PhotoDetail?

    @GET
    suspend fun downloadPhoto(
        @Url
        url: String
    ): ResponseBody


    @GET
    suspend fun getPagingPhotoContent(
        @Url url: String,
        @Query("page") page: String? = null,
        @Query("per_page") pageSize: String? = "25",
        @Query("order_by") order_by: String? = null
    ): Response<List<Photo>>

    @GET
    suspend fun getPagingCollectionContent(
        @Url url: String,
        @Query("page") page: String? = null,
        @Query("per_page") pageSize: String? = "15",
        @Query("order_by") order_by: String? = null
    ): Response<List<Collection>>



    @GET("/search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: String? = null,
        @Query("per_page") pageSize: String? = "25"
    ): Response<SearchResult>

    @GET("/photos")
    suspend fun tokenTest(
        @Query("page") page: String = "1",
        @Query("per_page") pageSize: String = "2"
    ): Response<List<Photo>>

}