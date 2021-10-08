package com.skillbox.github.data

import com.example.networking.CustomInterceptorMovie
import com.example.unsplash.Network.GithubApi
import com.skillbox.github.data.AuthConfig.customInterceptor
import com.skillbox.github.utils.CustomInterceptor
import net.openid.appauth.ResponseTypeValues
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

object AuthConfig {

    const val API_KEY = "e0f16b8"

    private val customInterceptor: CustomInterceptor by lazy { CustomInterceptor() }

    var token = ""
        set(value) {
            customInterceptor.setToken(value)
            field = value
        }

    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        ).addInterceptor(customInterceptor)
//        .addInterceptor(
//            CustomInterceptorMovie(API_KEY)
//        )
        .build()

    private var retrofit = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com/")
        //.baseUrl("http://www.omdbapi.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okHttpClient)
        .build()

    val githubApi: GithubApi
        get() = retrofit.create()


    const val AUTH_URI = "https://unsplash.com/oauth/authorize"
    const val TOKEN_URI = "https://unsplash.com/oauth/token"
    const val RESPONSE_TYPE = ResponseTypeValues.CODE

    const val SCOPE_PUPLIC = "public"
    const val SCOPE_READ_USER = "read_user"
    const val write_user = "write_user"
    const val read_photos = "read_photos"
    const val write_photos = "write_photos"
    const val write_likes = "write_likes"
    const val write_followers = "write_followers"
    const val read_collections = "read_collections"
    const val write_collections = "write_collections"

    const val CLIENT_ID = "mKFi8dQZY4xfESMojSJ8i2tYH3RdCJN_UaUDdd78FcQ"
    const val CLIENT_SECRET = "I4FQa5hQeuzz7RBr9iv5yqPOFPlLmtYZGSpwgzI8CWk"
    const val CALLBACK_URL = "com.example.unsplash://lisbv"
}