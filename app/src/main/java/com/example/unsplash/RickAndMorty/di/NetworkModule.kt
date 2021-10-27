package com.example.unsplash.RickAndMorty.di

import com.example.rickandmorty.di.Api
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {

    private fun provideOkhttpClient() = OkHttpClient.Builder()
        .addNetworkInterceptor(
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        .followRedirects(true)
        .build()


    private fun provideRetrofit(client: OkHttpClient) = Retrofit.Builder()
        .baseUrl("https://rickandmortyapi.com/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(client)
        .build()

    val api: Api = provideRetrofit(provideOkhttpClient()).create(Api::class.java)

}