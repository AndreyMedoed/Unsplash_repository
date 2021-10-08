package com.example.unsplash.fragmens.profile_fragment

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Profile(
    @Json(name = "id")
    val id: String,
    @Json(name = "updated_at")
    val updated_at: String,
    @Json(name = "username")
    val username: String,
    @Json(name = "first_name")
    val first_name: String
)