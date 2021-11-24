package com.example.unsplash.screens.main.tabs.top_photo_list_fragment

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.screens.main.tabs.top_photo_list_fragment.TopPhotoListRepository
import kotlinx.coroutines.launch

class TopPhotoListFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TopPhotoListRepository(application)


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