package com.example.roomwithaview

import android.app.Application
import com.example.roomwithaview.database.ToDoItemRepository
import com.example.roomwithaview.database.ToDoItemRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ToDoListApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { ToDoItemRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ToDoItemRepository(database.toDoItemDao()) }

}