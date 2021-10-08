package com.example.unsplash.fragmens.profile_fragment

import android.util.Log
import com.skillbox.github.data.AuthConfig

class ProfileRepository {

    suspend fun getMyProfile(): Profile {
       // val res = AuthConfig.githubApi.getMovies("love")
        val res = AuthConfig.githubApi.getMyProfile()

        Log.d("UnsplashLogging", "responseBody is  ${res}")
        return Profile("asdf", "asdf", "asdf", "asdf")
    }
}