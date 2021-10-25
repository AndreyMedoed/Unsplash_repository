package com.example.unsplash.dataBase.dataBaseEssences

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.unsplash.data.contracts.CollectionContract
import com.example.unsplash.data.contracts.PhotoUrlContract
import com.example.unsplash.data.contracts.ProfileImageContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = ProfileImageContract.TABLE_NAME,
    indices = [
        Index(ProfileImageContract.Columns.SMALL),
        Index(ProfileImageContract.Columns.LARGE),
        Index(ProfileImageContract.Columns.MEDIUM)
    ])
data class ProfileImageDB (
    @Json(name = "id")
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ProfileImageContract.Columns.ID)
    var id: Long,
    @Json(name = "small")
    @ColumnInfo(name = ProfileImageContract.Columns.SMALL)
    var small: String,
    @Json(name = "medium")
    @ColumnInfo(name = ProfileImageContract.Columns.MEDIUM)
    var medium: String,
    @Json(name = "large")
    @ColumnInfo(name = ProfileImageContract.Columns.LARGE)
    var large: String
) : Parcelable {
    constructor() : this(0, "", "", "")
}