package com.example.unsplash.fragmens.profile_fragment

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsplash.data.Photo
import com.example.unsplash.data.PhotoAndCollection
import com.example.unsplash.data.Profile
import com.example.unsplash.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()
    private var profile: Profile? = null

    private val profileMutableLiveData = MutableLiveData<Profile?>()
    private val userMutableLiveData = MutableLiveData<User?>()
    private val listMutableLiveData = MutableLiveData<List<PhotoAndCollection>?>()

    val profileLiveData: LiveData<Profile?>
        get() = profileMutableLiveData
    val userLiveData: LiveData<User?>
        get() = userMutableLiveData
    val listLiveData: LiveData<List<PhotoAndCollection>?>
        get() = listMutableLiveData

    fun getMyProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            profile = repository.getMyProfile()
            profileMutableLiveData.postValue(profile)
            val user = profile?.let { repository.getUser(profile!!.username) }
            userMutableLiveData.postValue(user)
        }
    }

    fun getMyPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            val photoList = profile?.let { repository.getMyPhotos(profile!!.username) }
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

    fun getMyLikesList() {
        viewModelScope.launch(Dispatchers.IO) {
            val photoList = profile?.let { repository.getMyLikeList(profile!!.username) }
            listMutableLiveData.postValue(photoList)
        }
    }

    fun getMyCollectionList() {
        viewModelScope.launch(Dispatchers.IO) {
            val collectionList = profile?.let { repository.getMyCollections(profile!!.username) }
            listMutableLiveData.postValue(collectionList)
        }
    }

    fun openCollectionPhotos(collectionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val photoList = repository.getCollectionPhotos(collectionId)
            listMutableLiveData.postValue(photoList)
        }
    }
}