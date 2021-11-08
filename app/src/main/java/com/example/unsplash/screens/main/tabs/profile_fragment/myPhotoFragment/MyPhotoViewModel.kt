package com.example.unsplash.screens.splash.fragmens.profile_fragment.myPhotoFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import kotlinx.coroutines.launch

class MyPhotoViewModel : ViewModel() {

    private val repository = MyPhotoRepository()

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
    fun postsOfPhotos(marker: String, pageSize: Int) = repository.postsOfPhotos(marker, pageSize)

}