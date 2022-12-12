package com.example.roomwithaview

import android.util.Log
import androidx.lifecycle.*
import com.example.roomwithaview.database.ToDoItem
import com.example.roomwithaview.database.ToDoItemRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class ToDoListViewModel(private val repository: ToDoItemRepository) : ViewModel() {

    companion object {
        var isDeletingAll: Boolean = false
    }

    private val firebaseDatabase = Firebase.firestore

    val allToDoItems: LiveData<List<ToDoItem>> = repository.allToDoItems.asLiveData()


    fun updateFirebase(userId: String) {
        println("state: $isDeletingAll")
        if (!isDeletingAll) {
            firebaseDatabase.collection("User").document(userId)
                .set(allToDoItems)
                .addOnSuccessListener {
                    Log.d("Successful", "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.w("Unsuccessful", "Error writing document", e)
                }
            isDeletingAll = false
        }
    }

    fun insert(toDoItem: ToDoItem) = viewModelScope.launch {
        repository.insert(toDoItem)
    }

    fun update(
        id: Int,
        userId: String,
        updateTitle: String,
        updateContent: String,
        updateDate: Long,
        updateCompleted: Int
    ) = viewModelScope.launch {
        repository.update(id, userId, updateTitle, updateContent, updateDate, updateCompleted)
    }

    fun deleteById(id: Int) = viewModelScope.launch {
        repository.deleteById(id)
    }

    fun deleteAll() = viewModelScope.launch {
        isDeletingAll = true
        repository.deleteAll()
    }

    class ToDoListViewModelFactory(private val repository: ToDoItemRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ToDoListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ToDoListViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}