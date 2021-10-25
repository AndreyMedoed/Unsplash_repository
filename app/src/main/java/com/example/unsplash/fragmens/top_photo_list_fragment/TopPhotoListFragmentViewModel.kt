package com.example.unsplash.fragmens.top_photo_list_fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.paging.PhotoPagingSource
import com.skillbox.github.data.NetworkConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
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

    fun getTopPhotosPaging(): kotlinx.coroutines.flow.Flow<PagingData<Photo>> {
        Log.d("UnsplashLoggingPaging", "getTopPhotosPaging is starting")
        val flow =  Pager(
            config = PagingConfig(pageSize = 10, maxSize = 50),
            pagingSourceFactory = { PhotoPagingSource(NetworkConfig.unsplashApi)}).flow.cachedIn(viewModelScope)
        flow.onEach {
            Log.d("UnsplashLoggingPaging", "PagingData is ${it}")
        }
        return flow

//        viewModelScope.launch(Dispatchers.IO) {
//            val photosList = repository.getTopPhotos()
//            listMutableLiveData.postValue(photosList)
//        }
    }


    fun getTopPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            val photosList = repository.getTopPhotos()
            listMutableLiveData.postValue(photosList)
        }
    }







}