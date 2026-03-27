package ru.pleshivtseva.myfirstapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.pleshivtseva.myfirstapp.dto.Post
import ru.pleshivtseva.myfirstapp.repository.PostRepository
import ru.pleshivtseva.myfirstapp.repository.PostRepositorySQLiteImpl
import ru.pleshivtseva.myfirstapp.db.AppDb

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // Используем SQLite репозиторий
    private val repository: PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao
    )

    private val empty = Post(
        id = 0,
        author = "",
        content = "",
        published = ""
    )

    // Новое поле для хранения черновика
    private val _draft = MutableLiveData("")
    val draft: LiveData<String> = _draft

    fun saveDraft(text: String) {
        _draft.value = text
    }

    fun clearDraft() {
        _draft.value = ""
    }

    val data: LiveData<List<Post>> = repository.getAll()
    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Post> = _edited
    private val _editingMode = MutableLiveData(false)
    val editingMode: LiveData<Boolean> = _editingMode

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun increaseViews(id: Long) = repository.increaseViews(id)
    fun removeById(id: Long) = repository.removeById(id)

    fun save() {
        _edited.value?.let { post ->
            if (post.content.isNotBlank()) repository.save(post)
        }
        _edited.value = empty
        _editingMode.value = false
    }

    fun changeContent(content: String) {
        val text = content.trim()
        _edited.value?.let { post -> if (post.content != text) _edited.value = post.copy(content = text) }
    }

    fun saveEditedPost(postId: Long, newContent: String) {
        val currentPosts = data.value ?: return
        val existingPost = currentPosts.find { it.id == postId } ?: return
        val updatedPost = existingPost.copy(content = newContent)

        repository.save(updatedPost)
        _edited.value = empty
        _editingMode.value = false
    }
}


