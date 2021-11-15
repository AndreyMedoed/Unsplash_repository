package com.example.unsplash.dataBase.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unsplash.data.contracts.PhotoContract
import com.example.unsplash.data.contracts.PhotoUrlContract
import com.example.unsplash.data.essences.Token
import com.example.unsplash.dataBase.contracts.RemoteKeyContract
import com.example.unsplash.dataBase.contracts.TokenContract
import com.example.unsplash.dataBase.dataBaseEssences.PhotoDB
import com.example.unsplash.dataBase.dataBaseEssences.PhotoUrlDB
import com.example.unsplash.dataBase.dataBaseEssences.remoteKeys.RemoteKey


@Dao
interface TokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(token: Token)

    @Query("SELECT * FROM ${TokenContract.TABLE_NAME} WHERE ${TokenContract.Columns.MARKER} = :marker")
    suspend fun getToken(marker: String): Token?

    @Query("DELETE FROM ${TokenContract.TABLE_NAME} WHERE ${TokenContract.Columns.MARKER} = :marker")
    suspend fun deleteToken(marker: String)

}