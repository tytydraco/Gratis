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

    private val openDocument = registerForActivityResult(ActivityResultContracts.CreateDocument()) {
        if (it != null)
            viewModel.saveTextToUri(it)
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
                    viewModel.loadTextFromString(it)
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
        viewModel.updateContents(text.text.toString())
        super.onPause()
    }

    override fun onBackPressed() {
        val content = text.text.toString()

        if (viewModel.contentsChanged(content)) {
            viewModel.updateContents(content)
            openDocument.launch("")
        } else {
            super.onBackPressed()
        }
    }
}