package com.example.unsplash.dataBase.dataBaseEssences

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.unsplash.data.contracts.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(
    tableName = ProfileContract.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = UserLinksDB::class,
            parentColumns = [UserLinksContract.Columns.ID],
            childColumns = [ProfileContract.Columns.USER_LINKS]
        )
    ]
)
data class ProfileDB(
    @Json(name = "id")
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = ProfileContract.Columns.ID)
    var id: String,
    @Json(name = "updated_at")
    @ColumnInfo(name = ProfileContract.Columns.UPDATED_AT)
    var updated_at: String?,
    @Json(name = "username")
    @ColumnInfo(name = ProfileContract.Columns.USERNAME)
    var username: String,
    @Json(name = "first_name")
    @ColumnInfo(name = ProfileContract.Columns.FIRST_NAME)
    var first_name: String?,
    @Json(name = "last_name")
    @ColumnInfo(name = ProfileContract.Columns.LAST_NAME)
    var last_name: String?,
    @Json(name = "bio")
    @ColumnInfo(name = ProfileContract.Columns.BIO)
    var bio: String?,
    @Json(name = "location")
    @ColumnInfo(name = ProfileContract.Columns.LOCATION)
    var location: String?,
    @Json(name = "total_likes")
    @ColumnInfo(name = ProfileContract.Columns.TOTAL_LIKES)
    var total_likes: Int?,
    @Json(name = "total_photos")
    @ColumnInfo(name = ProfileContract.Columns.TOTAL_PHOTOS)
    var total_photos: Int?,
    @Json(name = "total_collections")
    @ColumnInfo(name = ProfileContract.Columns.TOTAL_COLLECTIONS)
    var total_collections: Int?,
    @Json(name = "followed_by_user")
    @ColumnInfo(name = ProfileContract.Columns.FOLLOWED_BY_USER)
    var followed_by_user: Boolean?,
    @Json(name = "downloads")
    @ColumnInfo(name = ProfileContract.Columns.DOWNLOADS)
    var downloads: Int?,
    @Json(name = "email")
    @ColumnInfo(name = ProfileContract.Columns.EMAIL)
    var email: String?,
    @Json(name = "userLinks_id")
    @ColumnInfo(name = ProfileContract.Columns.USER_LINKS)
    var userLinks_id: Long?
) {
    constructor() : this("", "", "","", "", "","", 0, 0, 0, false, 0, "", 0)
}