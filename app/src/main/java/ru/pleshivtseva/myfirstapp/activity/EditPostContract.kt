package ru.pleshivtseva.myfirstapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class EditPostContract : ActivityResultContract<String?, String?>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(context, EditPostActivity::class.java).apply {
            if (!input.isNullOrBlank())
                putExtra(Intent.EXTRA_TEXT, input)
        }
    }
    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return when {
            resultCode != Activity.RESULT_OK -> null
            intent == null -> null
            else -> intent.getStringExtra(Intent.EXTRA_TEXT)
        }
    }
}

