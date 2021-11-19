package com.example.unsplash.Network

import com.skillbox.github.utils.CustomInterceptor
import net.openid.appauth.ResponseTypeValues
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

object NetworkConfig {


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
        .build()

    private var retrofit = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okHttpClient)
        .build()

    val unsplashApi: UnsplashApi
        get() = retrofit.create()


    const val AUTH_URI = "https://unsplash.com/oauth/authorize"
    const val TOKEN_URI = "https://unsplash.com/oauth/token"
    const val RESPONSE_TYPE = ResponseTypeValues.CODE
    const val ALL_SCOPES = "public read_user write_user read_photos write_photos write_likes write_followers read_collections write_collections"
    const val CLIENT_ID = "mKFi8dQZY4xfESMojSJ8i2tYH3RdCJN_UaUDdd78FcQ"
    const val CLIENT_SECRET = "I4FQa5hQeuzz7RBr9iv5yqPOFPlLmtYZGSpwgzI8CWk"
    const val CALLBACK_URL = "com.example.unsplash://lisbv"
}