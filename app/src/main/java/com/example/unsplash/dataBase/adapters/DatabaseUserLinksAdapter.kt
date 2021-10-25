package com.example.unsplash.data.adapters

import com.example.roomdao.dataBase.Database
import com.example.unsplash.data.essences.user.UserLinks
import com.example.unsplash.dataBase.dataBaseEssences.UserLinksDB

class DatabaseUserLinksAdapter {
    private val userLinksDao = Database.instance.userLinksDao()

    suspend fun fromUserLinksToDBUserLinks(userLinks: UserLinks): Long? {
        val userLinksDB = UserLinksDB(
            id = 0,
            self = userLinks.self,
            html = userLinks.html,
            photos = userLinks.photos,
            likes = userLinks.likes,
            portfolio = userLinks.portfolio
        )
        userLinksDao.insertUserLinks(userLinksDB)
        val userLinksDBWithId = userLinksDao.getUserLinks(userLinksDB.self, userLinksDB.html)

        return userLinksDBWithId?.id
    }

    suspend fun fromDBUserLinksToUserLinks(userLinksDB_id: Long): UserLinks? {
        val userLinksDB = userLinksDao.getUserLinksById(userLinksDB_id)

        return userLinksDB?.let {
            UserLinks(
                userLinksDB.self,
                userLinksDB.html,
                userLinksDB.photos,
                userLinksDB.likes,
                userLinksDB.portfolio
            )
        }
    }

}