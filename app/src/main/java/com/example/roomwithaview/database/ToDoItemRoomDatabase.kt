package com.example.roomwithaview.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(ToDoItem::class), version = 1, exportSchema = false)
public abstract class ToDoItemRoomDatabase : RoomDatabase() {

    abstract fun toDoItemDao(): ToDoItemDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ToDoItemRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ToDoItemRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoItemRoomDatabase::class.java,
                    "todoitems_database"
                )
                    .addCallback(ToDoItemDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    private class ToDoItemDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.toDoItemDao())
                }
            }
        }

        suspend fun populateDatabase(toDoItemDao: ToDoItemDao) {
            // Delete all content here.
            toDoItemDao.deleteAll()

            // Add sample words.
//            val toDoItem = ToDoItem(0,"Assignment 2", "Complete Assignment 2", 0, 0)
//            toDoItemDao.insert(toDoItem)
        }
    }

}