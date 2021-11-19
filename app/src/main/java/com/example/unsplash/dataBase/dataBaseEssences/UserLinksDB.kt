package com.example.unsplash.dataBase.dataBaseEssences

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.unsplash.data.contracts.UserLinksContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
@Entity(
    tableName = UserLinksContract.TABLE_NAME,
    indices = [
        Index(UserLinksContract.Columns.SELF),
        Index(UserLinksContract.Columns.HTML)
    ]
)
data class UserLinksDB(
    @Json(name = "id")
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = UserLinksContract.Columns.ID)
    var id: Long,
    @Json(name = "self")
    @ColumnInfo(name = UserLinksContract.Columns.SELF)
    var self: String?,
    @Json(name = "html")
    @ColumnInfo(name = UserLinksContract.Columns.HTML)
    var html: String?,
    @Json(name = "photos")
    @ColumnInfo(name = UserLinksContract.Columns.PHOTOS)
    var photos: String?,
    @Json(name = "likes")
    @ColumnInfo(name = UserLinksContract.Columns.LIKES)
    var likes: String?,
    @Json(name = "portfolio")
    @ColumnInfo(name = UserLinksContract.Columns.PORTFOLIO)
    var portfolio: String?
) : Parcelable {
    constructor() : this(0, "", "", "", "", "")
}