package com.example.unsplash.dataBase.adapters

import com.example.unsplash.dataBase.Database
import com.example.unsplash.data.essences.photo.Downloads
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.photo.Statistics
import com.example.unsplash.dataBase.dataBaseEssences.PhotoDB

/** Все адаптеры в этой папке нужны для преобразования экзампляров классов в их копию для удобного
 *  сохранения в базу данных и наоборот. ТОесть функция from"something"To"DBSomething"
 *  преобразовывает класс в его специальную копию для хранения в БД (Отличие в том, что некоторые свойства этих
 *  экземпляров - это экземпляры других классов. Так сохранять нельзя, поэтому вместо них в этих полях должны быть ID
 *  этих объектов). Функция написана так, что вызвав ее, мы преобразовываем не только сам екземпляр класса,
 *  но и экземпляры классов, которые хранятся в его свойствах и сразу записываем все в базу данных,
 *  и заменяя свойства с экземплярами на ID этих экземпляров. Функции вызываются по цепочке, пока все что нужно не перезапишется.*/
class DatabasePhotoAdapter {
    private val photoDao = Database.instance.photoDao()
    private val databasePhotoUrlAdapter = DatabasePhotoUrlAdapter()
    private val databaseUserAdapter = DatabaseUserAdapter()

    suspend fun fromPhotoToDBPhoto(photo: Photo, mark: String?): Long {
        val photoDB = PhotoDB(
            id = 0,
            unsplashId = photo.id,
            description = photo.description,
            photo_urls_id = photo.urls?.let {
                databasePhotoUrlAdapter.fromPhotoUrlToDBPhotoUrl(it)
            },
            likes = photo.likes,
            liked_by_user = photo.liked_by_user,
            user_id = photo.user?.let {
                databaseUserAdapter.fromUserToDBUser(it, null)
            },
            total_downloads = photo.statistics?.downloads?.total,
            mark = mark
        )
        photoDao.insertPhoto(photoDB)
        val id = photoDao.getPhotoByUnsplashIdAndMark(photo.id, mark).id
        return id
    }

    suspend fun fromDBPhotoToPhoto(photoDB_id: Long): Photo? {
        val photoDB = photoDao.getPhotoById(photoDB_id)
        return photoDB?.let {
            Photo(
                id = photoDB.unsplashId,
                description = photoDB.description,
                urls = photoDB.photo_urls_id?.let {
                    databasePhotoUrlAdapter.fromDBPhotoUrlToPhotoUrl(it)
                },
                likes = photoDB.likes,
                liked_by_user = photoDB.liked_by_user,
                user = photoDB.user_id?.let {
                    databaseUserAdapter.fromDBUserToUser(it)
                },
                statistics = Statistics(Downloads(photoDB.total_downloads))
            )
        }
    }

    suspend fun deletePhotosByMarker(marker: String) {
        photoDao.deleteByMarker(marker)
    }

}