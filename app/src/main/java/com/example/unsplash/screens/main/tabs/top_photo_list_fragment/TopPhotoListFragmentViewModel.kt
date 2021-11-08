package com.example.unsplash.screens.splash.fragmens.top_photo_list_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.unsplash.data.essences.PhotoAndCollection
import kotlinx.coroutines.launch

class TopPhotoListFragmentViewModel : ViewModel() {
    private val repository = TopPhotoListRepository()


    private val listMutableLiveData = MutableLiveData<List<PhotoAndCollection>?>()

    val listLiveData: LiveData<List<PhotoAndCollection>?>
        get() = listMutableLiveData


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