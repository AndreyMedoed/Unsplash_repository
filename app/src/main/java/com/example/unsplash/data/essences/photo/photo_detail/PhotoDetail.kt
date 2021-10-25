package com.example.unsplash.data.essences.photo.photo_detail

import android.os.Parcelable
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.data.essences.photo.PhotoUrl
import com.example.unsplash.data.essences.photo.Statistics
import com.example.unsplash.data.essences.user.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class PhotoDetail(
    @Json(name = "id")
    val id: String,
    @Json(name = "description")
    val description: String?,
    @Json(name = "urls")
    val urls: PhotoUrl?,
    @Json(name = "likes")
    var likes: Int?,
    @Json(name = "liked_by_user")
    var liked_by_user: Boolean,
    @Json(name = "user")
    val user: User?,
    @Json(name = "exif")
    val exif: Exif?,
    @Json(name = "location")
    val location: Location?,
    @Json(name = "tags")
    val tags: List<Tag>?
): PhotoAndCollection(), Parcelable