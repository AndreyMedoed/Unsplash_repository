package com.example.unsplash.screens.splash

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch


/**
 * SplashViewModel checks whether user is signed-in or not.
 */
class SplashViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = SplashRepository(application)

    private val sharedPreferences by lazy {
        application.getSharedPreferences(
            SHARED_PREFERENCE_NAME_PROFILE,
            Context.MODE_PRIVATE
        )
    }

    private val _launchMainScreenMutableLiveData = MutableLiveData<Pair<Boolean, Boolean>>()
    val launchMainScreenEvent: LiveData<Pair<Boolean, Boolean>>
        get() = _launchMainScreenMutableLiveData

    init {
        viewModelScope.launch {
            val isShownOnboarding = sharedPreferences.getBoolean(ON_BOARDING_IS_SHOWN, false)
            val isTokenNotOutdated = repository.checkToken()
            _launchMainScreenMutableLiveData.postValue(Pair(isShownOnboarding, isTokenNotOutdated))
        }
    }

    companion object {
        private const val ON_BOARDING_IS_SHOWN = "ON_BOARDING_IS_SHOWN"
        private const val SHARED_PREFERENCE_NAME_PROFILE = "SHARED_PREFERENCE_NAME_PROFILE"
    }
}