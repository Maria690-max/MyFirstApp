package ru.pleshivtseva.myfirstapp.repository

import androidx.lifecycle.LiveData
import ru.pleshivtseva.myfirstapp.dto.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun increaseViews(id: Long)
    fun save(post: Post)
    fun removeById(id: Long)


}


