package com.example.roomwithaview.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "todoitems_table")
data class ToDoItem(
    @get: PropertyName("id") @set: PropertyName("id") @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @get: PropertyName("userId") @set: PropertyName("userId") @ColumnInfo(name = "uid") var userId: String = "",
    @get: PropertyName("title") @set: PropertyName("title") @ColumnInfo(name = "title") var title: String = "",
    @get: PropertyName("content") @set: PropertyName("content") @ColumnInfo(name = "content") var content: String = "",
    @get: PropertyName("dueDate") @set: PropertyName("dueDate") @ColumnInfo(name = "due_date") var dueDate: Long? = 0,
    @get: PropertyName("completed") @set: PropertyName("completed") @ColumnInfo(name = "completed") var completed: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readInt(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeValue(userId)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeValue(dueDate)
        parcel.writeInt(completed)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ToDoItem> {
        override fun createFromParcel(parcel: Parcel): ToDoItem {
            return ToDoItem(parcel)
        }

        override fun newArray(size: Int): Array<ToDoItem?> {
            return arrayOfNulls(size)
        }
    }
}
