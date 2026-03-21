package ru.pleshivtseva.myfirstapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.pleshivtseva.myfirstapp.dto.Post
import ru.pleshivtseva.myfirstapp.repository.PostRepository
import ru.pleshivtseva.myfirstapp.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel() {

    private val repository: PostRepository = PostRepositoryInMemoryImpl()

    private val empty = Post(
        id = 0,
        author = "",
        content = "",
        published = ""
    )

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


