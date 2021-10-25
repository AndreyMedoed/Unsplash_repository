package com.example.unsplash.data.contracts

object ProfileContract {

    const val TABLE_NAME = "profile"


    object Columns {
        const val ID = "id"
        const val UPDATED_AT = "updated_at"
        const val USERNAME = "username"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val BIO = "bio"
        const val LOCATION = "location"
        const val TOTAL_LIKES = "total_likes"
        const val TOTAL_PHOTOS = "total_photos"
        const val TOTAL_COLLECTIONS = "total_collections"
        const val FOLLOWED_BY_USER = "followed_by_user"
        const val DOWNLOADS = "downloads"
        const val EMAIL = "email"
        const val USER_LINKS = "links"
    }

}