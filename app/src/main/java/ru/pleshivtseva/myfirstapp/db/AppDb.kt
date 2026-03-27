package ru.pleshivtseva.myfirstapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase

class AppDb private constructor(context: Context) {

    private val dbHelper: DbHelper = DbHelper(context.applicationContext)
    val postDao: PostDao by lazy { PostDaoImpl(readableDatabase) }

    val readableDatabase: SQLiteDatabase
        get() = dbHelper.readableDatabase

    val writableDatabase: SQLiteDatabase
        get() = dbHelper.writableDatabase

    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return INSTANCE ?: synchronized(this) {
                val instance = AppDb(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
