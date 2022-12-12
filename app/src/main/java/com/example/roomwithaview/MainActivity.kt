package com.example.roomwithaview

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notificationexample.NotificationUtils
import com.example.roomwithaview.database.FirebaseToDoItem
import com.example.roomwithaview.database.ToDoItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.lang.reflect.Array
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private val newWordActivityRequestCode = 1
    private var itemDue: Long = 0
    private var itemDelete: String = ""
    private var userId: String = ""
    private var username: String = ""

    private val firebaseDatabase = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth

    private val toDoListViewModel: ToDoListViewModel by viewModels {
        ToDoListViewModel.ToDoListViewModelFactory((application as ToDoListApplication).repository)
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun recyclerAdapterItemClicked(id: Int, userId: String, itemDue: Long, completed: Int) {
        println("Item Clicked: $userId")
        this.itemDue = itemDue
        this.itemDelete = userId
        val alarmManager: AlarmManager =
            this.applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val testingIntent = PendingIntent.getBroadcast(
            this,
            intent.getIntExtra("testing", 0),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(testingIntent)
        val pendingAlarmIntent = PendingIntent.getBroadcast(
            this,
            this.itemDue.toInt(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingAlarmIntent)
        println("main: ${this.itemDue}")
        toDoListViewModel.deleteById(id)
        println("userid: $userId")
        println("userid: $id")
        firebaseDatabase.collection("User").document(userId)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationUtils().createNotificationChannel(application)
        setContentView(R.layout.activity_main)
        val MainActivityTopBar = supportActionBar
        firebaseAuth = FirebaseAuth.getInstance()
        ToDoListViewModel.isDeletingAll = false
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = ToDoListAdapter(this::recyclerAdapterItemClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        username = intent.getStringExtra("email")?.substringBefore("@")?.capitalize(
            Locale.ROOT
        ).toString()

        userId = intent.getStringExtra("uid").toString()

        MainActivityTopBar?.title = "${username}'s TODO App"
        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        toDoListViewModel.allToDoItems.observe(this) { words ->
            // Update the cached copy of the words in the adapter.
            words.let {
                adapter.submitList(it)
            }
            if (words.isNotEmpty()) {
                toDoListViewModel.updateFirebase(userId)
            }
        }

        if (!ToDoListViewModel.isDeletingAll) {
            println("user: $userId")
            firebaseDatabase.collection("User").document(userId)
                .addSnapshotListener { document, e ->
                    if (e != null) {
                        Log.d("Empty", e.toString())
                    } else if (document != null) {
                        //println("here: ${document.getBlob("doc")}")
                        println("testing: ${document.get("value").toString()}")
                        if (document.get("value").toString() == "null") {
                            println("you have nothing to load")
                        } else {
                            println("you have something to load!")
                            val data: ArrayList<HashMap<String, Any>> =
                                document.data?.get("value") as ArrayList<HashMap<String, Any>>
                            for (d in data) {
                                for ((k, v) in d) {
                                    //println("$k, $v")
//                                    Log.d("testing", d["userId"].toString())
                                    val todo = ToDoItem(
                                        d["id"].toString().toInt(),
                                        d["userId"].toString(),
                                        d["title"].toString(),
                                        d["content"].toString(),
                                        d["dueDate"].toString().toLong(),
                                        d["completed"].toString().toInt()
                                    )
                                    toDoListViewModel.insert(todo)
                                }
                            }
                        }
                        //receive document -> convert to TodoItem object -> insert into Room database
                        Log.d("retrieved data:", document.toString())
                    } else {
                        Log.d("no document found", "")
                    }
                }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewWordActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }

        val signOutFab = findViewById<FloatingActionButton>(R.id.logout)
        signOutFab.setOnClickListener {
            firebaseAuth.signOut()
            userId = ""
            ToDoListViewModel.isDeletingAll = true
            toDoListViewModel.deleteAll()
            Toast.makeText(this, "Successfully Signed Out!", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        //item click
        adapter.clickOnItem = {
            val intent = Intent(this, detail_activity::class.java)
            intent.putExtra("Details", it)
            startActivity(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val todoTitleIntent =
                intentData?.getStringExtra(NewWordActivity.EXTRA_REPLY_TITLE).toString()
            val todoContentIntent =
                intentData?.getStringExtra(NewWordActivity.EXTRA_REPLY_CONTENT).toString()
            val todoDueIntent = intentData?.getLongExtra(NewWordActivity.EXTRA_REPLY_DUE, 0)
//                ?.div(1000)
            val todoCompletionIntent = intentData?.getIntExtra("checkBoxComplete", 0)
            val rawDue = intentData?.getStringExtra("rawDue")
            println("userid: $userId")
            val toDoItem = todoCompletionIntent?.let {
                ToDoItem(
                    0, userId, todoTitleIntent, todoContentIntent, todoDueIntent,
                    it
                )
            }
            if (toDoItem != null) {
                toDoListViewModel.insert(toDoItem)

                println("insert time: ${toDoItem.dueDate?.div(1000)}")
                println("type: ${toDoItem.dueDate!!::class.simpleName}")
                val alarmManager: AlarmManager =
                    this.applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
                val alarmIntent: Intent = Intent(this, AlarmReceiver::class.java)
                alarmIntent.putExtra("due", rawDue)
                alarmIntent.putExtra("title", toDoItem.title)
                val pendingAlarmIntent = PendingIntent.getBroadcast(
                    this,
                    toDoItem.dueDate!!.toInt(),
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                toDoItem.dueDate?.let {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        toDoItem.dueDate!!, pendingAlarmIntent
                    )
                }
            }

            Toast.makeText(
                applicationContext,
                R.string.successfully_saved,
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            userId = currentUser.uid
            username = currentUser.email?.substringBefore("@")?.capitalize(
                Locale.ROOT
            ).toString()
            val MainActivityTopBar = supportActionBar
            MainActivityTopBar?.title = "${username}'s TODO App"
            Log.d("email-main activity", username)
            Log.d("uid-main activity", currentUser.uid)
        }
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    companion object {
        const val EXTRA_RAND_INT = "rememberanykeyisactuallyfine"
    }
}
