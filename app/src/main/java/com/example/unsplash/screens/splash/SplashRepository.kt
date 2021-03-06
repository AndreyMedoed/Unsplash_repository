package com.example.unsplash.screens.splash

import android.content.Context
import com.example.unsplash.Network.NetworkConfig
import com.example.unsplash.dataBase.Database
import com.example.unsplash.screens.main.authorization_fragment_.AuthFragment


/**
 * SplashViewModel checks whether user is signed-in or not.
 */
class SplashRepository(
    context: Context
) {
    private val tokenDao = Database.instance.tokenDao()

    suspend fun checkToken(): Boolean {
        val token = tokenDao.getToken(AuthFragment.TOKEN_MARKER) ?: return false
        NetworkConfig.token = token.token

        val response = NetworkConfig.unsplashApi.tokenTest()
        return response.isSuccessful
    }
}