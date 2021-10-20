package com.skillbox.github.utils


import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response


class CustomInterceptor() : Interceptor {

    private var token: String = ""

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        val modifiedRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        Log.d(
            "UnsplashLogging","CustomInterceptor request is $modifiedRequest")
        val response = chain.proceed(modifiedRequest)

        return response
    }

    fun setToken(newToken: String) {
        token = newToken
    }
}