package com.example.unsplash.data.adapters

import com.example.roomdao.dataBase.Database
import com.example.unsplash.data.essences.collection.CollectionLinks
import com.example.unsplash.dataBase.dataBaseEssences.CollectionLinksDB

class DatabaseCollectionLinksAdapter {
    private val collectionLinksDao = Database.instance.collectionLinksDao()

    suspend fun fromCollectionLinksToDBCollectionLinks(collectionLinks: CollectionLinks): Long? {
        val collectionLinksDB = CollectionLinksDB(
            id = 0,
            self = collectionLinks.self,
            html = collectionLinks.html,
            photos = collectionLinks.photos,
            related = collectionLinks.related
        )
        collectionLinksDao.insertCollectionLinks(collectionLinksDB)
        val collectionLinksWithId =
            collectionLinksDao.getCollectionLinks(collectionLinks.self, collectionLinks.html)

        return collectionLinksWithId?.id
    }


    suspend fun fromDBCollectionLinksToCollectionLinks(collectionLinksDB_id: Long): CollectionLinks? {
        val collectionLinksDB = collectionLinksDao.getCollectionLinksById(collectionLinksDB_id)

        return collectionLinksDB?.let {
            CollectionLinks(
                self = collectionLinksDB.self,
                html = collectionLinksDB.html,
                photos = collectionLinksDB.photos,
                related = collectionLinksDB.related
            )
        }
    }
}