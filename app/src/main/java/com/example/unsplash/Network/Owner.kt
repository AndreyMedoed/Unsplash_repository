package com.skillbox.github.data

import com.squareup.moshi.JsonClass

//Чтоб вытащить имя владельца репозитория
@JsonClass(generateAdapter = true)
data class Owner(
    val login: String
)