package com.example.unsplash.data

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class Statistics(
    @Json(name = "downloads")
    val downloads: Downloads
) : Parcelable