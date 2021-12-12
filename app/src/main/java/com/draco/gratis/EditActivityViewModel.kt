package com.draco.gratis

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class EditActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var handled = false

    private val contentResolver = application.contentResolver

    val content = MutableLiveData("")
    private var initialHashCode = -1

    private fun loadText(text: String) {
        content.postValue(text)
        initialHashCode = text.hashCode()
    }

    fun loadTextFromString(text: String) {
        if (handled)
            return
        handled = true

        loadText(text)
    }

    fun loadTextFromUri(uri: Uri) {
        if (handled)
            return
        handled = true

        contentResolver
            .openInputStream(uri)
            ?.bufferedReader()
            .use { reader ->
                reader?.readText()?.let {
                    loadText(it)
                }
            }
    }

    fun saveTextToUri(uri: Uri) {
        contentResolver
            .openOutputStream(uri)
            ?.bufferedWriter()
            .use {
                it?.write(content.value)
            }
    }
}