package com.example.unsplash.Network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.unsplash.screens.main.MainActivity

/** Этот LocalBroadcastReceiver нужен для того, чтобы слушать команду, когда надо показать SnackBar
 *, потому что в задании было сказано,что иногда надо показывать SnackBar, а иногда уведомление.
 * В зависимости от того открыто приложение или нет. В воркере я это проверяю, и если приложение открыто,
 * то отправляю интент для этого ресивера.*/

class SnackBarLocalBroadcastReceiver(private val snackbarCallback: (uri: Uri) -> Unit) :
    BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ProcessLog", "SnackBarLocalBroadcastReceiver is Recieve")
        val uriString = intent.getStringExtra(MainActivity.INTENT_FILTER_SNACKBAR_URI)
        val uri = Uri.parse(uriString)
        snackbarCallback(uri)
    }
}