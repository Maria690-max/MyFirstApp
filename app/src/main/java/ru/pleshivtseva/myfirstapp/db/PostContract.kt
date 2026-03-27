package ru.pleshivtseva.myfirstapp.db

import android.provider.BaseColumns

object PostContract {
    // Имя таблицы
    const val TABLE_NAME = "posts"

    object Columns : BaseColumns {
        const val AUTHOR = "author"
        const val AUTHOR_ID = "author_id"
        const val CONTENT = "content"
        const val PUBLISHED = "published"
        const val LIKED_BY_ME = "liked_by_me"
        const val LIKES = "likes"
        const val SHARES = "shares"
        const val VIEWS = "views"
        const val VIDEO = "video"

        const val _ID = "_id"

        // Все колонки для выборки
        val ALL_COLUMNS = arrayOf(
            _ID, AUTHOR, AUTHOR_ID, CONTENT, PUBLISHED,
            LIKED_BY_ME, LIKES, SHARES, VIEWS, VIDEO
        )
    }
}
