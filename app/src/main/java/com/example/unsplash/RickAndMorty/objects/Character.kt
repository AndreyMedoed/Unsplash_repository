package com.example.rickandmorty.objects

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Character(
    val id: String,
    val name: String,
    val image: String
)