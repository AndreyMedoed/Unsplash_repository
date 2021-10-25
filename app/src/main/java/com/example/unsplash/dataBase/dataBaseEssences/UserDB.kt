package com.example.unsplash.dataBase.dataBaseEssences

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.unsplash.data.contracts.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = UserContract.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = UserLinksDB::class,
            parentColumns = [UserLinksContract.Columns.ID],
            childColumns = [UserContract.Columns.USER_LINKS]
        ),
        ForeignKey(
            entity = ProfileImageDB::class,
            parentColumns = [ProfileImageContract.Columns.ID],
            childColumns = [UserContract.Columns.PROFILE_IMAGE]
        )
    ])
data class UserDB(
    @Json(name = "id")
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = UserContract.Columns.ID)
    var id: String,
    @Json(name = "updated_at")
    @ColumnInfo(name = UserContract.Columns.UPDATED_AT)
    var updated_at: String?,
    @Json(name = "username")
    @ColumnInfo(name = UserContract.Columns.USERNAME)
    var username: String,
    @Json(name = "first_name")
    @ColumnInfo(name = UserContract.Columns.FIRST_NAME)
    var first_name: String?,
    @Json(name = "last_name")
    @ColumnInfo(name = UserContract.Columns.LAST_NAME)
    var last_name: String?,
    @Json(name = "bio")
    @ColumnInfo(name = UserContract.Columns.BIO)
    var bio: String?,
    @Json(name = "location")
    @ColumnInfo(name = UserContract.Columns.LOCATION)
    var location: String?,
    @Json(name = "total_likes")
    @ColumnInfo(name = UserContract.Columns.TOTAL_LIKES)
    var total_likes: Int?,
    @Json(name = "total_photos")
    @ColumnInfo(name = UserContract.Columns.TOTAL_PHOTOS)
    var total_photos: Int?,
    @Json(name = "total_collections")
    @ColumnInfo(name = UserContract.Columns.TOTAL_COLLECTIONS)
    var total_collections: Int?,
    @Json(name = "followed_by_user")
    @ColumnInfo(name = UserContract.Columns.FOLLOWED_BY_USER)
    var followed_by_user: Boolean?,
    @Json(name = "downloads")
    @ColumnInfo(name = UserContract.Columns.DOWNLOADS)
    var downloads: Int?,
    @Json(name = "email")
    @ColumnInfo(name = UserContract.Columns.EMAIL)
    var email: String?,
    @Json(name = "userLinks_id")
    @ColumnInfo(name = UserContract.Columns.USER_LINKS)
    var userLinks_id: Long?,
    @Json(name = "profile_image_id")
    @ColumnInfo(name = UserContract.Columns.PROFILE_IMAGE)
    var profile_image_id: Long?,
    @Json(name = "mark")
    @ColumnInfo(name = UserContract.Columns.MARK)
    var mark: String?
) : Parcelable {
    constructor() : this("", "", "","", "", "","", 0, 0, 0, false, 0, "", 0, 0,"")
}