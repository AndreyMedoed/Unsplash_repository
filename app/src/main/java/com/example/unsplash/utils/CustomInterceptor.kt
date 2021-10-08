package com.skillbox.github.utils


import okhttp3.Interceptor
import okhttp3.Response

/** Тут я добавляю заголовок авторизации с токеном. В задании есть с сылка на сайт, но там написано вот это:
 * $ curl -H "Authorization: token OAUTH-TOKEN" https://api.github.com
 * Долго думал что это значит, и в итоге решил, что нужнго сделать так -> */
class CustomInterceptor() : Interceptor {

    private var token: String = ""

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        val modifiedRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        val response = chain.proceed(modifiedRequest)

        return response
    }

    fun setToken(newToken: String) {
        token = newToken
    }
}