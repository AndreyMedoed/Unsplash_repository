package com.example.unsplash.screens.main.photo_detail_fragment

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.unsplash.R
import com.example.unsplash.data.essences.photo.photo_detail.PhotoDetail
import com.example.unsplash.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class PhotoDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application
    private val repository = PhotoDetailRepository(application)
    private val toastSingleLiveEvent = SingleLiveEvent<String>()
    val toastLiveData: LiveData<String>
        get() = toastSingleLiveEvent

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

    fun savePhoto(name: String, url: String, uri: Uri) {
        viewModelScope.launch {
            try {
                repository.startDownload(name, url, uri)
            } catch (t: Throwable) {
                Log.d("UnsplashLoggingDownload", "${t.message} ")
                toastSingleLiveEvent.postValue(context.getString(R.string.photo_detail_save_fail))
            }
        }
    }

}