package ru.pleshivtseva.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import ru.pleshivtseva.myfirstapp.activity.EditPostContract
import ru.pleshivtseva.myfirstapp.adapter.OnPostInteractionListener
import ru.pleshivtseva.myfirstapp.adapter.PostsAdapter
import ru.pleshivtseva.myfirstapp.databinding.ActivityMainBinding
import ru.pleshivtseva.myfirstapp.dto.Post
import ru.pleshivtseva.myfirstapp.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: PostViewModel by viewModels()

    // Регистрация контракта для редактирования/создания поста
    private val editPostLauncher = registerForActivityResult(EditPostContract()) { result ->
        if (!result.isNullOrBlank()) {
            if (editingPostId != 0L) {
                viewModel.saveEditedPost(editingPostId, newContent = result)
                editingPostId = 0L

            } else {
                viewModel.changeContent(content = result)
                viewModel.save()
            }
        }
    }

    private val interactionListener = object : OnPostInteractionListener {
        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        override fun onShare(post: Post) {
            // Создаем Intent для отправки текста
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, post.content)
                type = "text/plain"
            }
            // Создаем Chooser с заголовком
            val chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_post_via))
            startActivity(chooserIntent)

            // Увеличиваем счетчик репостов
            viewModel.shareById(post.id)
        }

        override fun onEdit(post: Post) {
            editingPostId = post.id
            // Запускаем редактирование существующего поста с содержимым
            editPostLauncher.launch(post.content)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
            Toast.makeText(this@MainActivity, "Пост удален", Toast.LENGTH_SHORT).show()
        }

        override fun onAvatarClick(post: Post) {
            Toast.makeText(this@MainActivity, "Профиль: ${post.author}", Toast.LENGTH_SHORT).show()
            viewModel.increaseViews(post.id)
        }
    }
    private var editingPostId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка адаптера
        val adapter = PostsAdapter(interactionListener)
        binding.list.adapter = adapter

        // Наблюдение за списком постов
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        // Отслеживание изменений текста от пользователя
        binding.content.addTextChangedListener { text ->
            viewModel.changeContent(text.toString())
        }

        // Кнопка сохранения
        binding.save.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, "Введите текст поста", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (editingPostId != 0L) {
                viewModel.saveEditedPost(editingPostId, text)
                editingPostId = 0L
            } else {
                viewModel.changeContent(text)
                viewModel.save()
            }

            binding.content.text.clear()
            binding.cancelGroup.visibility = View.GONE
            hideKeyboard(binding.content)
        }

        // Кнопка отмены редактирования
        binding.cancel.setOnClickListener {
            editingPostId = 0L
            binding.content.text.clear()
            binding.cancelGroup.visibility = View.GONE
            hideKeyboard(binding.content)
            viewModel.cancelEdit()
        }

        // Обработка нажатия FAB для создания нового поста
        binding.fab.setOnClickListener {
            // Запускаем создание нового поста
            editPostLauncher.launch(null)  // null — это для нового поста
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.showSoftInput(view, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
    }
}

