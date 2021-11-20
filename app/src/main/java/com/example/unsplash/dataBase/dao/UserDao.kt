package com.example.roomdao.dataBase.dao

import androidx.room.*
import com.example.unsplash.data.contracts.UserContract
import com.example.unsplash.dataBase.dataBaseEssences.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserDB)


    @Query("SELECT * FROM ${UserContract.TABLE_NAME} WHERE ${UserContract.Columns.ID} =:id")
    suspend fun getUserById(id: String?): UserDB?

    @Query("DELETE FROM ${UserContract.TABLE_NAME}")
    fun clearAll()
}