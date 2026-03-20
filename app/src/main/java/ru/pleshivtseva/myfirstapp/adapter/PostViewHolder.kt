package ru.pleshivtseva.myfirstapp.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.pleshivtseva.myfirstapp.R
import ru.pleshivtseva.myfirstapp.databinding.CardPostBinding
import ru.pleshivtseva.myfirstapp.dto.Post
import java.text.DecimalFormat

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeClickListener: (Post) -> Unit,
    private val onShareClickListener: (Post) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
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

            like.setOnClickListener {
                onLikeClickListener(post)
            }

            share.setOnClickListener {
                onShareClickListener(post)
            }

            menu.setOnClickListener {
                android.widget.Toast.makeText(
                    itemView.context,
                    "Меню поста ${post.id}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }

            avatar.setOnClickListener {
                android.widget.Toast.makeText(
                    itemView.context,
                    "Профиль автора ${post.author}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
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
                    java.text.DecimalFormat(".").format(millions) + "M"
                }
            }
            count >= 10_000 -> "${count / 1000}K"
            count >= 1_000 -> {
                val thousands = count / 1000.0
                if (thousands % 1.0 == 0.0) {
                    "${thousands.toInt()}K"
                } else {
                    java.text.DecimalFormat(".").format(thousands) + "K"
                }
            }
            else -> count.toString()
        }
    }
}
