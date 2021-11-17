package com.example.unsplash.dataBase.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unsplash.dataBase.contracts.PhotoContract
import com.example.unsplash.dataBase.dataBaseEssences.PhotoDB


@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoDB>?)

    @Query("SELECT * FROM ${PhotoContract.TABLE_NAME} WHERE ${PhotoContract.Columns.ID} =:id")
    suspend fun getPhotoById(id: Long): PhotoDB?

    @Query("SELECT * FROM ${PhotoContract.TABLE_NAME} WHERE ${PhotoContract.Columns.UNSPLASH_ID} =:unsplashId AND ${PhotoContract.Columns.MARK} =:mark")
    suspend fun getPhotoByUnsplashIdAndMark(unsplashId: String, mark: String?): PhotoDB

    @Query("SELECT * FROM ${PhotoContract.TABLE_NAME} WHERE mark = :marker")
    fun postsByTopPhotos(marker: String): PagingSource<Int, PhotoDB>

    @Query("DELETE FROM ${PhotoContract.TABLE_NAME} WHERE mark = :marker")
    suspend fun deleteByMarker(marker: String)

    @Query("DELETE FROM ${PhotoContract.TABLE_NAME}")
    fun clearAll()
}