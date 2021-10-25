package com.example.unsplash.fragmens.photo_detail_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.data.essences.photo.photo_detail.PhotoDetail
import com.example.unsplash.data.essences.user.Profile
import com.example.unsplash.data.essences.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoDetailViewModel : ViewModel() {

    private val repository = PhotoDetailRepository()
    private var photoDetail: PhotoDetail? = null

    private val photoDetailMutableLiveData = MutableLiveData<PhotoDetail?>()

    val photoDetailLiveData: LiveData<PhotoDetail?>
        get() = photoDetailMutableLiveData

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

    fun getPhotoDetail(photoId: String) {
        viewModelScope.launch {
            val photoDetail = repository.getPhotoDetail(photoId)
            photoDetailMutableLiveData.postValue(photoDetail)
        }
    }

}