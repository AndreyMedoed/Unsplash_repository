package com.example.roomdao.dataBase.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unsplash.data.contracts.CollectionContract
import com.example.unsplash.data.contracts.PhotoContract
import com.example.unsplash.dataBase.dataBaseEssences.CollectionDB
import com.example.unsplash.dataBase.dataBaseEssences.PhotoDB


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
}