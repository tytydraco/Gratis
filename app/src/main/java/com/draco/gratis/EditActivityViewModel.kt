package com.draco.gratis

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class EditActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var handled = false

    private val contentResolver = application.contentResolver

    private val _content = MutableLiveData("")
    val content: LiveData<String> = _content

    private var initialHashCode = -1

    fun loadTextFromString(text: String) {
        if (handled)
            return
        handled = true

        /*
         * We imported raw text, not URI contents. We do not know if there was ever
         * an original file to compare to. Do not save the checksum.
         */
        _content.postValue(text)
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
                    _content.postValue(it)
                    initialHashCode = it.hashCode()
                }
            }
    }

    fun saveTextToUri(uri: Uri) {
        contentResolver
            .openOutputStream(uri)
            ?.bufferedWriter()
            .use {
                it?.write(_content.value)
            }
    }

    fun updateContents(text: String) {
        _content.postValue(text)
    }

    fun contentsChanged(text: String) =
        initialHashCode != text.hashCode()
}