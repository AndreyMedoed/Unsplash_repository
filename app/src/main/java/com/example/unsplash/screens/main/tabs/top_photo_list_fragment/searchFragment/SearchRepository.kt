package com.example.unsplash.screens.main.tabs.top_photo_list_fragment.searchFragment

import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.unsplash.paging.PhotoPagingSource
import com.example.unsplash.Network.NetworkConfig
import com.example.unsplash.data.essences.PhotoAndCollection
import com.example.unsplash.data.essences.photo.Downloads
import com.example.unsplash.data.essences.photo.Photo
import com.example.unsplash.data.essences.photo.Statistics
import com.example.unsplash.data.essences.photo.photo_detail.PhotoDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import okhttp3.Dispatcher
import java.util.regex.Pattern

class SearchRepository {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val unsplashUrlPattern =
        Pattern.compile("^https:\\/\\/unsplash\\.com\\/photos\\/[\\w\\-_]+\$")

    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }

    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }


    @ExperimentalPagingApi
    fun searchPhoto(
        flow: Flow<String>,
        toPhotoDetail: (photo: Photo) -> Unit
    ): Flow<Flow<PagingData<PhotoAndCollection>>> {
        return flow
            .mapLatest { query ->
                Log.d("FlowLogging", "query is $query")
//                if (unsplashUrlPattern.matcher(query).matches()) {
//                    Log.d("FlowLogging", "$query is matches")
//                    getPhoto(query, toPhotoDetail)
//                }

                Pager(PagingConfig(pageSize = 10)) {
                    PhotoPagingSource(NetworkConfig.unsplashApi, query)
                }.flow.cachedIn(scope)

            }

    }

     suspend fun getPhoto(url: String): Photo {
        val photoId =
            Uri.parse(url).lastPathSegment ?: return Photo("", "", null, null, false, null, null)
        Log.d("FlowLogging", "photoId !=null")
        val photoDetail = NetworkConfig.unsplashApi.getPhotoDetails(photoId) ?: return Photo(
            "",
            "",
            null,
            null,
            false,
            null,
            null
        )
        Log.d("FlowLogging", "photoDetail !=null")
        val photo = photoDetailToPhoto(photoDetail)
        return photo
    }

//    private suspend fun getPhoto(url: String, toPhotoDetail: (photo: Photo) -> Unit) {
//        val photoId = Uri.parse(url).lastPathSegment ?: return
//        Log.d("FlowLogging", "photoId !=null")
//        val photoDetail = NetworkConfig.unsplashApi.getPhotoDetails(photoId) ?: return
//        Log.d("FlowLogging", "photoDetail !=null")
//        val photo = photoDetailToPhoto(photoDetail)
//        toPhotoDetail(photo)
//    }

    /** Так как фрагмент детальной информации принимает в себя объект класса Photo, то проще
     * преобразовать PhotoDetail в Photo и передать его. Да, делается потом лишний запрос один, но
     * зато не придется усложнять код. Решил сделать такой выбор.*/
    private fun photoDetailToPhoto(photoDetail: PhotoDetail): Photo {
        return Photo(
            photoDetail.id,
            photoDetail.description,
            photoDetail.urls,
            photoDetail.likes,
            photoDetail.liked_by_user,
            photoDetail.user,
            Statistics(Downloads(photoDetail.downloads))
        )
    }
}