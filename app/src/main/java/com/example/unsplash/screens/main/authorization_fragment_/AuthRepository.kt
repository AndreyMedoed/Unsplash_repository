package com.example.unsplash.screens.main.authorization_fragment_

import android.net.Uri
import android.util.Log
import com.example.unsplash.data.essences.Token
import com.example.unsplash.dataBase.Database
import com.example.unsplash.Network.NetworkConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.openid.appauth.*

class AuthRepository {
    private val tokenDao = Database.instance.tokenDao()
    private val coroutine = CoroutineScope(Dispatchers.IO)

    fun getAuthRequest(): AuthorizationRequest {
        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(NetworkConfig.AUTH_URI),
            Uri.parse(NetworkConfig.TOKEN_URI)
        )

        val redirectUri = Uri.parse(NetworkConfig.CALLBACK_URL)

        return AuthorizationRequest.Builder(
            serviceConfiguration,
            NetworkConfig.CLIENT_ID,
            NetworkConfig.RESPONSE_TYPE,
            redirectUri
        )
            .setScope(NetworkConfig.ALL_SCOPES)
            .setCodeVerifier(null)
            .build()

    }

    fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
        onComplete: () -> Unit,
        onError: (ex: AuthorizationException?) -> Unit
    ) {
        authService.performTokenRequest(tokenRequest, getClientAuthentication()) { response, ex ->
            when {
                response != null -> {
                    val accessToken = response.accessToken.orEmpty()
                    NetworkConfig.token = accessToken

                    coroutine.launch {
                        tokenDao.insert(Token(AuthFragment.TOKEN_MARKER, accessToken))
                    }
                    Log.d("UnsplashLogging", "accessToken$accessToken")
                    onComplete()
                }
                else -> onError(ex)
            }
        }
    }

    private fun getClientAuthentication(): ClientAuthentication {
        return ClientSecretPost(NetworkConfig.CLIENT_SECRET)
    }
}