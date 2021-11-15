package com.example.unsplash.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.unsplash.dataBase.Database
import com.example.unsplash.Network.UnsplashApi
import com.example.unsplash.dataBase.adapters.DatabaseCollectionAdapter
import com.example.unsplash.dataBase.dataBaseEssences.CollectionDB
import com.example.unsplash.dataBase.dataBaseEssences.remoteKeys.RemoteKey
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CollectionRemoteMediator(
    private val db: Database,
    private val unsplashApi: UnsplashApi,
    /** Маркер это особая метка, которая будет храниться в поле у каждого элемента в базе данных,
     *  В моем случае меткой служит запрос, по которому я получал тот или иной объект. Например фото или пользователя.
     *  По этому маркеру я потом буду искать его среди прочих объектов.*/
    private val marker: String,
    private val pageSize: Int
) : RemoteMediator<Int, CollectionDB>() {
    private val collectionDao = db.instance.collectionDao()
    private val remoteKeyDao = db.instance.remoteKeyDao()

    override suspend fun initialize(): InitializeAction {
        // Require that remote REFRESH is launched on initial load and succeeds before launching
        // remote PREPEND / APPEND.
        return InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CollectionDB>
    ): MediatorResult {
        try {
            // Get the closest item from PagingState that we want to load data around.
            val loadKey = when (loadType) {
                REFRESH -> null
                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                APPEND -> {
                    val remoteKey = db.instance.withTransaction {
                        remoteKeyDao.remoteKeyByMarker(marker)
                    }
                    /** Для отслеживания номера следующей страницы у меня есть специальный класс RemoteKey.
                     * он хранит какая страница следующая для каждого из запросов(маркеров).  Если запрос был последним,
                     * то ему присваевается значение end*/
                    if (remoteKey?.nextPageKey == "end") {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey?.nextPageKey
                }
            }

            val response = unsplashApi.getPagingCollectionContent(
                marker,
                loadKey, pageSize.toString()
            )

            var pageNumber: String = when {
                loadKey == null -> "2"
                else -> {
                    val page = loadKey.toInt() + 1
                    page.toString()
                }
            }

            val collectionList = response.body()
            Log.d("UnsplashLoggingResponse", "collectionList pageNumber  ${pageNumber}")

            Log.d(
                "UnsplashLoggingResponse",
                "collectionList response.headers()  ${response.headers()}"
            )
            Log.d("UnsplashLoggingResponse", "collectionList response.code() ${response.code()}")
            Log.d("UnsplashLoggingResponse", "collectionList response.raw()  ${response.raw()}")

            db.instance.withTransaction {
                if (loadType == REFRESH) {
                    Log.d("UnsplashLoggingRefresh", "collectionList is Refreshing")
                    collectionDao.deleteByMarker(marker)
                    remoteKeyDao.deleteByMarker(marker)
                }

                remoteKeyDao.insert(
                    RemoteKey(
                        marker,
                        if (collectionList?.isEmpty() ?: true) {
                            "end"
                        } else pageNumber
                    )
                )
                val adapter = DatabaseCollectionAdapter()
                collectionList?.map { adapter.fromCollectionToDBCollection(it, marker) }
            }

            return MediatorResult.Success(endOfPaginationReached = collectionList.isNullOrEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}
