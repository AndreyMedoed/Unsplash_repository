package com.example.unsplash.Network

import com.example.flow.JsonAndMovie.Search
import com.example.unsplash.Network.Repository
import com.example.unsplash.fragmens.profile_fragment.Profile
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface GithubApi {

    //Запрос на Инфу о пользователе
    @GET("/me")
    suspend fun getMyProfile(
    ): Profile?

    @GET(".")
    suspend fun getMovies(
        @Query("s") s: String
    ): Search?

    //Запрос на инфу о его репозиториях
    @GET("repositories")
    fun getRepositoryList(): Call<List<Repository>>

    //Проверка отмечен репозиторий звездой или нет
    @GET("/user/starred/{owner}/{repo}")
    fun chekStarStatus(
        @Path("owner") ownerLogin: String,
        @Path("repo") repoName: String
    ): Call<String>

    //Ставим звезду
    @PUT("/user/starred/{owner}/{repo}")
    fun setStar(
        @Path("owner") ownerLogin: String,
        @Path("repo") repoName: String
    ): Call<String>

    //убираем звезду
    @DELETE("/user/starred/{owner}/{repo}")
    fun deleteStar(
        @Path("owner") ownerLogin: String,
        @Path("repo") repoName: String
    ): Call<String>
}