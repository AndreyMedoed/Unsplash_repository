package com.example.unsplash.Network

import com.skillbox.github.data.Owner
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Repository(
    val id: Int,
    val name: String,
    val full_name: String,
    val description: String?,
    val owner: Owner
)