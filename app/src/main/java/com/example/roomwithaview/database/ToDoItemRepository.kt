package com.example.roomwithaview.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ToDoItemRepository(private val toDoItemDao: ToDoItemDao) {

    val allToDoItems: Flow<List<ToDoItem>> = toDoItemDao.getToDoItems()
//    val allToDoItems: Flow<Map<Int, ToDoItem>> = toDoItemDao.getToDoItems()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(toDoItem: ToDoItem) {
        toDoItemDao.insert(toDoItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(
        id: Int,
        userId: String,
        updateTitle: String,
        updateContent: String,
        updateDate: Long,
        updateCompleted: Int
    ) {
        toDoItemDao.updateToDoItem(
            id,
            userId,
            updateTitle,
            updateContent,
            updateDate,
            updateCompleted
        )
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteById(id: Int) {
        toDoItemDao.deleteById(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        toDoItemDao.deleteAll()
    }
}