package ru.pleshivtseva.myfirstapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.pleshivtseva.myfirstapp.dto.Post
import ru.pleshivtseva.myfirstapp.repository.PostRepository
import ru.pleshivtseva.myfirstapp.repository.PostRepositoryFileImpl  // или другую реализацию


class PostViewModel(application: Application) : AndroidViewModel(application) {

    // Используем файловую реализацию с передачей контекста приложения
    private val repository: PostRepository = PostRepositoryFileImpl(application)

    val data: LiveData<List<Post>> = repository.getAll()

    private val empty = Post(
        id = 0,
        author = "",
        content = "",
        published = ""
    )

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
            if (post.content.isNotBlank()) {
                repository.save(post)
            }
        }
        _edited.value = empty
        _editingMode.value = false
    }

    fun edit(post: Post) {
        _edited.value = post
        _editingMode.value = true
    }

    fun changeContent(content: String) {
        val text = content.trim()
        _edited.value?.let { post ->
            if (post.content != text) {
                _edited.value = post.copy(content = text)
            }
        }
    }

    fun cancelEdit() {
        _edited.value = empty
        _editingMode.value = false
    }

    fun saveEditedPost(postId: Long, newContent: String) {
        // Получаем текущие данные о посте
        val currentPosts = data.value ?: return
        val existingPost = currentPosts.find { it.id == postId } ?: return

        // Создаем обновленный пост с новым контентом
        val updatedPost = existingPost.copy(content = newContent)

        // Сохраняем через репозиторий
        repository.save(updatedPost)

        // Сбрасываем режим редактирования
        _edited.value = empty
        _editingMode.value = false
    }
}



