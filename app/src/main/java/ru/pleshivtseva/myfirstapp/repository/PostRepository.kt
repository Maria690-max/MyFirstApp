package ru.pleshivtseva.myfirstapp.repository
import androidx.lifecycle.LiveData
import ru.pleshivtseva.myfirstapp.dto.Post
interface PostRepository {       fun get(): LiveData<Post>

    // Лайк/дизлайк
    fun like()

    // Репост (увеличение счетчика)
    fun share()

    // Изменение просмотров (может пригодиться позже)
    fun increaseViews()
}

