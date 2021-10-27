package com.example.rickandmorty.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.rickandmorty.di.Api
import com.example.unsplash.RickAndMorty.di.NetworkModule
import com.example.rickandmorty.panding_recycler_view.CharacterPagesSource

class MainFragmentViewModel(
    application: Application
) :
    AndroidViewModel(application) {
    private val api: Api = NetworkModule.api

    val photoPagingFlow = Pager(PagingConfig(pageSize = 10)) {
        CharacterPagesSource(api)
    }.flow.cachedIn(viewModelScope)
}