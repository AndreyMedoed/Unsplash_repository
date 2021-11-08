package com.example.unsplash.screens.main.tabs.profile_fragment.myCollectionsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.example.unsplash.screens.splash.fragmens.profile_fragment.myCollectionsFragment.MyCollectionRepository
import kotlinx.coroutines.launch

class MyCollectionsViewModel : ViewModel() {

    private val repository = MyCollectionRepository()


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