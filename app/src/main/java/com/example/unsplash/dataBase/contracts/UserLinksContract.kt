package com.example.unsplash.data.contracts

import com.example.unsplash.data.essences.*
import com.squareup.moshi.Json

object UserLinksContract {

    const val TABLE_NAME = "user_links"

    object Columns {
        const val ID = "id"
        const val SELF = "self"
        const val HTML = "html"
        const val PHOTOS = "photos"
        const val LIKES = "likes"
        const val PORTFOLIO = "portfolio"
    }
}