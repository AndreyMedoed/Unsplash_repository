package com.example.unsplash.screens.splash.fragmens.onboarding_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {

    private val changeSceneMutableLiveData = MutableLiveData<Int>()
    val changeSceneLiveData: LiveData<Int>
        get() = changeSceneMutableLiveData

    private var sceneState = 1

    fun nextScene() {
        when (sceneState) {
            1 -> viewModelScope.launch {
                delay(1700)
                sceneState++
                changeSceneMutableLiveData.postValue(sceneState)
            }
            2 -> {
                sceneState++
                changeSceneMutableLiveData.postValue(sceneState)
            }
            3 -> {
                sceneState++
                changeSceneMutableLiveData.postValue(sceneState)
            }
            4 -> {
                sceneState++
                changeSceneMutableLiveData.postValue(sceneState)
            }
        }
    }
}