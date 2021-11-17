package com.example.unsplash.dataBase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unsplash.data.essences.Token
import com.example.unsplash.dataBase.contracts.TokenContract


@Dao
interface TokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(token: Token)

    @Query("SELECT * FROM ${TokenContract.TABLE_NAME} WHERE ${TokenContract.Columns.MARKER} = :marker")
    suspend fun getToken(marker: String): Token?

    @Query("DELETE FROM ${TokenContract.TABLE_NAME} WHERE ${TokenContract.Columns.MARKER} = :marker")
    suspend fun deleteToken(marker: String)

    @Query("DELETE FROM ${TokenContract.TABLE_NAME}")
    fun clearAll()
}