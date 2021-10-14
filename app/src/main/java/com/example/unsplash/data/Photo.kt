package com.example.unsplash.data

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Photo(
    @Json(name = "id")
    val id: String,
    @Json(name = "description")
    val description: String?,
    @Json(name = "urls")
    val urls: PhotoUrl,
    @Json(name = "likes")
    var likes: Int?,
    @Json(name = "liked_by_user")
    var liked_by_user: Boolean,
    @Json(name = "user")
    val user: User?,
    @Json(name = "statistics")
    val statistics: Statistics?,
): PhotoAndCollection(), Parcelable