package com.example.unsplash.screens.main.photo_detail_fragment

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import com.bumptech.glide.Glide
import com.example.unsplash.Notifications.NotificationChannels
import com.example.unsplash.screens.main.photo_detail_fragment.worker.DownloadWorker
import com.example.unsplash.data.essences.photo.photo_detail.PhotoDetail
import com.example.unsplash.screens.main.MainActivity
import com.example.unsplash.utils.SingleLiveEvent
import com.skillbox.github.data.NetworkConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import java.util.concurrent.Flow
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class PhotoDetailRepository(private val context: Context) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    suspend fun getPhotoDetail(photoId: String): PhotoDetail? =
        NetworkConfig.unsplashApi.getPhotoDetails(photoId)

    suspend fun setLike(photoId: String) {
        NetworkConfig.unsplashApi.setLike(photoId)
    }

    suspend fun deleteLike(photoId: String) {
        NetworkConfig.unsplashApi.deleteLike(photoId)
    }

    suspend fun savePhoto(name: String, url: String, uri: Uri): String {
        return suspendCoroutine<String> { continuation ->
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) continuation.resume(
                FAILURE_EXTERNAL_STORAGE_MESSAGE
            )
            coroutineScope.launch {
                try {
                    val photoUri = savePhotoDetails(name, uri)
                    downloadPhoto(url, photoUri)
                    makePhotoVisible(photoUri)
                    continuation.resume(SUCCESS_MESSAGE)
                } catch (t: Throwable) {
                    Log.d("UnsplashLogging", "Ошибка при загрузке файла: ${t.stackTraceToString()}")
                    continuation.resume(FAILURE_INTERNET_MESSAGE)
                }
            }
        }

    }

    private fun savePhotoDetails(name: String, uri: Uri): Uri {

        val volume = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        } else {
            MediaStore.VOLUME_EXTERNAL
        }

        /** я удаляю лишнее начало, и остается только путь относительно sdCard */
        val chosenFolder = uri.lastPathSegment?.substring(8, uri.lastPathSegment!!.length)

        val photoCollectionUri = MediaStore.Images.Media.getContentUri(volume)
        val photoDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, chosenFolder)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        return context.contentResolver.insert(photoCollectionUri, photoDetails)!!
    }

    private suspend fun downloadPhoto(url: String, uri: Uri) {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            NetworkConfig.unsplashApi.downloadPhoto(url)
                .byteStream()
                .use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
        }
    }

    private fun makePhotoVisible(photoUri: Uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return

        val photoDetails = ContentValues().apply {
            put(MediaStore.Images.Media.IS_PENDING, 0)
        }
        context.contentResolver.update(
            photoUri, photoDetails, null, null
        )

    }

    /** Скачиваем используя ворк менеджер*/
    fun startDownload(name: String, url: String, uri: Uri) {
        val workData = workDataOf(
            DownloadWorker.WORK_DATA_URL_KEY to url,
            DownloadWorker.WORK_DATA_URI_KEY to uri.toString(),
            DownloadWorker.WORK_DATA_NAME_KEY to name
        )

        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workData)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .setConstraints(workConstraints)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(DOWNLOAD_WORK_ID, ExistingWorkPolicy.KEEP, workRequest)

    }



    companion object {
        private const val NEWS_NOTIFICATION_ID = 22221
        const val FAILURE_EXTERNAL_STORAGE_MESSAGE = "fail external storage"
        const val FAILURE_INTERNET_MESSAGE = "fail internet"
        const val SUCCESS_MESSAGE = "success"
        const val DOWNLOAD_WORK_ID = "work id"
    }
}