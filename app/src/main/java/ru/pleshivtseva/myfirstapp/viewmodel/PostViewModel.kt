package ru.pleshivtseva.myfirstapp.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.pleshivtseva.myfirstapp.dto.Post
import ru.pleshivtseva.myfirstapp.repository.PostRepository
import ru.pleshivtseva.myfirstapp.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel() {

    // Создаем экземпляр репозитория
    private val repository: PostRepository = PostRepositoryInMemoryImpl()

    // Данные, доступные для наблюдения
    val data: LiveData<Post> = repository.get()

    // Методы для вызова из Activity
    fun like() = repository.like()
    fun share() = repository.share()
    fun increaseViews() = repository.increaseViews()
}
