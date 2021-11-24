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
import com.example.unsplash.dataBase.adapters.DatabasePhotoAdapter
import com.example.unsplash.dataBase.dataBaseEssences.PhotoDB
import com.example.unsplash.dataBase.dataBaseEssences.remoteKeys.RemoteKey
import com.example.unsplash.screens.main.tabs.profile_fragment.myPhotoFragment.MyPhotoFragment
import com.example.unsplash.utils.EmptyListException
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator(
    private val db: Database,
    private val unsplashApi: UnsplashApi,
    private val marker: String,
    private val pageSize: Int,
    private val context: Context
) : RemoteMediator<Int, PhotoDB>() {
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
        state: PagingState<Int, PhotoDB>
    ): MediatorResult {
        Log.d("UnsplashLoggingMarker", "  ${marker}")
        try {

            val databasePhotoAdapter = DatabasePhotoAdapter()

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

            val response = unsplashApi.getPagingPhotoContent(
                marker,
                loadKey,
                pageSize.toString()
            )

            val pageNumber: String = when {
                loadKey == null -> "2"
                else -> {
                    val page = loadKey.toInt() + 1
                    page.toString()
                }
            }

            val photoList = response.body()
            Log.d("UnsplashLoggingResponse", "pageNumber  ${pageNumber}")
            Log.d("UnsplashLoggingResponse", "response.headers()  ${response.headers()}")

            db.instance.withTransaction {
                Log.d("UnsplashLoggingResponse", "loadType == REFRESH")
                if (loadType == REFRESH) {
                    databasePhotoAdapter.deletePhotosByMarker(marker)
                    remoteKeyDao.deleteByMarker(marker)
                }
                remoteKeyDao.insert(
                    RemoteKey(
                        marker,
                        if (photoList.isNullOrEmpty()) {
                            "end"
                        } else pageNumber
                    )
                )
                photoList?.map { databasePhotoAdapter.fromPhotoToDBPhoto(it, marker) }
            }
            /** Если на 1 странице пусто, значит выдаем кастомную ошибку - чтоб обработать пустой список*/
            if (loadKey == null && photoList.isNullOrEmpty()) throw EmptyListException()

            return MediatorResult.Success(endOfPaginationReached = photoList.isNullOrEmpty())
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