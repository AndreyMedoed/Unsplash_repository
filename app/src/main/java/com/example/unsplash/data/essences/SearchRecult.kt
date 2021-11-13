package com.example.unsplash.data.essences

import android.os.Parcelable
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.user.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
data class SearchRecult(
    @Json(name = "total")
    val total: String,
    @Json(name = "total_pages")
    val total_pages: String,
    @Json(name = "results")
    val results: List<Photo>
)