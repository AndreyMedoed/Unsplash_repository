package com.example.unsplash.dataBase.adapters

import com.example.unsplash.dataBase.Database
import com.example.unsplash.data.essences.collection.CollectionLinks
import com.example.unsplash.dataBase.dataBaseEssences.CollectionLinksDB

/** Все адаптеры в этой папке нужны для преобразования экзампляров классов в их копию для удобного
 *  сохранения в базу данных и наоборот. ТОесть функция from"something"To"DBSomething"
 *  преобразовывает класс в его специальную копию для хранения в БД (Отличие в том, что некоторые свойства этих
 *  экземпляров - это экземпляры других классов. Так сохранять нельзя, поэтому вместо них в этих полях должны быть ID
 *  этих объектов). Функция написана так, что вызвав ее, мы преобразовываем не только сам екземпляр класса,
 *  но и экземпляры классов, которые хранятся в его свойствах и сразу записываем все в базу данных,
 *  и заменяя свойства с экземплярами на ID этих экземпляров. Функции вызываются по цепочке, пока все что нужно не перезапишется.*/
class DatabaseCollectionLinksAdapter {
    private val collectionLinksDao = Database.instance.collectionLinksDao()

    suspend fun fromCollectionLinksToDBCollectionLinks(collectionLinks: CollectionLinks): Long? {
        val collectionLinksDB = CollectionLinksDB(
            id = 0,
            self = collectionLinks.self,
            html = collectionLinks.html,
            photos = collectionLinks.photos,
            related = collectionLinks.related
        )
        collectionLinksDao.insertCollectionLinks(collectionLinksDB)
        val collectionLinksWithId =
            collectionLinksDao.getCollectionLinks(collectionLinks.self, collectionLinks.html)

        return collectionLinksWithId?.id
    }


    suspend fun fromDBCollectionLinksToCollectionLinks(collectionLinksDB_id: Long): CollectionLinks? {
        val collectionLinksDB = collectionLinksDao.getCollectionLinksById(collectionLinksDB_id)

        return collectionLinksDB?.let {
            CollectionLinks(
                self = collectionLinksDB.self,
                html = collectionLinksDB.html,
                photos = collectionLinksDB.photos,
                related = collectionLinksDB.related
            )
        }
    }
}