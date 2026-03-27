package ru.pleshivtseva.myfirstapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.pleshivtseva.myfirstapp.R
import ru.pleshivtseva.myfirstapp.databinding.ActivityEditPostBinding
class EditPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val existingText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (!existingText.isNullOrBlank()) {
            binding.content.setText(existingText)
            binding.title.setText(R.string.edit_post_edit_title)
        }
        binding.btnSave.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, R.string.error_empty_content, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val resultIntent = Intent().apply {
                putExtra(Intent.EXTRA_TEXT, text)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
