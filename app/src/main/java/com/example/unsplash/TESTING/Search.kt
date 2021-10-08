package com.example.flow.JsonAndMovie

import com.example.unsplash.TESTING.Movie
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Search(
    @Json(name = "Search")
    val movieList: List<Movie>
)