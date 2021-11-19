package com.example.roomdao.dataBase.dao

import androidx.room.*
import com.example.unsplash.data.contracts.ProfileImageContract
import com.example.unsplash.dataBase.dataBaseEssences.ProfileImageDB

@Dao
interface ProfileImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfileImage(profileImage: ProfileImageDB)


    @Query("SELECT * FROM ${ProfileImageContract.TABLE_NAME} WHERE ${ProfileImageContract.Columns.SMALL} =:small AND ${ProfileImageContract.Columns.MEDIUM} =:medium AND ${ProfileImageContract.Columns.LARGE} =:large")
    suspend fun getProfileImage(small: String, medium: String, large: String): ProfileImageDB?

    @Query("SELECT * FROM ${ProfileImageContract.TABLE_NAME} WHERE ${ProfileImageContract.Columns.ID} =:id")
    suspend fun getProfileImageById(id: Long): ProfileImageDB?


    @Query("DELETE FROM ${ProfileImageContract.TABLE_NAME}")
    fun clearAll()




}