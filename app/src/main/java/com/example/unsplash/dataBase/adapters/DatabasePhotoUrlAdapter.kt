package com.example.unsplash.dataBase.adapters

import com.example.unsplash.dataBase.Database
import com.example.unsplash.data.essences.photo.PhotoUrl
import com.example.unsplash.dataBase.dataBaseEssences.PhotoUrlDB

/** Все адаптеры в этой папке нужны для преобразования экзампляров классов в их копию для удобного
 *  сохранения в базу данных и наоборот. ТОесть функция from"something"To"DBSomething"
 *  преобразовывает класс в его специальную копию для хранения в БД (Отличие в том, что некоторые свойства этих
 *  экземпляров - это экземпляры других классов. Так сохранять нельзя, поэтому вместо них в этих полях должны быть ID
 *  этих объектов). Функция написана так, что вызвав ее, мы преобразовываем не только сам екземпляр класса,
 *  но и экземпляры классов, которые хранятся в его свойствах и сразу записываем все в базу данных,
 *  и заменяя свойства с экземплярами на ID этих экземпляров. Функции вызываются по цепочке, пока все что нужно не перезапишется.*/
class DatabasePhotoUrlAdapter {
    private val photoUrlDao = Database.instance.photoUrlDao()

    suspend fun fromPhotoUrlToDBPhotoUrl(photoUrl: PhotoUrl): Long? {
        val photoUrlDB = PhotoUrlDB(
            id = 0,
            raw = photoUrl.raw,
            full = photoUrl.full,
            regular = photoUrl.regular,
            small = photoUrl.small,
            thumb = photoUrl.thumb
        )
        photoUrlDao.insertPhotoUrl(photoUrlDB)
        val photoUrlDBWithId = photoUrlDao.getPhotoUrl(photoUrl.small, photoUrl.full)

        return photoUrlDBWithId?.id
    }


    suspend fun fromDBPhotoUrlToPhotoUrl(photoUrlDB_id: Long): PhotoUrl? {
        val photoUrlDB = photoUrlDao.getPhotoUrlById(photoUrlDB_id)

        return photoUrlDB?.let {
            PhotoUrl(
                raw = photoUrlDB.raw,
                full = photoUrlDB.full,
                regular = photoUrlDB.regular,
                small = photoUrlDB.small,
                thumb = photoUrlDB.thumb
            )
        }
    }

}