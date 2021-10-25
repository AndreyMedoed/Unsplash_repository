package com.example.unsplash.dataBase.dao

import androidx.room.*

import com.example.unsplash.data.contracts.CollectionContract
import com.example.unsplash.data.contracts.ProfileContract
import com.example.unsplash.dataBase.dataBaseEssences.CollectionDB
import com.example.unsplash.dataBase.dataBaseEssences.ProfileDB
import retrofit2.http.DELETE

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileDB)

    @Query("SELECT * FROM ${ProfileContract.TABLE_NAME} WHERE ${ProfileContract.Columns.ID} =:id")
    suspend fun getProfileById(id: String): ProfileDB?


}