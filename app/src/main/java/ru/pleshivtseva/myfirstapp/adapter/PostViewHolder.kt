package ru.pleshivtseva.myfirstapp.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.pleshivtseva.myfirstapp.R
import ru.pleshivtseva.myfirstapp.databinding.CardPostBinding
import ru.pleshivtseva.myfirstapp.databinding.ItemVideoBinding
import ru.pleshivtseva.myfirstapp.dto.Post

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: OnPostInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content

            like.isChecked = post.likedByMe
            like.text = formatCount(post.likes)
            share.text = formatCount(post.shares)
            views.text = formatCount(post.views)

            // Обработка видео
            if (post.video.isNullOrBlank()) {
                // Если видео нет, скрываем контейнер
                videoContainer.removeAllViews()
                videoContainer.visibility = View.GONE
            } else {
                // Если есть, показываем и наполняем
                videoContainer.visibility = View.VISIBLE
                videoContainer.removeAllViews()

                val videoBinding = ItemVideoBinding.inflate(LayoutInflater.from(itemView.context), videoContainer, true)
                videoBinding.videoUrl.text = post.video

                // Обработка клика по контейнеру видео
                videoContainer.setOnClickListener {
                    openVideo(post.video!!)
                }
            }

            // Обработка кнопок
            like.setOnClickListener { listener.onLike(post) }
            share.setOnClickListener { listener.onShare(post) }
            avatar.setOnClickListener { listener.onAvatarClick(post) }
            menu.setOnClickListener { view ->
                showPopupMenu(view, post)
            }
        }
    }

    private fun showPopupMenu(anchor: View, post: Post) {
        PopupMenu(anchor.context, anchor).apply {
            inflate(R.menu.post_menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit -> {
                        listener.onEdit(post)
                        true
                    }
                    R.id.remove -> {
                        listener.onRemove(post)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> {
                val millions = count / 1_000_000.0
                if (millions % 1.0 == 0.0) {
                    "${millions.toInt()}M"
                } else {
                    java.text.DecimalFormat.getNumberInstance(java.util.Locale.US).apply {
                        maximumFractionDigits = 1
                        minimumFractionDigits = 1
                    }.format(millions) + "M"
                }
            }
            count >= 10_000 -> "${count / 1000}K"
            count >= 1_000 -> {
                val thousands = count / 1000.0
                if (thousands % 1.0 == 0.0) {
                    "${thousands.toInt()}K"
                } else {
                    java.text.DecimalFormat.getNumberInstance(java.util.Locale.US).apply {
                        maximumFractionDigits = 1
                        minimumFractionDigits = 1
                    }.format(thousands) + "K"
                }
            }
            else -> count.toString()
        }
    }

    // Вспомогательная функция для открытия видео по URL
    private fun openVideo(videoUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))

        // Получаем список приложений, которые могут обработать Intent
        val packageManager = itemView.context.packageManager
        val activities = packageManager.queryIntentActivities(intent, 0)

        // Логируем результат
        Log.d("VideoIntent", "queryIntentActivities: $activities")

        val resolveInfo = intent.resolveActivity(packageManager)
        Log.d("VideoIntent", "resolveActivity: $resolveInfo")

        // Проверяем и запускаем
        if (resolveInfo != null) {
            try {
                itemView.context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(itemView.context, R.string.error_cannot_open_video, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(itemView.context, R.string.error_no_video_app, Toast.LENGTH_SHORT).show()
        }
    }
}


