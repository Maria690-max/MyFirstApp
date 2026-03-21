package ru.pleshivtseva.myfirstapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import ru.pleshivtseva.myfirstapp.adapter.OnPostInteractionListener
import ru.pleshivtseva.myfirstapp.adapter.PostsAdapter
import ru.pleshivtseva.myfirstapp.databinding.ActivityMainBinding
import ru.pleshivtseva.myfirstapp.dto.Post
import ru.pleshivtseva.myfirstapp.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private val viewModel: PostViewModel by viewModels()

    // ID поста, который редактируется (0 = новый пост)
    private var editingPostId: Long = 0L

    private val interactionListener = object : OnPostInteractionListener {
        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        override fun onShare(post: Post) {
            viewModel.shareById(post.id)
            Toast.makeText(this@MainActivity, "Репост +1", Toast.LENGTH_SHORT).show()
        }

        override fun onEdit(post: Post) {
            // Сохраняем ID редактируемого поста
            editingPostId = post.id
            // Устанавливаем текст в поле ввода
            binding.content.setText(post.content)
            binding.content.setSelection(binding.content.text.length)
            // Переводим фокус и показываем клавиатуру
            binding.content.requestFocus()
            showKeyboard(binding.content)
            // Показываем панель отмены
            binding.cancelGroup.visibility = View.VISIBLE
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

        // Наблюдаем за режимом редактирования, чтобы показывать/скрывать панель отмены
        viewModel.editingMode.observe(this) { isEditing ->
            if (isEditing) {
                binding.cancelGroup.visibility = View.VISIBLE
            } else {
                binding.cancelGroup.visibility = View.GONE
            }
        }

        // Наблюдаем за редактируемым постом, чтобы обновлять поле ввода
        viewModel.edited.observe(this) { post ->
            if (post.id != 0L) { // Если это редактирование существующего поста
                binding.content.setText(post.content)
                binding.content.setSelection(post.content.length) // Перемещаем курсор в конец
                binding.content.requestFocus() // Переводим фокус
                showKeyboard(binding.content) // Показываем клавиатуру
            } else { // Если это создание нового поста
                binding.content.setText("")
            }
        }


        // Отслеживание изменений текста от пользователя
        binding.content.addTextChangedListener { text ->
            // Обновляем ViewModel при изменении текста пользователем
            viewModel.changeContent(text.toString())

            // Если режим редактирования активен, и текст в поле ввода пустой,
            // но это не создание нового поста, то можно сбросить режим редактирования.
            // Это может быть полезно, если пользователь очистит весь текст редактируемого поста.
            if (viewModel.editingMode.value == true && text.isNullOrBlank() && editingPostId != 0L) {
                // Можно добавить логику для сброса, если это необходимо.
                // Например, если пользователь удалил весь текст редактируемого поста,
                // можно предложить вернуться к созданию нового или отменить.
                // В данном примере, мы просто продолжаем наблюдать и позволим кнопке Save/Cancel решить.
            }
        }

        // Кнопка сохранения
        binding.save.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, "Введите текст поста", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Если редактируем существующий пост (editingPostId != 0L)
            if (editingPostId != 0L) {
                viewModel.saveEditedPost(editingPostId, text)
                // Сбрасываем editingPostId после сохранения
                editingPostId = 0L
            } else {
                // Создаем новый пост
                viewModel.changeContent(text) // Убеждаемся, что ViewModel имеет актуальный контент
                viewModel.save()
            }

            // Очищаем поле ввода
            binding.content.text.clear()
            // Скрываем панель отмены (это будет сделано через наблюдение за editingMode)
            // Скрываем клавиатуру
            hideKeyboard(binding.content)
        }

        // Кнопка отмены редактирования
        binding.cancel.setOnClickListener {
            // Очищаем ID редактируемого поста
            editingPostId = 0L
            // Очищаем поле ввода
            binding.content.text.clear()
            // Скрываем панель отмены (это будет сделано через наблюдение за editingMode)
            // Скрываем клавиатуру
            hideKeyboard(binding.content)
            // Отменяем редактирование в ViewModel
            viewModel.cancelEdit()
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

