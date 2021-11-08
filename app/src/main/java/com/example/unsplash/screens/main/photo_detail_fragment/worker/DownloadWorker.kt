package com.example.unsplash.screens.splash.fragmens.photo_detail_fragment.worker

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.example.unsplash.Notifications.NotificationChannels
import com.example.unsplash.screens.splash.fragmens.photo_detail_fragment.PhotoDetailRepository
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
            showNewsMessageNotification(url)
            return Result.success()
        }
        return Result.failure()
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
            .notify(NEWS_NOTIFICATION_ID + Random.nextInt(), notification)
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
            .setPriority(NotificationCompat.PRIORITY_LOW)
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