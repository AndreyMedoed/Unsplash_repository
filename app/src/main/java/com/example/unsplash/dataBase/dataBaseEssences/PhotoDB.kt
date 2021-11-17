package com.example.unsplash.dataBase.dataBaseEssences

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.unsplash.data.contracts.*
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.dataBase.contracts.PhotoContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = PhotoContract.TABLE_NAME,
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
    ]
)
data class PhotoDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = PhotoContract.Columns.ID)
    var id: Long,
    @ColumnInfo(name = PhotoContract.Columns.UNSPLASH_ID)
    var unsplashId: String,
    @ColumnInfo(name = PhotoContract.Columns.DESCRIPTION)
    var description: String?,
    @ColumnInfo(name = PhotoContract.Columns.PHOTO_URLS_ID)
    var photo_urls_id: Long?,
    @ColumnInfo(name = PhotoContract.Columns.LIKES)
    var likes: Int?,
    @ColumnInfo(name = PhotoContract.Columns.LIKED_BY_USER)
    var liked_by_user: Boolean,
    @ColumnInfo(name = PhotoContract.Columns.USER_ID)
    var user_id: String?,
    @ColumnInfo(name = PhotoContract.Columns.TOTAL_DOWNLOADS)
    var total_downloads: Int?,
    @ColumnInfo(name = PhotoContract.Columns.MARK)
    var mark: String?
) : PhotoAndCollection(), Parcelable {
    constructor() : this(0, "", "", 0, 0, false, "", 0, "")
}