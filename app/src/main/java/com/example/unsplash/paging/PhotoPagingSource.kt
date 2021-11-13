package com.example.unsplash.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplash.Network.UnsplashApi
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.data.essences.photo.Photo
import retrofit2.HttpException

/** Этот источник используется тольуо для "Поиска" фотографий по ключевым словам. */
class PhotoPagingSource(
    private val unsplashApi: UnsplashApi,
    private val query: String
) : PagingSource<Int, PhotoAndCollection>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoAndCollection> {
        if (query.isBlank()) {
            return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        }

        try {
            val pageNumber = params.key ?: INITIAL_PAGE_NUMBER
            val pageSize = params.loadSize
            val response =
                unsplashApi.searchPhotos(query, pageNumber.toString(), pageSize.toString())

            val photos = response.body()?.results
            val nextPageNumber = if (photos?.isEmpty()!!) null else pageNumber + 1
            val prevPageNumber = if (pageNumber > 1) pageNumber - 1 else null
            return LoadResult.Page(photos, prevPageNumber, nextPageNumber)

        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoAndCollection>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }


    companion object {

        const val INITIAL_PAGE_NUMBER = 1
    }
}