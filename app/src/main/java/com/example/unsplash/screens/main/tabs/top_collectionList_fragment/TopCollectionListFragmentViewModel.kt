package com.example.unsplash.screens.main.tabs.top_collectionList_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.example.unsplash.screens.splash.fragmens.top_collectionList_fragment.TopCollectionListRepository
import kotlinx.coroutines.launch

class TopCollectionListFragmentViewModel: ViewModel() {
    private val repository = TopCollectionListRepository()

    fun setLike(photoId: String) {
        viewModelScope.launch {
            repository.setLike(photoId)
        }
    }

    fun deleteLike(photoId: String) {
        viewModelScope.launch {
            repository.deleteLike(photoId)
        }
    }

    @ExperimentalPagingApi
    fun postsOfCollections(marker: String, pageSize: Int) = repository.postsOfCollections(marker, pageSize)

}