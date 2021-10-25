package com.example.unsplash.paging


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplash.Network.UnsplashApi
import com.example.unsplash.data.essences.photo.Photo

//class PhotoPagingSource(
//    private val unsplashApi: UnsplashApi
//) : PagingSource<Int, Photo>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
//        try {
//            val pageNumber = params.key ?: INITIAL_PAGE_NUMBER
//            val pageSize = params.loadSize
//            val listPhoto = unsplashApi.getTopPhotos(pageNumber.toString())
//
//            if (listPhoto != null) {
//                Log.d("UnsplashLoggingPaging", "listPhoto != null")
//                val nextPageNumber = if (listPhoto?.isEmpty()!!) null else pageNumber + 1
//                val prevPageNumber = if (pageNumber > 1) pageNumber - 1 else null
//                return LoadResult.Page(listPhoto, prevPageNumber, nextPageNumber)
//            } else {
//                Log.d("UnsplashLoggingPaging", "listPhoto == null")
//                return LoadResult.Error(Throwable())
//            }
//        } catch (e: Exception) {
//            Log.d("UnsplashLoggingPaging", "Exception")
//            return LoadResult.Error(e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
////        val anchorPosition = state.anchorPosition ?: return null
////        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
////        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
//        return state.anchorPosition
//    }
//
//    companion object {
//
//        const val INITIAL_PAGE_NUMBER = 1
//    }
//}


class PhotoPagingSource(
    private val unsplashApi: UnsplashApi
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            val nextPageNumber = params.key ?: 1
            val listPhoto = unsplashApi.getTopPhotos(nextPageNumber.toString())
            LoadResult.Page(
                data = listPhoto!!,
                prevKey = if (nextPageNumber > 1) nextPageNumber - 1 else null,
                nextKey = if (nextPageNumber < 20) nextPageNumber + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition
    }
}