package com.example.roomdao.dataBase.dao

import androidx.room.*
import com.example.unsplash.data.contracts.CollectionLinksContract
import com.example.unsplash.dataBase.dataBaseEssences.CollectionLinksDB


@Dao
interface CollectionLinksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionLinks(collectionLinksDB: CollectionLinksDB)


    @Query("SELECT * FROM ${CollectionLinksContract.TABLE_NAME} WHERE ${CollectionLinksContract.Columns.SELF} =:self AND ${CollectionLinksContract.Columns.HTML} =:html")
    suspend fun getCollectionLinks(self: String?, html: String?): CollectionLinksDB?

    @Query("SELECT * FROM ${CollectionLinksContract.TABLE_NAME} WHERE ${CollectionLinksContract.Columns.ID} =:id")
    suspend fun getCollectionLinksById(id: Long): CollectionLinksDB?

    @Query("DELETE FROM ${CollectionLinksContract.TABLE_NAME}")
    fun clearAll()
}