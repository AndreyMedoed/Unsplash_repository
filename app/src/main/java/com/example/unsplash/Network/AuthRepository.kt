package com.example.unsplash.Network

import android.net.Uri
import android.util.Log
import com.skillbox.github.data.AuthConfig
import net.openid.appauth.*
import java.lang.Exception

class AuthRepository {


    fun getAuthRequest(): AuthorizationRequest {
        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(AuthConfig.AUTH_URI),
            Uri.parse(AuthConfig.TOKEN_URI)
        )

        val redirectUri = Uri.parse(AuthConfig.CALLBACK_URL)

        return AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID,
            AuthConfig.RESPONSE_TYPE,
            redirectUri
        )

//            .setScope(AuthConfig.SCOPE_PUPLIC)
//            .setScope(AuthConfig.write_user)
//            .setScope(AuthConfig.read_photos)
//            .setScope(AuthConfig.write_photos)
//            .setScope(AuthConfig.write_likes)
//            .setScope(AuthConfig.write_followers)
//            .setScope(AuthConfig.read_collections)
//            .setScope(AuthConfig.write_collections)
            .setScope(AuthConfig.SCOPE_READ_USER)
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
                    AuthConfig.token = accessToken
                    Log.d("UnsplashLogging", "accessToken$accessToken")
                    onComplete()
                }
                else -> onError(ex)
            }
        }
    }


    private fun getClientAuthentication(): ClientAuthentication {
        return ClientSecretPost(AuthConfig.CLIENT_SECRET)
    }
}