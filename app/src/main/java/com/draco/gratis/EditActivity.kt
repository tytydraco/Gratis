package com.draco.gratis

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class EditActivity : AppCompatActivity() {
    private val viewModel: EditActivityViewModel by viewModels()

    private lateinit var text: EditText

    private val openDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        if (it != null)
            viewModel.saveTextToUri(it)
        else
            finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        text = findViewById(R.id.text)

        viewModel.content.observe(this) {
            if (it != null)
                text.setText(it)
        }

        when (intent?.action) {
            Intent.ACTION_SEND -> {
                intent?.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    viewModel.loadText(it)
                }

                intent?.extras?.get(Intent.EXTRA_STREAM)?.let {
                    viewModel.loadTextFromUri(it as Uri)
                }
            }

            Intent.ACTION_VIEW -> {
                intent?.data?.let {
                    viewModel.loadTextFromUri(it)
                }
            }
        }
    }

    override fun onPause() {
        viewModel.content.postValue(text.text.toString())
        super.onPause()
    }

    override fun onBackPressed() = openDocument.launch(arrayOf("*/*"))
}