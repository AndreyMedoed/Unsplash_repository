package com.example.rickandmorty.objects

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharacterResponse(
    val results: List<Character>
)
