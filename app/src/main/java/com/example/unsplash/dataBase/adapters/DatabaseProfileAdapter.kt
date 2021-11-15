package com.example.unsplash.dataBase.adapters

import com.example.unsplash.dataBase.Database
import com.example.unsplash.data.essences.user.Profile
import com.example.unsplash.dataBase.dataBaseEssences.ProfileDB

/** Все адаптеры в этой папке нужны для преобразования экзампляров классов в их копию для удобного
 *  сохранения в базу данных и наоборот. ТОесть функция from"something"To"DBSomething"
 *  преобразовывает класс в его специальную копию для хранения в БД (Отличие в том, что некоторые свойства этих
 *  экземпляров - это экземпляры других классов. Так сохранять нельзя, поэтому вместо них в этих полях должны быть ID
 *  этих объектов). Функция написана так, что вызвав ее, мы преобразовываем не только сам екземпляр класса,
 *  но и экземпляры классов, которые хранятся в его свойствах и сразу записываем все в базу данных,
 *  и заменяя свойства с экземплярами на ID этих экземпляров. Функции вызываются по цепочке, пока все что нужно не перезапишется.*/
class DatabaseProfileAdapter {
    private val profileDao = Database.instance.profileDao()
    private val databaseUserLinksAdapter = DatabaseUserLinksAdapter()

    suspend fun fromProfileToDBProfile(profile: Profile): String {
        val profileDB = ProfileDB(
            id = profile.id,
            updated_at = profile.updated_at,
            username = profile.username,
            first_name = profile.first_name,
            last_name = profile.last_name,
            bio = profile.bio,
            location = profile.location,
            total_likes = profile.total_likes,
            total_photos = profile.total_photos,
            total_collections = profile.total_collections,
            followed_by_user = profile.followed_by_user,
            downloads = profile.downloads,
            email = profile.email,
            userLinks_id = profile.userLinks?.let {
                databaseUserLinksAdapter.fromUserLinksToDBUserLinks(it)
            }
        )
        profileDao.insertProfile(profileDB)
        return profile.id

    }

    suspend fun fromDBProfileToProfile(profileDB_id: String): Profile? {
        val profileDB = profileDao.getProfileById(profileDB_id)
        return profileDB?.let {
            Profile(
                id = profileDB.id,
                updated_at = profileDB.updated_at,
                username = profileDB.username,
                first_name = profileDB.first_name,
                last_name = profileDB.last_name,
                bio = profileDB.bio,
                location = profileDB.location,
                total_likes = profileDB.total_likes,
                total_photos = profileDB.total_photos,
                total_collections = profileDB.total_collections,
                followed_by_user = profileDB.followed_by_user,
                downloads = profileDB.downloads,
                email = profileDB.email,
                userLinks = profileDB.userLinks_id?.let {
                    databaseUserLinksAdapter.fromDBUserLinksToUserLinks(it)
                }
            )
        }
    }

}