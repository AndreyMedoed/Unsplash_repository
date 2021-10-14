package com.example.unsplash.data

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class Collection(
    @Json(name = "id")
    val id: String,
    @Json(name = "title")
    val title: String?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "total_photos")
    val total_photos: Int?,
    @Json(name = "private")
    val private: Boolean?,
    @Json(name = "links")
    val links: CollectionLinks?,
    @Json(name = "user")
    val user: User?,
    @Json(name = "cover_photo")
    val cover_photo: Photo?
): PhotoAndCollection(), Parcelable