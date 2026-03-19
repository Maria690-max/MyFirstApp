package ru.pleshivtseva.myfirstapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.pleshivtseva.myfirstapp.databinding.ActivityMainBinding
import ru.pleshivtseva.myfirstapp.dto.Post
import ru.pleshivtseva.myfirstapp.viewmodel.PostViewModel
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Делегирование создания ViewModel
    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("Activity: onCreate")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Подписываемся на изменения данных
        viewModel.data.observe(this) { post ->
            bindPost(post)
        }

        setupClickListeners()
    }

    override fun onStart() {
        super.onStart()
        println("Activity: onStart")
    }

    override fun onResume() {
        super.onResume()
        println("Activity: onResume")
    }

    override fun onPause() {
        super.onPause()
        println("Activity: onPause")
    }

    override fun onStop() {
        super.onStop()
        println("Activity: onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("Activity: onDestroy")
    }

    private fun bindPost(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content

            // Форматируем и отображаем счетчики
            likeCount.text = formatCount(post.likes)
            shareCount.text = formatCount(post.shares)
            viewsCount.text = formatCount(post.views)

            // Устанавливаем иконку лайка в зависимости от состояния
            if (post.likedByMe) {
                like.setImageResource(R.drawable.like_filled)
            } else {
                like.setImageResource(R.drawable.like_border)
            }

            // Пример с ссылкой
            linkTitle.text = "Новая Нетология: 4 уровня карьеры"
            linkUrl.text = "netology.ru"
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            // Обработка лайка - вызываем метод ViewModel
            like.setOnClickListener {
                viewModel.like()
                Toast.makeText(this@MainActivity, "Лайк", Toast.LENGTH_SHORT).show()
            }

            // Обработка репоста - вызываем метод ViewModel
            share.setOnClickListener {
                viewModel.share()
                Toast.makeText(this@MainActivity, "Репост +1", Toast.LENGTH_SHORT).show()
            }

            menu.setOnClickListener {
                Toast.makeText(this@MainActivity, "Меню поста", Toast.LENGTH_SHORT).show()
            }

            avatar.setOnClickListener {
                Toast.makeText(this@MainActivity, "Профиль автора", Toast.LENGTH_SHORT).show()
                // Увеличиваем просмотры при клике на аватар (для примера)
                viewModel.increaseViews()
            }

            // Для исследования поведения
            root.setOnClickListener {
                println("CLICK: корневой layout")
                Toast.makeText(this@MainActivity, "Клик по фону", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> {
                val millions = count / 1_000_000.0
                if (millions % 1.0 == 0.0) {
                    "${millions.toInt()}M"
                } else {
                    DecimalFormat(".").format(millions) + "M"
                }
            }
            count >= 10_000 -> {
                "${count / 1000}K"
            }
            count >= 1_000 -> {
                val thousands = count / 1000.0
                if (thousands % 1.0 == 0.0) {
                    "${thousands.toInt()}K"
                } else {
                    DecimalFormat(".").format(thousands) + "K"
                }
            }
            else -> count.toString()
        }
    }
}
