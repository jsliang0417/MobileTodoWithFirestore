package com.example.roomwithaview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.notificationexample.NotificationUtils

class AlarmReceiver : BroadcastReceiver() {

    private val CHANNELID = "ToDoItemNotificationChannel"
    override fun onReceive(context: Context, intent: Intent) {
        val myRandInt = intent.getLongExtra(MainActivity.EXTRA_RAND_INT, 0)
        intent.getStringExtra("title")?.let { NotificationUtils().createNotification(it, context) }
    }
}