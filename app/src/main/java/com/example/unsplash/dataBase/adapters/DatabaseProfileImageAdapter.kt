package com.example.unsplash.dataBase.adapters

import com.example.roomdao.dataBase.Database
import com.example.unsplash.data.essences.user.ProfileImage
import com.example.unsplash.dataBase.dataBaseEssences.ProfileImageDB

/** Все адаптеры в этой папке нужны для преобразования экзампляров классов в их копию для удобного
 *  сохранения в базу данных и наоборот. ТОесть функция from"something"To"DBSomething"
 *  преобразовывает класс в его специальную копию для хранения в БД (Отличие в том, что некоторые свойства этих
 *  экземпляров - это экземпляры других классов. Так сохранять нельзя, поэтому вместо них в этих полях должны быть ID
 *  этих объектов). Функция написана так, что вызвав ее, мы преобразовываем не только сам екземпляр класса,
 *  но и экземпляры классов, которые хранятся в его свойствах и сразу записываем все в базу данных,
 *  и заменяя свойства с экземплярами на ID этих экземпляров. Функции вызываются по цепочке, пока все что нужно не перезапишется.*/
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