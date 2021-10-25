package com.example.unsplash.dataBase.dataBaseEssences

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.unsplash.data.contracts.*
import com.example.unsplash.data.essences.PhotoAndCollection
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = PhotoContract.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = PhotoUrlDB::class,
            parentColumns = [PhotoUrlContract.Columns.ID],
            childColumns = [PhotoContract.Columns.PHOTO_URLS_ID]
        ),
        ForeignKey(
            entity = UserDB::class,
            parentColumns = [UserContract.Columns.ID],
            childColumns = [PhotoContract.Columns.USER_ID]
        )
    ])
@JsonClass(generateAdapter = true)
data class PhotoDB(
    @Json(name = "id")
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = PhotoContract.Columns.ID)
    var id: String,
    @Json(name = "description")
    @ColumnInfo(name = PhotoContract.Columns.DESCRIPTION)
    var description: String?,
    @Json(name = "photo_urls_id")
    @ColumnInfo(name = PhotoContract.Columns.PHOTO_URLS_ID)
    var photo_urls_id: Long?,
    @Json(name = "likes")
    @ColumnInfo(name = PhotoContract.Columns.LIKES)
    var likes: Int?,
    @Json(name = "liked_by_user")
    @ColumnInfo(name = PhotoContract.Columns.LIKED_BY_USER)
    var liked_by_user: Boolean,
    @Json(name = "user_id")
    @ColumnInfo(name = PhotoContract.Columns.USER_ID)
    var user_id: String?,
    @Json(name = "total_downloads")
    @ColumnInfo(name = PhotoContract.Columns.TOTAL_DOWNLOADS)
    var total_downloads: Int?,
    @Json(name = "mark")
    @ColumnInfo(name = PhotoContract.Columns.MARK)
    var mark: String?
): PhotoAndCollection(), Parcelable {
    constructor() : this("", "", 0, 0, false, "", 0, "")
}