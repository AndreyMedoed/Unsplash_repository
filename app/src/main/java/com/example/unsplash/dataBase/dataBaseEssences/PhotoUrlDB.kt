package com.example.unsplash.dataBase.dataBaseEssences

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.unsplash.data.contracts.PhotoUrlContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = PhotoUrlContract.TABLE_NAME,
    indices = [
        Index(PhotoUrlContract.Columns.SMALL),
        Index(PhotoUrlContract.Columns.FULL)
    ])
@JsonClass(generateAdapter = true)
data class PhotoUrlDB(
    @Json(name = "id")
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = PhotoUrlContract.Columns.ID)
    var id: Long,
    @Json(name = "raw")
    @ColumnInfo(name = PhotoUrlContract.Columns.RAW)
    var raw: String,
    @Json(name = "full")
    @ColumnInfo(name = PhotoUrlContract.Columns.FULL)
    var full: String,
    @Json(name = "regular")
    @ColumnInfo(name = PhotoUrlContract.Columns.REGULAR)
    var regular: String,
    @Json(name = "small")
    @ColumnInfo(name = PhotoUrlContract.Columns.SMALL)
    var small: String,
    @Json(name = "thumb")
    @ColumnInfo(name = PhotoUrlContract.Columns.THUMB)
    var thumb: String
) : Parcelable {
    constructor() : this(0, "", "", "", "", "")
}