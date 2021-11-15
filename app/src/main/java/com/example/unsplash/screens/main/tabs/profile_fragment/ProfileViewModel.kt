package com.example.unsplash.screens.splash.fragmens.profile_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsplash.data.essences.user.Profile
import com.example.unsplash.data.essences.user.User
import com.example.unsplash.screens.main.tabs.profile_fragment.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()
    private var profile: Profile? = null

    private val profileMutableLiveData = MutableLiveData<Profile?>()
    private val userMutableLiveData = MutableLiveData<User?>()


    val profileLiveData: LiveData<Profile?>
        get() = profileMutableLiveData
    val userLiveData: LiveData<User?>
        get() = userMutableLiveData

    fun getMyProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            profile = repository.getMyProfile()
            profileMutableLiveData.postValue(profile)
            val user = profile?.let { repository.getUser(profile!!.username) }
            userMutableLiveData.postValue(user)
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

    fun clearAllDatabase() {
        viewModelScope.launch {
            repository.clearAllDatabase()
        }
    }
}