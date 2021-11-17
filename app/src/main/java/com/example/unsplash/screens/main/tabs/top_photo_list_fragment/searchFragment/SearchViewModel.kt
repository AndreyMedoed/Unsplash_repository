package com.example.unsplash.screens.main.tabs.top_photo_list_fragment.searchFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.paging.PhotoPagingSource
import com.example.unsplash.Network.NetworkConfig
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val repository = SearchRepository()
    var flowJob: Job? = null

    //LiveData для показа и сокрытия progressBar
    private val progBarEvent = MutableLiveData<Boolean>(false)
    private val isMatchesMutableLiveData = SingleLiveEvent<Photo>()

    val isMatchesLiveData: LiveData<Photo>
        get() = isMatchesMutableLiveData


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

    private fun toPhotoDetail(photo: Photo) {
        Log.d("FlowLogging", "toPhotoDetail in viewModel")
        isMatchesMutableLiveData.postValue(photo)
    }

    /** Тут получилась такая логика, что я отправляю во фрагмент поток с потоками, каждое изменеие
     * запроса в EditText по сути создает новый поток ответов на этот запрос, соответственно после
     * каждого изменения запроса я подменяю поток во фрагменте и начинаю слушать новый.*/
    @ExperimentalPagingApi
    fun searchPhoto(flow: Flow<String>): Flow<Flow<PagingData<PhotoAndCollection>>> {
        val flow1 = flow
            .debounce(2000)
            .distinctUntilChanged()
            .onEach { showProgress(true) }

        val flow2 = repository.searchPhoto(flow1) { photo -> toPhotoDetail(photo) }

        return flow2
            .catch {
                /* Если возникнет ошибка, то по параметру запроса, сохраненному в query,
                * выводим диалог, где предлагаем вывести поиск по базе данных */
                Log.d("FlowLogging", it.stackTraceToString())
                flowJob?.cancel()
                showProgress(false)
                Log.d("FlowLogging", "ОШИБКА, ПОДГРУЖАЕМ ИЗ БД")
            }
            .onEach {
                showProgress(false)
            }.onEach {
                Log.d("FlowLogging", "$it")
            }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            progBarEvent.postValue(true)
        } else {
            progBarEvent.postValue(false)
        }
    }

    fun getPhoto(url: String) {
        viewModelScope.launch {
            val photo = repository.getPhoto(url)
            isMatchesMutableLiveData.postValue(photo)
        }
    }
}