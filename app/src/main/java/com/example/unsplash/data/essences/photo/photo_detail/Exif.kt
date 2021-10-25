package com.example.unsplash.data.essences.photo.photo_detail

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class Exif(
    @Json(name = "make")
    val make: String?,
    @Json(name = "model")
    val model: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "exposure_time")
    val exposure_time: String?,
    @Json(name = "aperture")
    val aperture: String?,
    @Json(name = "focal_length")
    val focal_length: String?,
    @Json(name = "iso")
    val iso: Int?,
) : Parcelable