package ru.pleshivtseva.myfirstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import ru.pleshivtseva.myfirstapp.databinding.ActivityMainBinding
import ru.pleshivtseva.myfirstapp.dto.Post
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "21 мая в 18:36",
            likedByMe = false,
            likes = 999,
            shares = 25,
            views = 5700
        )
        bindPost(post)
        setupClickListeners()
    }
    private fun bindPost(post: Post) {

        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likeCount.text = formatCount(post.likes)
            shareCount.text = formatCount(post.shares)
            viewsCount.text = formatCount(post.views)
            if (post.likedByMe) {
                like.setImageResource(R.drawable.like_filled)
            } else {
                like.setImageResource(R.drawable.like_border)
            }
            linkTitle.text = "Новая Нетология: 4 уровня карьеры"
            linkUrl.text = "netology.ru"
        }
    }
    private fun setupClickListeners() {
        binding.apply {
            // Обработка лайка
            like.setOnClickListener {
                // Меняем состояние
                post = post.copy(
                    likedByMe = !post.likedByMe,
                    likes = if (post.likedByMe) post.likes - 1 else post.likes + 1
                )

                bindPost(post)
                Toast.makeText(this@MainActivity,
                    if (post.likedByMe) "Лайк поставлен" else "Лайк убран",
                    Toast.LENGTH_SHORT).show()
            }
            share.setOnClickListener {
                post = post.copy(
                    shares = post.shares + 1
                )

            }
            bindPost(post)
            Toast.makeText(this@MainActivity, "Репост +1", Toast.LENGTH_SHORT).show()
            menu.setOnClickListener {
                Toast.makeText(this@MainActivity, "Меню поста", Toast.LENGTH_SHORT).show()
            }
            avatar.setOnClickListener {
                Toast.makeText(this@MainActivity, "Профиль автора", Toast.LENGTH_SHORT).show()
            }
            root.setOnClickListener {
                println("CLICK: корневой layout")
                Toast.makeText(this@MainActivity, "Клик по фону", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> {
            val millions = count / 1_000_000.0
            // Если число миллионов целое, показываем без десятых
            if (millions % 1.0 == 0.0) {
                "${millions.toInt()}M"
            } else {
                // Оставляем один знак после запятой
                DecimalFormat(".").format(millions) + "M"
            }
        }
        count >= 10_000 -> {
            // После 10К сотни не показываем
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