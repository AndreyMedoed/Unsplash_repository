package com.example.unsplash.data.adapters

import com.example.roomdao.dataBase.Database
import com.example.unsplash.data.essences.collection.Collection
import com.example.unsplash.dataBase.adapters.DatabaseUserAdapter
import com.example.unsplash.dataBase.dataBaseEssences.CollectionDB

class DatabaseCollectionAdapter {
    private val collectionDao = Database.instance.collectionDao()
    private val databaseCollectionLinksAdapter = DatabaseCollectionLinksAdapter()
    private val databasePhotoAdapter = DatabasePhotoAdapter()
    private val databaseUserAdapter = DatabaseUserAdapter()

    suspend fun fromCollectionToDBCollection(collection: Collection, mark: String?): String {
        val collectionDB = CollectionDB(
            id = collection.id,
            title = collection.title,
            description = collection.description,
            total_photos = collection.total_photos,
            private = collection.private,
            links_id = collection.links?.let {
                databaseCollectionLinksAdapter.fromCollectionLinksToDBCollectionLinks(it)
            },
            user_id = collection.user?.let {
                databaseUserAdapter.fromUserToDBUser(it, null)
            },
            cover_photo_id = collection.cover_photo?.let {
                databasePhotoAdapter.fromPhotoToDBPhoto(it, null)
            },
            mark = mark
        )
        collectionDao.insertCollection(collectionDB)
        return collection.id
    }


    suspend fun fromDBCollectionToCollection(collectionDB_id: String): Collection? {
        val collectionDB = collectionDao.getCollectionById(collectionDB_id)

        return collectionDB?.let {
            Collection(
                id = collectionDB.id,
                title = collectionDB.title,
                description = collectionDB.description,
                total_photos = collectionDB.total_photos,
                private = collectionDB.private,
                links = collectionDB.links_id?.let {
                    databaseCollectionLinksAdapter.fromDBCollectionLinksToCollectionLinks(it)
                },
                user = collectionDB.user_id?.let {
                    databaseUserAdapter.fromDBUserToUser(it)
                },
                cover_photo = collectionDB.cover_photo_id?.let {
                    databasePhotoAdapter.fromDBPhotoToPhoto(it)
                }
            )
        }
    }

}