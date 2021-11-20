package com.example.unsplash.dataBase.dao

import androidx.room.*

import com.example.unsplash.data.contracts.ProfileContract
import com.example.unsplash.dataBase.dataBaseEssences.ProfileDB

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileDB)

    @Query("SELECT * FROM ${ProfileContract.TABLE_NAME} WHERE ${ProfileContract.Columns.ID} =:id")
    suspend fun getProfileById(id: String?): ProfileDB?

    @Query("DELETE FROM ${ProfileContract.TABLE_NAME}")
    fun clearAll()

}