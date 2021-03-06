package com.example.roomdao.dataBase.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.unsplash.dataBase.contracts.CollectionContract
import com.example.unsplash.dataBase.dataBaseEssences.CollectionDB


@Dao
interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionDB)

    @Query("SELECT * FROM ${CollectionContract.TABLE_NAME} WHERE ${CollectionContract.Columns.ID} =:id")
    suspend fun getCollectionById(id: String): CollectionDB?

    @Query("SELECT * FROM ${CollectionContract.TABLE_NAME} WHERE mark = :marker")
    fun postsByTopCollections(marker: String): PagingSource<Int, CollectionDB>

    @Query("DELETE FROM ${CollectionContract.TABLE_NAME} WHERE mark = :marker")
    suspend fun deleteByMarker(marker: String)

    @Query("DELETE FROM ${CollectionContract.TABLE_NAME}")
    fun clearAll()
}