package ru.pleshivtseva.myfirstapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.pleshivtseva.myfirstapp.databinding.CardPostBinding
import ru.pleshivtseva.myfirstapp.dto.Post

class PostsAdapter(
    private val listener: OnPostInteractionListener) : ListAdapter<Post, PostViewHolder>(
    PostDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, listener)
    }
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

