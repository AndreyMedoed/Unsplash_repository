package com.example.unsplash.screens.main.tabs.profile_fragment

import android.app.Application
import androidx.lifecycle.*
import com.example.unsplash.data.essences.user.Profile
import com.example.unsplash.data.essences.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository(application)
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


    fun clearAllDatabase() {
        viewModelScope.launch {
            repository.clearAllDatabase()
        }
    }
}