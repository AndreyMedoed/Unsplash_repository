package ua.cn.stu.navcomponent.tabs.screens.splash

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.unsplash.screens.splash.SplashFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * SplashViewModel checks whether user is signed-in or not.
 */
class SplashViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val sharedPreferences by lazy {
        application.getSharedPreferences(
            SHARED_PREFERENCE_NAME_PROFILE,
            Context.MODE_PRIVATE
        )
    }

    private val _launchMainScreenMutableLiveData = MutableLiveData<Boolean>()
    val launchMainScreenEvent: LiveData<Boolean>
        get() = _launchMainScreenMutableLiveData

    init {
        viewModelScope.launch {
            val isShown = sharedPreferences.getBoolean(ON_BOARDING_IS_SHOWN, false)
            _launchMainScreenMutableLiveData.postValue(isShown)
        }
    }

    companion object {
        private const val ON_BOARDING_IS_SHOWN = "ON_BOARDING_IS_SHOWN"
        private const val SHARED_PREFERENCE_NAME_PROFILE = "SHARED_PREFERENCE_NAME_PROFILE"
    }
}