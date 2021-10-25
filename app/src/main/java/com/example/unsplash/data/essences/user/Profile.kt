package com.example.unsplash.data.essences.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Profile(
    @Json(name = "id")
    val id: String,
    @Json(name = "updated_at")
    val updated_at: String?,
    @Json(name = "username")
    val username: String,
    @Json(name = "first_name")
    val first_name: String?,
    @Json(name = "last_name")
    val last_name: String?,
    @Json(name = "bio")
    val bio: String?,
    @Json(name = "location")
    val location: String?,
    @Json(name = "total_likes")
    val total_likes: Int?,
    @Json(name = "total_photos")
    val total_photos: Int?,
    @Json(name = "total_collections")
    val total_collections: Int?,
    @Json(name = "followed_by_user")
    val followed_by_user: Boolean?,
    @Json(name = "downloads")
    val downloads: Int?,
    @Json(name = "email")
    val email: String?,
    @Json(name = "links")
    val userLinks: UserLinks?
)