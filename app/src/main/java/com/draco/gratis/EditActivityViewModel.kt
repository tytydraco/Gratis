package com.draco.gratis

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class EditActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var handled = false

    private val contentResolver = application.contentResolver

    val content = MutableLiveData("")

    fun loadText(text: String) {
        if (handled)
            return
        handled = true

        content.postValue(text)
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
                    content.postValue(it)
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