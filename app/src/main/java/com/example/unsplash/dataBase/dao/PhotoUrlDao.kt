package com.example.unsplash.dataBase.dao

import androidx.room.*
import com.example.unsplash.data.contracts.PhotoUrlContract
import com.example.unsplash.dataBase.dataBaseEssences.PhotoUrlDB

@Dao
interface PhotoUrlDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoUrl(photoUrl: PhotoUrlDB)


    @Query("SELECT * FROM ${PhotoUrlContract.TABLE_NAME} WHERE ${PhotoUrlContract.Columns.SMALL} =:small AND ${PhotoUrlContract.Columns.FULL} =:full")
    suspend fun getPhotoUrl(small: String, full: String): PhotoUrlDB?

    @Query("SELECT * FROM ${PhotoUrlContract.TABLE_NAME} WHERE ${PhotoUrlContract.Columns.ID} =:id")
    suspend fun getPhotoUrlById(id: Long): PhotoUrlDB?

    @Query("DELETE FROM ${PhotoUrlContract.TABLE_NAME}")
    fun clearAll()
}