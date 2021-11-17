package com.example.unsplash.dataBase.contracts

object PhotoContract {

    const val TABLE_NAME = "photo"


    object Columns {
        const val ID = "id"
        const val UNSPLASH_ID = "unsplash_id"
        const val DESCRIPTION = "description"
        const val PHOTO_URLS_ID = "photo_urls_id"
        const val LIKES = "likes"
        const val LIKED_BY_USER = "liked_by_user"
        const val USER_ID = "user_id"
        const val TOTAL_DOWNLOADS = "total_downloads"
        const val MARK = "mark"
    }


}