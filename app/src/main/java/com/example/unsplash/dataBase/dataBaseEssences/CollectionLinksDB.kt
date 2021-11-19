package com.example.unsplash.dataBase.dataBaseEssences

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.unsplash.data.contracts.CollectionLinksContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = CollectionLinksContract.TABLE_NAME,
    indices = [
        Index(CollectionLinksContract.Columns.SELF),
        Index(CollectionLinksContract.Columns.HTML)
    ]
)
@JsonClass(generateAdapter = true)
data class CollectionLinksDB(
    @Json(name = "id")
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = CollectionLinksContract.Columns.ID)
    var id: Long,
    @Json(name = "self")
    @ColumnInfo(name = CollectionLinksContract.Columns.SELF)
    var self: String?,
    @Json(name = "html")
    @ColumnInfo(name = CollectionLinksContract.Columns.HTML)
    var html: String?,
    @Json(name = "photos")
    @ColumnInfo(name = CollectionLinksContract.Columns.PHOTOS)
    var photos: String?,
    @Json(name = "related")
    @ColumnInfo(name = CollectionLinksContract.Columns.RELATED)
    var related: String?
) : Parcelable {
    constructor() : this(0, "", "", "", "")
}