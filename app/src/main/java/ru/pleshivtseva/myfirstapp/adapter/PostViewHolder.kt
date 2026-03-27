package ru.pleshivtseva.myfirstapp.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ru.pleshivtseva.myfirstapp.R
import ru.pleshivtseva.myfirstapp.databinding.CardPostBinding
import ru.pleshivtseva.myfirstapp.databinding.ItemVideoBinding
import ru.pleshivtseva.myfirstapp.dto.Post
import ru.pleshivtseva.myfirstapp.util.FormatUtils
import androidx.core.net.toUri
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
            like.text = FormatUtils.formatCount(post.likes)
            share.text = FormatUtils.formatCount(post.shares)
            views.text = FormatUtils.formatCount(post.views)

            if (post.video.isNullOrBlank()) {
                videoContainer.removeAllViews()
                videoContainer.visibility = View.GONE
            } else {
                videoContainer.visibility = View.VISIBLE
                videoContainer.removeAllViews()

                val videoBinding = ItemVideoBinding.inflate(LayoutInflater.from(itemView.context), videoContainer, true)

                videoBinding.videoUrl.text = post.video

                videoContainer.setOnClickListener {
                    openVideo(post.video)
                }
            }
            root.setOnClickListener {
                listener.onPostClick(post)
            }

            like.setOnClickListener {
                listener.onLike(post)
                it.stopPropagation()
            }

            share.setOnClickListener {
                listener.onShare(post)
                it.stopPropagation()
            }

            avatar.setOnClickListener {
                listener.onAvatarClick(post)
                it.stopPropagation()
            }

            menu.setOnClickListener { view ->
                showPopupMenu(view, post)
            }

        }
    }
    fun View.stopPropagation() {
        isClickable = true
        setOnClickListener {}
    }

    private fun openVideo(videoUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, videoUrl.toUri())
        val packageManager = itemView.context.packageManager
        val activities = packageManager.queryIntentActivities(intent, 0)
        Log.d("VideoIntent", "queryIntentActivities: $activities")

        val resolveInfo = intent.resolveActivity(packageManager)
        Log.d("VideoIntent", "resolveActivity: $resolveInfo")

        try {
            if (intent.resolveActivity(itemView.context.packageManager) != null)
                itemView.context.startActivity(intent)
            else
                Toast.makeText(itemView.context, R.string.error_no_video_app, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(itemView.context, R.string.error_cannot_open_video, Toast.LENGTH_SHORT).show()
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
}


