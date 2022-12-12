package com.example.roomwithaview.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoItemDao {

    @MapInfo(keyColumn = "id")
    @Query("SELECT * FROM todoitems_table order by completed ASC, due_date ASC")
    fun getToDoItems(): Flow<List<ToDoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(toDoItem: ToDoItem)

    @Query("UPDATE todoitems_table SET uid=:userId, title=:updateTitle, content=:updateContent, due_date=:updateDate, completed=:updateCompleted WHERE id = :id")
    suspend fun updateToDoItem(
        id: Int,
        userId: String,
        updateTitle: String,
        updateContent: String,
        updateDate: Long,
        updateCompleted: Int
    )

    @Query("DELETE FROM todoitems_table WHERE id=:id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM todoitems_table")
    suspend fun deleteAll()
}