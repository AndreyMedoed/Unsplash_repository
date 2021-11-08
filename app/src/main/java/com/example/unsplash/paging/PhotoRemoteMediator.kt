package com.example.unsplash.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.roomdao.dataBase.Database
import com.example.unsplash.Network.UnsplashApi
import com.example.unsplash.data.adapters.DatabasePhotoAdapter
import com.example.unsplash.dataBase.dao.PhotoDao
import com.example.unsplash.dataBase.dataBaseEssences.PhotoDB
import com.example.unsplash.dataBase.dataBaseEssences.remoteKeys.RemoteKey
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator(
    private val db: Database,
    private val unsplashApi: UnsplashApi,
    private val marker: String,
    private val pageSize: Int
) : RemoteMediator<Int, PhotoDB>() {
    private val remoteKeyDao = db.instance.photoRemoteKeyDao()

    override suspend fun initialize(): InitializeAction {
        // Require that remote REFRESH is launched on initial load and succeeds before launching
        // remote PREPEND / APPEND.
        return InitializeAction.SKIP_INITIAL_REFRESH
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

//            val links = response.headers().get("link")
//            val links1 = links?.substring(0, links.length - 25)
//            var pageNumber: String = ""

//            links1?.length?.let {
//                var i = it
//                while (links1[i - 1] != '=') {
//                    pageNumber = links1[i - 1] + pageNumber
//                    i--
//                }
//            }
            var pageNumber: String = when {
                loadKey == null -> "2"
                else -> {
                    val page = loadKey.toInt() + 1
                    page.toString()
                }
            }

            val photoList = response.body()
//            Log.d("UnsplashLoggingResponse", "response.headers().get(link)  ${links}")
//            Log.d("UnsplashLoggingResponse", "links1  ${links1}")
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
                        if (photoList?.isEmpty() ?: true) {
                            "end"
                        } else pageNumber
                    )
                )
                photoList?.map { databasePhotoAdapter.fromPhotoToDBPhoto(it, marker) }
            }

            return MediatorResult.Success(endOfPaginationReached = photoList.isNullOrEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}
