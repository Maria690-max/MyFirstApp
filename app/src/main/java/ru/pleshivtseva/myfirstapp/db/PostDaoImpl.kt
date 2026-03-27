package ru.pleshivtseva.myfirstapp.db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.pleshivtseva.myfirstapp.dto.Post
import ru.pleshivtseva.myfirstapp.db.PostContract.Columns

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()

        // Выполняем запрос SELECT  FROM posts ORDER BY _id DESC
        val cursor = db.query(
            PostContract.TABLE_NAME,
            Columns.ALL_COLUMNS,
            null, null, null, null,
            "${Columns._ID} DESC"
        )

        cursor.use { // Автоматически закроется после использования
            while (it.moveToNext()) {
                posts.add(mapCursorToPost(it))
            }
        }

        return posts
    }

    override fun getById(id: Long): Post? {
        val cursor = db.query(
            PostContract.TABLE_NAME,
            Columns.ALL_COLUMNS,
            "${Columns._ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return cursor.use {
            if (it.moveToFirst()) mapCursorToPost(it) else null
        }
    }

    override fun insert(post: Post): Post {
        val values = postToContentValues(post).apply {
            remove(Columns._ID) // ID сгенерируется автоматически
        }

        val newId = db.insert(PostContract.TABLE_NAME, null, values)
        return getById(newId) ?: post.copy(id = newId)
    }

    override fun update(post: Post): Post {
        val values = postToContentValues(post).apply {
            remove(Columns._ID) // ID не обновляем
        }

        db.update(
            PostContract.TABLE_NAME,
            values,
            "${Columns._ID} = ?",
            arrayOf(post.id.toString())
        )
        return getById(post.id) ?: post
    }

    override fun delete(id: Long) {
        db.delete(
            PostContract.TABLE_NAME,
            "${Columns._ID} = ?",
            arrayOf(id.toString())
        )
    }

    override fun likeById(id: Long) {
        // Увеличиваем лайки и меняем likedByMe
        val post = getById(id) ?: return

        val newLikedByMe = !post.likedByMe
        val newLikes = if (newLikedByMe) post.likes + 1 else post.likes - 1

        val values = ContentValues().apply {
            put(Columns.LIKED_BY_ME, if (newLikedByMe) 1 else 0)
            put(Columns.LIKES, newLikes)
        }

        db.update(
            PostContract.TABLE_NAME,
            values,
            "${Columns._ID} = ?",
            arrayOf(id.toString())
        )
    }

    override fun shareById(id: Long) {
        val values = ContentValues().apply {
            put(Columns.SHARES, "${Columns.SHARES} + 1") // Нельзя так!
            // Правильно будет сначала прочитать, потом увеличить
        }
        // Более сложная операция, реализуем через raw query
        db.execSQL(
            "UPDATE ${PostContract.TABLE_NAME} SET ${Columns.SHARES} = ${Columns.SHARES} + 1 WHERE ${Columns._ID} = ?",
            arrayOf(id)
        )
    }

    override fun increaseViews(id: Long) {
        db.execSQL(
            "UPDATE ${PostContract.TABLE_NAME} SET ${Columns.VIEWS} = ${Columns.VIEWS} + 1 WHERE ${Columns._ID} = ?",
            arrayOf(id)
        )
    }

    // Маппинг: Cursor -> Post
    private fun mapCursorToPost(cursor: Cursor): Post {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(Columns._ID))
        val author = cursor.getString(cursor.getColumnIndexOrThrow(Columns.AUTHOR))
        val authorId = cursor.getLong(cursor.getColumnIndexOrThrow(Columns.AUTHOR_ID))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(Columns.CONTENT))
        val published = cursor.getString(cursor.getColumnIndexOrThrow(Columns.PUBLISHED))
        val likedByMe = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.LIKED_BY_ME)) != 0
        val likes = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.LIKES))
        val shares = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.SHARES))
        val views = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.VIEWS))
        val video = cursor.getString(cursor.getColumnIndexOrThrow(Columns.VIDEO))

        return Post(
            id = id,
            author = author,
            authorId = authorId,
            content = content,
            published = published,
            likedByMe = likedByMe,
            likes = likes,
            shares = shares,
            views = views,
            video = video
        )
    }

    // Маппинг: Post -> ContentValues
    private fun postToContentValues(post: Post): ContentValues {
        return ContentValues().apply {
            put(Columns._ID, post.id)
            put(Columns.AUTHOR, post.author)
            put(Columns.AUTHOR_ID, post.authorId)
            put(Columns.CONTENT, post.content)
            put(Columns.PUBLISHED, post.published)
            put(Columns.LIKED_BY_ME, if (post.likedByMe) 1 else 0)
            put(Columns.LIKES, post.likes)
            put(Columns.SHARES, post.shares)
            put(Columns.VIEWS, post.views)
            if (post.video != null) {
                put(Columns.VIDEO, post.video)
            } else {
                putNull(Columns.VIDEO)
            }
        }
    }
}
