package com.example.unsplash.fragmens.profile_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    private val profileMutableLiveData = MutableLiveData<Profile>()

    val profileLiveData: LiveData<Profile>
        get() = profileMutableLiveData

    fun getMyProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = repository.getMyProfile()
            profileMutableLiveData.postValue(profile)
        }
    }
}