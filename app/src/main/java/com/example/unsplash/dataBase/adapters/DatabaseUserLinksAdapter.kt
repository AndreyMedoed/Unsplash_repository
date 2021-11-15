package com.example.unsplash.dataBase.adapters

import com.example.unsplash.dataBase.Database
import com.example.unsplash.data.essences.user.UserLinks
import com.example.unsplash.dataBase.dataBaseEssences.UserLinksDB

/** Все адаптеры в этой папке нужны для преобразования экзампляров классов в их копию для удобного
 *  сохранения в базу данных и наоборот. ТОесть функция from"something"To"DBSomething"
 *  преобразовывает класс в его специальную копию для хранения в БД (Отличие в том, что некоторые свойства этих
 *  экземпляров - это экземпляры других классов. Так сохранять нельзя, поэтому вместо них в этих полях должны быть ID
 *  этих объектов). Функция написана так, что вызвав ее, мы преобразовываем не только сам екземпляр класса,
 *  но и экземпляры классов, которые хранятся в его свойствах и сразу записываем все в базу данных,
 *  и заменяя свойства с экземплярами на ID этих экземпляров. Функции вызываются по цепочке, пока все что нужно не перезапишется.*/
class DatabaseUserLinksAdapter {
    private val userLinksDao = Database.instance.userLinksDao()

    suspend fun fromUserLinksToDBUserLinks(userLinks: UserLinks): Long? {
        val userLinksDB = UserLinksDB(
            id = 0,
            self = userLinks.self,
            html = userLinks.html,
            photos = userLinks.photos,
            likes = userLinks.likes,
            portfolio = userLinks.portfolio
        )
        userLinksDao.insertUserLinks(userLinksDB)
        val userLinksDBWithId = userLinksDao.getUserLinks(userLinksDB.self, userLinksDB.html)

        return userLinksDBWithId?.id
    }

    suspend fun fromDBUserLinksToUserLinks(userLinksDB_id: Long): UserLinks? {
        val userLinksDB = userLinksDao.getUserLinksById(userLinksDB_id)

        return userLinksDB?.let {
            UserLinks(
                userLinksDB.self,
                userLinksDB.html,
                userLinksDB.photos,
                userLinksDB.likes,
                userLinksDB.portfolio
            )
        }
    }

}