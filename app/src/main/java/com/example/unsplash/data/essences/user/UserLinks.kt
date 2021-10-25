package com.example.unsplash.data.essences.user

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserLinks(
    @Json(name = "self")
    val self: String?,
    @Json(name = "html")
    val html: String?,
    @Json(name = "photos")
    val photos: String?,
    @Json(name = "likes")
    val likes: String?,
    @Json(name = "portfolio")
    val portfolio: String?
) : Parcelable