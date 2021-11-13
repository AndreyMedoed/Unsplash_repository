package com.example.unsplash.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
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