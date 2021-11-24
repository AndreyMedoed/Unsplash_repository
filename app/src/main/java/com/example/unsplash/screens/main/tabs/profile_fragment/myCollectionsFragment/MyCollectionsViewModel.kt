package com.example.unsplash.screens.main.tabs.profile_fragment.myCollectionsFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import kotlinx.coroutines.launch

class MyCollectionsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MyCollectionRepository(application)


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