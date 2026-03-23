package ru.pleshivtseva.myfirstapp.adapter

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.pleshivtseva.myfirstapp.R
import ru.pleshivtseva.myfirstapp.databinding.CardPostBinding
import ru.pleshivtseva.myfirstapp.dto.Post
import java.text.DecimalFormat

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: OnPostInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content

            // Для кнопки лайка используем isChecked и текст
            like.isChecked = post.likedByMe
            like.text = formatCount(post.likes)

            // Для репоста и просмотров - только текст
            share.text = formatCount(post.shares)
            views.text = formatCount(post.views)

            // Обработчики кликов
            like.setOnClickListener {
                listener.onLike(post)
            }

            share.setOnClickListener {
                listener.onShare(post)
            }

            avatar.setOnClickListener {
                listener.onAvatarClick(post)
            }

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
                    // ИспользуемLocale.US для точки как разделителя десятичных разрядов
                    DecimalFormat.getNumberInstance(java.util.Locale.US).apply {
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
                    // Используем Locale.US для точки как разделителя десятичных разрядов
                    DecimalFormat.getNumberInstance(java.util.Locale.US).apply {
                        maximumFractionDigits = 1
                        minimumFractionDigits = 1
                    }.format(thousands) + "K"
                }
            }
            else -> count.toString()
        }
    }
}


