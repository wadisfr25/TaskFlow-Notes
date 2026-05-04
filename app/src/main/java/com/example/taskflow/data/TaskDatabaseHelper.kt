package com.example.taskflow.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    fun insertTask(task: Task): Long {
        val database = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_CATEGORY, task.category)
            put(COLUMN_CREATED_AT, task.createdAt)
        }
        return database.insert(TABLE_TASKS, null, values)
    }

    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val database = readableDatabase
        val cursor = database.query(
            TABLE_TASKS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_CREATED_AT DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                tasks.add(
                    Task(
                        id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                        description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        category = it.getString(it.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COLUMN_CREATED_AT))
                    )
                )
            }
        }

        return tasks
    }

    fun getTaskById(taskId: Long): Task? {
        val database = readableDatabase
        val cursor = database.query(
            TABLE_TASKS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(taskId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return Task(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    category = it.getString(it.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    createdAt = it.getLong(it.getColumnIndexOrThrow(COLUMN_CREATED_AT))
                )
            }
        }

        return null
    }

    fun updateTask(task: Task): Int {
        val database = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_CATEGORY, task.category)
            put(COLUMN_CREATED_AT, task.createdAt)
        }
        return database.update(
            TABLE_TASKS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(task.id.toString())
        )
    }

    fun deleteTask(taskId: Long): Int {
        val database = writableDatabase
        return database.delete(
            TABLE_TASKS,
            "$COLUMN_ID = ?",
            arrayOf(taskId.toString())
        )
    }

    companion object {
        private const val DATABASE_NAME = "taskflow_notes.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_TASKS = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_CREATED_AT = "created_at"
    }
}
