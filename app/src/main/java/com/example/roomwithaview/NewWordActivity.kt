package com.example.roomwithaview

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class NewWordActivity : AppCompatActivity() {

    private lateinit var todoItemTitle: EditText
    private lateinit var todoItemContent: EditText
    private lateinit var todoItemDue: TextView
    private lateinit var dateButton: Button
    private lateinit var todoItemCompleteCheck: CheckBox

    //calendar
    var calendar = Calendar.getInstance()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_word)
        setTitle("Add a " + "New TODO Item")

        todoItemTitle = findViewById(R.id.todo_item_title)
        todoItemContent = findViewById(R.id.todo_item_content)
        todoItemDue = findViewById(R.id.todo_item_due)
        dateButton = findViewById(R.id.dateTimeButton)

        // create an OnDateSetListener
        val timeSetListener = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {

                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                var AMPM: String = ""
                if (hour < 12) {
                    AMPM = "AM"
                } else {
                    AMPM = "PM"
                }
                updateDateInView(AMPM)
            }
        }


        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                //updateDateInView()
                val timePicker = TimePickerDialog(
                    this@NewWordActivity,
                    R.style.TimePickerTheme,
                    timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        dateButton.setOnClickListener {
            DatePickerDialog(
                this@NewWordActivity,
                R.style.TimePickerTheme,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(todoItemTitle.text) || TextUtils.isEmpty(todoItemContent.text) || TextUtils.isEmpty(
                    todoItemDue.text
                )
            ) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val title = todoItemTitle.text.toString()
                val content = todoItemContent.text.toString()
                val due = updateDateInView2(todoItemDue.text.toString())
                replyIntent.putExtra(EXTRA_REPLY_TITLE, title)
                replyIntent.putExtra(EXTRA_REPLY_CONTENT, content)
                replyIntent.putExtra(EXTRA_REPLY_DUE, due)
                replyIntent.putExtra(EXTRA_REPLY_RAW, todoItemDue.text.toString())
                setResult(Activity.RESULT_OK, replyIntent)
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDateInView2(testTime: String): Long {
        val myFormat = "MM/dd/yyyy h:mm a" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return sdf.parse(testTime).time
    }

    @SuppressLint("SetTextI18n")
    private fun updateDateInView(ampm: String) {
        val myFormat = "MM/dd/yyyy h:mm a" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        todoItemDue.text = sdf.format(calendar.time)
    }

    companion object {
        const val EXTRA_REPLY_TITLE = "com.example.android.wordlistsql.REPLYTITLE"
        const val EXTRA_REPLY_CONTENT = "com.example.android.wordlistsql.REPLYCONTENT"
        const val EXTRA_REPLY_DUE = "com.example.android.wordlistsql.REPLYDUE"
        const val EXTRA_REPLY_RAW = "com.example.android.wordlistsql.REPLYRAWDUE"
    }
}
