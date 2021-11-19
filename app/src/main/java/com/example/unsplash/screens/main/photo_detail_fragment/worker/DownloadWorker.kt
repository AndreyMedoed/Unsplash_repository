package com.example.unsplash.screens.main.photo_detail_fragment.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.example.unsplash.Notifications.NotificationChannels
import com.example.unsplash.R
import com.example.unsplash.screens.main.MainActivity
import com.example.unsplash.screens.main.photo_detail_fragment.PhotoDetailRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.random.Random

class DownloadWorker(
    val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    private val repository = PhotoDetailRepository(context)

    override suspend fun doWork(): Result {
        val url = inputData.getString(WORK_DATA_URL_KEY)
        val name = inputData.getString(WORK_DATA_NAME_KEY)
        val uriString = inputData.getString(WORK_DATA_URI_KEY)
        val uri = Uri.parse(uriString)

        url?.let {
            val resultUri = repository.savePhoto(name ?: "", url, uri)
            if (resultUri == PhotoDetailRepository.FAILURE_INTERNET_MESSAGE) {
                Log.d("UnsplashLogging", "Ошибка сети")
                return Result.retry()
            }
            if (resultUri == PhotoDetailRepository.FAILURE_EXTERNAL_STORAGE_MESSAGE) {
                Log.d("UnsplashLogging", "Внешнее хранилище недоступно")
                return Result.failure()
            }

            notifyUser(url, Uri.parse(resultUri))

            return Result.success()
        }
        return Result.failure()
    }

    /** В этом методе проверяем в каком состоянии наше приложение с помощью ProcessLifecycleOwner,
     * если RESUME, то показываем Snackbar, отправляя intent к localBroadcastReciever,
     * иначе - уведомление.*/
    fun notifyUser(imageUrl: String?, imageUri: Uri) {
        Log.d(
            "ProcessLog", "ProcessLifecycleOwner.get().lifecycle.currentState is ${
                ProcessLifecycleOwner.get()
                    .lifecycle.currentState
            }"
        )
        Log.d(
            "ProcessLog", "image uri is $imageUri"
        )
        Log.d(
            "ProcessLog", "image url is $imageUrl"
        )
        when (ProcessLifecycleOwner.get()
            .lifecycle.currentState) {
            Lifecycle.State.RESUMED -> {
                Log.d("ProcessLog", "Lifecycle.State.RESUMED")
                LocalBroadcastManager.getInstance(context).sendBroadcast(
                    Intent(MainActivity.INTENT_FILTER_SNACKBAR).putExtra(
                        MainActivity.INTENT_FILTER_SNACKBAR_URI,
                        imageUri.toString()
                    )
                )
            }
            else -> {
                Log.d("ProcessLog", "SHOW_NOTIFICATION")
                showNewsMessageNotification(imageUrl, imageUri)
            }
        }
    }

    private fun showNewsMessageNotification(imageUrl: String?, imageUri: Uri) {

        val notificationBuilder = getNewsNotificationBuilder(imageUri)
        val notification = if (imageUrl != null) {
            try {
                val bitmap = Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get()

                notificationBuilder.setLargeIcon(bitmap).build()
            } catch (t: Throwable) {
                notificationBuilder.build()
            }
        } else {
            notificationBuilder.build()
        }
        NotificationManagerCompat.from(context)
            .notify(DownloadWorker.NEWS_NOTIFICATION_ID + Random.nextInt(), notification)
    }

    private fun getNewsNotificationBuilder(imageUri: Uri): NotificationCompat.Builder {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(imageUri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        return NotificationCompat.Builder(
            context,
            NotificationChannels.NEWS_CHANNEL_ID
        )
            .setContentTitle(context.getString(R.string.download_worker_downloading))
            .setContentText(context.getString(R.string.download_worker_isSave))
            .setSmallIcon(com.example.unsplash.R.drawable.ic_baseline_notifications_24)
            //для того чтоб отображалось на версии ниже О с нужным приоритетом
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
    }



    companion object {
        const val NEWS_NOTIFICATION_ID = 22222
        const val WORK_DATA_URL_KEY = "url key"
        const val WORK_DATA_URI_KEY = "uri key"
        const val WORK_DATA_NAME_KEY = "name key"
    }
}