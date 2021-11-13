package com.example.unsplash.screens.main.photo_detail_fragment.worker

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
            val result = repository.savePhoto(name ?: "", url, uri)
            if (result == PhotoDetailRepository.FAILURE_INTERNET_MESSAGE) {
                Log.d("UnsplashLogging", "Ошибка сети")
                return Result.retry()
            }
            if (result == PhotoDetailRepository.FAILURE_EXTERNAL_STORAGE_MESSAGE) {
                Log.d("UnsplashLogging", "Внешнее хранилище недоступно")
                return Result.failure()
            }

            notifyUser(url, uri)

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
                showNewsMessageNotification(imageUrl)
            }
        }
    }

    private fun showNewsMessageNotification(imageUrl: String?) {

        val notificationBuilder = getNewsNotificationBuilder()
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

    private fun getNewsNotificationBuilder(): NotificationCompat.Builder {
//        val intent = Intent(this, MainActivity::class.java)
//            .putExtra("", "")
//
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        return NotificationCompat.Builder(
            context,
            NotificationChannels.NEWS_CHANNEL_ID
        )
            .setContentTitle("Загрузка")
            .setContentText("Фотография сохранена")
            .setSmallIcon(com.example.unsplash.R.drawable.ic_baseline_notifications_24)
            //для того чтоб отображалось на версии ниже О с нужным приоритетом
            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
    }



    companion object {
        const val NEWS_NOTIFICATION_ID = 22222
        const val WORK_DATA_URL_KEY = "url key"
        const val WORK_DATA_URI_KEY = "uri key"
        const val WORK_DATA_NAME_KEY = "name key"
    }
}