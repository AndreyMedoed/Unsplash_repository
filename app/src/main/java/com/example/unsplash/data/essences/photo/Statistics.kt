package com.example.unsplash.data.essences.photo

import android.os.Parcelable
import com.example.unsplash.data.essences.photo.Downloads
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class Statistics(
    @Json(name = "downloads")
    val downloads: Downloads
) : Parcelable