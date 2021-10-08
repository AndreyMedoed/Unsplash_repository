package com.skillbox.github.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User (
    val login: String,
    val id: Long,
    val name: String,
    val avatar_url: String
)

