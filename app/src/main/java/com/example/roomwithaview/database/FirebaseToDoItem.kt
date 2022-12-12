package com.example.roomwithaview.database

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

data class FirebaseToDoItem(
    @get: PropertyName("id") @set: PropertyName("id") var id: Int = 0,
    @get: PropertyName("userId") @set: PropertyName("userId")var userId: String = "",
    @get: PropertyName("title") @set: PropertyName("title")var title: String = "",
    @get: PropertyName("content") @set: PropertyName("content")var content: String = "",
    @get: PropertyName("dueDate") @set: PropertyName("dueDate")var dueDate: Long? = 0,
    @get: PropertyName("completed") @set: PropertyName("completed")var completed: Int = 0
)
