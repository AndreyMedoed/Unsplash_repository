package com.example.unsplash.data.essences

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.user.User
import com.example.unsplash.dataBase.contracts.TokenContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Entity(tableName = TokenContract.TABLE_NAME)
data class Token(
    @PrimaryKey
    @ColumnInfo(name = TokenContract.Columns.MARKER)
    val marker: String,
    @ColumnInfo(name = TokenContract.Columns.TOKEN)
    val token: String
)