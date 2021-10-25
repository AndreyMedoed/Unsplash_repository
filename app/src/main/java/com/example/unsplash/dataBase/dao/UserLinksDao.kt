package com.example.roomdao.dataBase.dao

import androidx.room.*
import com.example.unsplash.data.contracts.ProfileImageContract
import com.example.unsplash.data.contracts.UserLinksContract
import com.example.unsplash.dataBase.dataBaseEssences.ProfileImageDB
import com.example.unsplash.dataBase.dataBaseEssences.UserLinksDB
import retrofit2.http.DELETE

@Dao
interface UserLinksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserLinks(userLinksDB: UserLinksDB)


    @Query("SELECT * FROM ${UserLinksContract.TABLE_NAME} WHERE ${UserLinksContract.Columns.SELF} =:self AND ${UserLinksContract.Columns.HTML} =:html")
    suspend fun getUserLinks(self: String?, html: String?): UserLinksDB?

    @Query("SELECT * FROM ${UserLinksContract.TABLE_NAME} WHERE ${UserLinksContract.Columns.ID} =:id")
    suspend fun getUserLinksById(id: Long): UserLinksDB?

}