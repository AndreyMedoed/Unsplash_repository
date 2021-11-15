package com.example.unsplash.dataBase.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unsplash.data.contracts.CollectionContract
import com.example.unsplash.data.contracts.PhotoContract
import com.example.unsplash.data.contracts.PhotoUrlContract
import com.example.unsplash.dataBase.dataBaseEssences.PhotoDB
import com.example.unsplash.dataBase.dataBaseEssences.PhotoUrlDB


@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoDB>?)

    @Query("SELECT * FROM ${PhotoContract.TABLE_NAME} WHERE ${PhotoContract.Columns.ID} =:id")
    suspend fun getPhotoById(id: String): PhotoDB?

    @Query("SELECT * FROM ${PhotoContract.TABLE_NAME} WHERE mark = :marker")
    fun postsByTopPhotos(marker: String): PagingSource<Int, PhotoDB>

    @Query("DELETE FROM ${PhotoContract.TABLE_NAME} WHERE mark = :marker")
    suspend fun deleteByMarker(marker: String)

    @Query("DELETE FROM ${PhotoContract.TABLE_NAME}")
    fun clearAll()
}