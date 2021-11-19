package com.example.unsplash.dataBase.dataBaseEssences

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.unsplash.data.contracts.*
import com.example.unsplash.dataBase.contracts.CollectionContract
import com.example.unsplash.dataBase.contracts.PhotoContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@Entity(
    tableName = CollectionContract.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = CollectionLinksDB::class,
            parentColumns = [CollectionLinksContract.Columns.ID],
            childColumns = [CollectionContract.Columns.LINKS]
        ),
        ForeignKey(
            entity = UserDB::class,
            parentColumns = [UserContract.Columns.ID],
            childColumns = [CollectionContract.Columns.USER]
        ),
        ForeignKey(
            entity = PhotoDB::class,
            parentColumns = [PhotoContract.Columns.ID],
            childColumns = [CollectionContract.Columns.COVER_PHOTO]
        )
    ]
)
@JsonClass(generateAdapter = true)
data class CollectionDB(
    @Json(name = "id")
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = CollectionContract.Columns.ID)
    var id: String,
    @Json(name = "title")
    @ColumnInfo(name = CollectionContract.Columns.TITLE)
    var title: String?,
    @Json(name = "description")
    @ColumnInfo(name = CollectionContract.Columns.DESCRIPTION)
    var description: String?,
    @Json(name = "total_photos")
    @ColumnInfo(name = CollectionContract.Columns.TOTAL_PHOTOS)
    var total_photos: Int?,
    @Json(name = "private")
    @ColumnInfo(name = CollectionContract.Columns.PRIVATE)
    var private: Boolean?,
    @Json(name = "links_id")
    @ColumnInfo(name = CollectionContract.Columns.LINKS)
    var links_id: Long?,
    @Json(name = "user_id")
    @ColumnInfo(name = CollectionContract.Columns.USER)
    var user_id: String?,
    @Json(name = "cover_photo_id")
    @ColumnInfo(name = CollectionContract.Columns.COVER_PHOTO)
    var cover_photo_id: Long?,
    @Json(name = "mark")
    @ColumnInfo(name = CollectionContract.Columns.MARK)
    var mark: String?
) {
    constructor() : this("", "", "", 0, false, 0, "", 0, "")
}