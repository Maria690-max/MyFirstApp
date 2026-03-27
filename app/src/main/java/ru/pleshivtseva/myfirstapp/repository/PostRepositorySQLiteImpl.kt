package ru.pleshivtseva.myfirstapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.pleshivtseva.myfirstapp.db.PostDao
import ru.pleshivtseva.myfirstapp.dto.Post

class PostRepositorySQLiteImpl(
    private val postDao: PostDao
) : PostRepository {

    // Кэш в памяти для быстрого доступа
    private var posts = emptyList<Post>()
    private val _data = MutableLiveData(posts)

    init {
        // Загружаем данные из БД при создании
        posts = postDao.getAll()
        _data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = _data

    override fun likeById(id: Long) {
        postDao.likeById(id)
        // Обновляем кэш
        posts = postDao.getAll()
        _data.value = posts
    }

    override fun shareById(id: Long) {
        postDao.shareById(id)
        posts = postDao.getAll()
        _data.value = posts
    }

    override fun increaseViews(id: Long) {
        postDao.increaseViews(id)
        posts = postDao.getAll()
        _data.value = posts
    }

    override fun save(post: Post): Post {
        val saved = if (post.id == 0L) {
            postDao.insert(post)
        } else {
            postDao.update(post)
        }
        posts = postDao.getAll()
        _data.value = posts
        return saved
    }

    override fun removeById(id: Long) {
        postDao.delete(id)
        posts = postDao.getAll()
        _data.value = posts
    }
}
