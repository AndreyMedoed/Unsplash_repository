package com.example.unsplash.screens.main.collection_fragment

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.screens.main.collection_fragment.CollectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CollectionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CollectionRepository(application)

    private val listMutableLiveData = MutableLiveData<List<PhotoAndCollection>?>()

    val listLiveData: LiveData<List<PhotoAndCollection>?>
        get() = listMutableLiveData

    fun openCollectionPhotos(collectionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val photoList = repository.getCollectionPhotos(collectionId)
            listMutableLiveData.postValue(photoList)
        }
    }

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