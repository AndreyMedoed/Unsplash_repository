package com.example.unsplash.data.adapters

import com.example.roomdao.dataBase.Database
import com.example.unsplash.data.essences.user.ProfileImage
import com.example.unsplash.dataBase.dataBaseEssences.ProfileImageDB

class DatabaseProfileImageAdapter {
    private val profileImageDao = Database.instance.profileImageDao()

    suspend fun fromProfileImageToDBProfileImage(profileImage: ProfileImage): Long? {
        val profileImageDB = ProfileImageDB(
            id = 0,
            small = profileImage.small,
            medium = profileImage.medium,
            large = profileImage.large
        )
        profileImageDao.insertProfileImage(profileImageDB)
        val profileImageDBWithId = profileImageDao.getProfileImage(
            profileImageDB.small,
            profileImageDB.medium,
            profileImageDB.large
        )

        return profileImageDBWithId?.id
    }


    suspend fun fromDBProfileImageToProfileImage(profileImageDB_id: Long): ProfileImage? {
        val profileImageDB = profileImageDao.getProfileImageById(profileImageDB_id)

        return profileImageDB?.let {
            ProfileImage(
                small = profileImageDB.small,
                medium = profileImageDB.medium,
                large = profileImageDB.large
            )
        }
    }

}