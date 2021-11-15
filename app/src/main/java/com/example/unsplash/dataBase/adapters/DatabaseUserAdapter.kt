package com.example.unsplash.dataBase.adapters

import com.example.unsplash.dataBase.Database
import com.example.unsplash.data.essences.user.User
import com.example.unsplash.dataBase.dataBaseEssences.UserDB

/** Все адаптеры в этой папке нужны для преобразования экзампляров классов в их копию для удобного
 *  сохранения в базу данных и наоборот. ТОесть функция from"something"To"DBSomething"
 *  преобразовывает класс в его специальную копию для хранения в БД (Отличие в том, что некоторые свойства этих
 *  экземпляров - это экземпляры других классов. Так сохранять нельзя, поэтому вместо них в этих полях должны быть ID
 *  этих объектов). Функция написана так, что вызвав ее, мы преобразовываем не только сам екземпляр класса,
 *  но и экземпляры классов, которые хранятся в его свойствах и сразу записываем все в базу данных,
 *  и заменяя свойства с экземплярами на ID этих экземпляров. Функции вызываются по цепочке, пока все что нужно не перезапишется.*/
class DatabaseUserAdapter {
    private val userDao = Database.instance.userDao()
    private val databaseUserLinksAdapter = DatabaseUserLinksAdapter()
    private val databaseProfileImageAdapter = DatabaseProfileImageAdapter()

    suspend fun fromUserToDBUser(user: User, mark: String?): String {
        val userDB = UserDB(
            id = user.id,
            updated_at = user.updated_at,
            username = user.username,
            first_name = user.first_name,
            last_name = user.last_name,
            bio = user.bio,
            location = user.location,
            total_likes = user.total_likes,
            total_photos = user.total_photos,
            total_collections = user.total_collections,
            followed_by_user = user.followed_by_user,
            downloads = user.downloads,
            email = user.email,
            userLinks_id = user.userLinks?.let {
                databaseUserLinksAdapter.fromUserLinksToDBUserLinks(
                    it
                )
            },
            profile_image_id = user.profile_image?.let {
                databaseProfileImageAdapter.fromProfileImageToDBProfileImage(
                    it
                )
            },
            mark = mark
        )
        userDao.insertUser(userDB)
        return userDB.id
    }

    suspend fun fromDBUserToUser(userDB_id: String): User? {

        val userDB = userDao.getUserById(userDB_id)

        return userDB?.let {
            User(
                id = userDB.id,
                updated_at = userDB.updated_at,
                username = userDB.username,
                first_name = userDB.first_name,
                last_name = userDB.last_name,
                bio = userDB.bio,
                location = userDB.location,
                total_likes = userDB.total_likes,
                total_photos = userDB.total_photos,
                total_collections = userDB.total_collections,
                followed_by_user = userDB.followed_by_user,
                downloads = userDB.downloads,
                email = userDB.email,
                userLinks = userDB.userLinks_id?.let {
                    databaseUserLinksAdapter.fromDBUserLinksToUserLinks(
                        it
                    )
                },
                profile_image = userDB.profile_image_id?.let {
                    databaseProfileImageAdapter.fromDBProfileImageToProfileImage(
                        it
                    )
                }
            )
        }
    }
}