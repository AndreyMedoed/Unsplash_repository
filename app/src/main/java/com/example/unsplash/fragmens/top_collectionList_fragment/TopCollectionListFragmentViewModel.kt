package com.example.unsplash.fragmens.top_collectionList_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsplash.data.essences.PhotoAndCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TopCollectionListFragmentViewModel: ViewModel() {
    private val repository = TopCollectionListRepository()

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


    fun getTopCollections() {
        viewModelScope.launch(Dispatchers.IO) {
            val collectionList = repository.getTopCollections()
            listMutableLiveData.postValue(collectionList)
        }
    }


}