package com.example.rickandmorty.panding_recycler_view

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rickandmorty.di.Api
import com.example.rickandmorty.objects.Character
import com.example.unsplash.data.essences.photo.Photo
import javax.inject.Inject

class CharacterPagesSource(private val api: Api) :
    PagingSource<Int, Photo>() {

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            val pageNumber = params.key ?: 0
            val response = api.getTopPhotos(pageNumber.toString())
            val prevKey = if (pageNumber > 0) pageNumber - 1 else null
            val nextKey = if (response?.isNotEmpty() ?: false) pageNumber + 1 else null
            LoadResult.Page(
                data = response ?: emptyList(),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}