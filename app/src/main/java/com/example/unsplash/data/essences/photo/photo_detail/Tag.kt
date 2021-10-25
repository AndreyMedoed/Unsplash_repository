package com.example.unsplash.data.essences.photo.photo_detail

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class Tag(
    @Json(name = "title")
    val title: String?
) : Parcelable