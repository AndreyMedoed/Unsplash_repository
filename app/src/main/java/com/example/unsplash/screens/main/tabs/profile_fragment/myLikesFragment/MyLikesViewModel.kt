package com.example.unsplash.screens.splash.fragmens.profile_fragment.myLikesFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.example.unsplash.screens.main.tabs.profile_fragment.myLikesFragment.MyLikesRepository
import kotlinx.coroutines.launch

class MyLikesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MyLikesRepository(application)

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