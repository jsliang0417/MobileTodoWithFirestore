package com.example.roomwithaview

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.roomwithaview.database.ToDoItem
import java.text.SimpleDateFormat
import java.util.*


class detail_activity : AppCompatActivity() {

    private lateinit var displayItemContent: TextView
    private lateinit var displayItemDate: TextView
    private lateinit var displayItemCompletion: CheckBox
    private lateinit var displayItemTimeButton: Button
    private lateinit var displaySaveButton: Button
    private lateinit var uid: String

    //calendar
    var calendar = Calendar.getInstance()

    private val toDoListViewModel: ToDoListViewModel by viewModels {
        ToDoListViewModel.ToDoListViewModelFactory((application as ToDoListApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var details = intent.getParcelableExtra<ToDoItem>("Details")
        val displayItemTitle = findViewById<TextView>(R.id.todo_item_title)
        displayItemContent = findViewById<TextView>(R.id.todo_item_content)
        displayItemDate = findViewById<TextView>(R.id.todo_item_due)
        displayItemCompletion = findViewById<CheckBox>(R.id.checkBoxDetail)
        displayItemTimeButton = findViewById<Button>(R.id.dateTimeUpdateButton)
        displaySaveButton = findViewById<Button>(R.id.button_save)
        //println("here in the second page onCreate()")

        //time
        //create an OnTimeSetListner
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
                    this@detail_activity,
                    R.style.TimePickerTheme,
                    timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        displayItemTimeButton.setOnClickListener {
            DatePickerDialog(
                this@detail_activity,
                R.style.TimePickerTheme,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        val secondActivityTopBar = supportActionBar
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("detailTitle")) {
                secondActivityTopBar?.title = intent.getStringExtra("detailTitle").toString()
                displayItemTitle.setText(intent.getStringExtra("detailTitle").toString())
            }
            if (extras.containsKey("detailContent")) {
                displayItemContent.setText(intent.getStringExtra("detailContent").toString())
            }
            if (extras.containsKey("detailTime")) {
                val sdf = SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US)
                val testingTime = intent.getLongExtra("detailTime", 0)
                displayItemDate.setText(sdf.format(testingTime?.let { Date(it) }))
            }
            if (extras.containsKey("checkBoxComplete")) {
                if (intent.getIntExtra("checkBoxComplete", 0) == 1) {
                    displayItemCompletion.isChecked = true
                } else {
                    displayItemCompletion.isChecked = false
                }
            }
        }

        displaySaveButton.setOnClickListener {

            val id: Int = intent.getIntExtra("id", 0)
            val updateId: String = intent.getStringExtra("detailId").toString()
            val updateTitle: String = displayItemTitle.text.toString()
            val updateContent: String = displayItemContent.text.toString()
            val updateDate: Long = timeConverter(displayItemDate.text.toString())
            var updateCompleted: Int = 0
            if (displayItemCompletion.isChecked) {
                updateCompleted = 1
                intent.putExtra("isChecked", 1)
                //println("is checked")
            } else {
                updateCompleted = 0
                intent.putExtra("isChecked", 0)
            }
            toDoListViewModel.update(
                id,
                updateId,
                updateTitle,
                updateContent,
                updateDate,
                updateCompleted
            )

            val alarmManager: AlarmManager = this.applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
            val alarmIntent: Intent = Intent(this, AlarmReceiver::class.java)
            val testingIntent: Intent = Intent(this, MainActivity::class.java)
            testingIntent.putExtra("testing", (intent.getLongExtra("detailTime", 0)).toInt())
            alarmIntent.putExtra("due", updateDate)
            alarmIntent.putExtra("title", updateTitle)
            alarmIntent.putExtra("due", testing(updateDate))
//            Log.w("uid in detail", uid.toString())
//            testingIntent.putExtra("uid", uid)
            val pendingAlarmIntent = PendingIntent.getBroadcast(this, (intent.getLongExtra("detailTime", 0)).toInt(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, updateDate, pendingAlarmIntent)

            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        secondActivityTopBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun timeConverter(rawDueTime: String): Long {
        val myFormat = "MM/dd/yyyy h:mm a" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return sdf.parse(rawDueTime).time
    }

    private fun testing(ampm: Long): String {
        val myFormat = "MM/dd/yyyy h:mm a" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return sdf.format(calendar.time)
    }

    private fun updateDateInView(ampm: String) {
        val myFormat = "MM/dd/yyyy h:mm a" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        displayItemDate.text = sdf.format(calendar.time)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}