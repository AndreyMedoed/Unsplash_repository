package com.example.unsplash.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.unsplash.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Fragment.toast(@StringRes stringRes: Int) {
    Toast.makeText(requireContext(), stringRes, Toast.LENGTH_SHORT).show()
}

fun EditText.textChangedFlow(): Flow<String> {
    return callbackFlow<String> {
        val textChangeListener = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sendBlocking(s?.toString().orEmpty())
            }

            override fun afterTextChanged(s: Editable?) {}

        }
        this@textChangedFlow.addTextChangedListener(textChangeListener)
        awaitClose {
            this@textChangedFlow.removeTextChangedListener(textChangeListener)
        }
    }
}

fun SearchView.searchTextChangedFlow(): Flow<String> {
    return callbackFlow<String> {
        val onQueryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                trySendBlocking(newText.orEmpty())
                return true
            }
        }
        this@searchTextChangedFlow.setOnQueryTextListener(onQueryTextListener)
        awaitClose {
            this@searchTextChangedFlow.setOnQueryTextListener(null)
        }
    }
}

fun Fragment.findTopNavController(): NavController {
    val topLevelHost = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment?
    return topLevelHost?.navController ?: findNavController()
}

fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }

    return result
}