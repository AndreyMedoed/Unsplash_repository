package com.example.rickandmorty.di

import com.example.rickandmorty.objects.CharacterResponse
import com.example.unsplash.data.essences.photo.Photo
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("character")
    suspend fun getResponse(): CharacterResponse

    @GET("/photos")
    suspend fun getTopPhotos(
        @Query("page") page: String,
        @Query("order_by") order_by: String = "popular"
    ): List<Photo>?
}