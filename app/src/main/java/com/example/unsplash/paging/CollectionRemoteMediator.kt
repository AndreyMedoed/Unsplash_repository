package com.example.unsplash.paging

import android.content.Context
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
import com.example.unsplash.utils.EmptyListException
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
    private val pageSize: Int,
    private val context: Context
) : RemoteMediator<Int, CollectionDB>() {
    private val collectionDao = db.instance.collectionDao()
    private val remoteKeyDao = db.instance.remoteKeyDao()

    private val sharedPreferences by lazy {
        context.getSharedPreferences(
            SHARED_PREFERENCE_NAME_PROFILE,
            Context.MODE_PRIVATE
        )
    }

    override suspend fun initialize(): InitializeAction {
        // Require that remote REFRESH is launched on initial load and succeeds before launching
        // remote PREPEND / APPEND.
        val lastRefreshTime = sharedPreferences.getLong(LAST_REFRESH_TIME, 0)
        return if (lastRefreshTime == 0L || System.currentTimeMillis() - lastRefreshTime > STANDARD_TIME_TO_REFRESH) {
            sharedPreferences.edit().putLong(LAST_REFRESH_TIME, System.currentTimeMillis()).apply()
            Log.d("UnsplashRefreshTime", "LAUNCH_INITIAL_REFRESH lastRefreshTime is $lastRefreshTime")
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else{
            Log.d("UnsplashRefreshTime", "SKIP_INITIAL_REFRESH")
            InitializeAction.SKIP_INITIAL_REFRESH
        }
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
            /** Если на 1 странице пусто, значит выдаем кастомную ошибку - чтоб обработать пустой список*/
            if (loadKey == null && collectionList.isNullOrEmpty()) throw EmptyListException()

            return MediatorResult.Success(endOfPaginationReached = collectionList.isNullOrEmpty())
        } catch (e: EmptyListException) {
            return MediatorResult.Error(e)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
    companion object {
        private const val SHARED_PREFERENCE_NAME_PROFILE = "SHARED_PREFERENCE_NAME_PROFILE"
        private const val LAST_REFRESH_TIME = "LAST_REFRESH_TIME"
        private const val STANDARD_TIME_TO_REFRESH = 3600000L
    }
}
