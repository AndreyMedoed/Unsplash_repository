package com.example.unsplash.screens.main.authorization_fragment_

import android.net.Uri
import android.util.Log
import com.skillbox.github.data.NetworkConfig
import net.openid.appauth.*

class AuthRepository {


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