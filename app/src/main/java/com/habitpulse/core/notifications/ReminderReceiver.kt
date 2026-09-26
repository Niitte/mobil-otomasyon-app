package com.habitpulse.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getStringExtra("HABIT_ID")
        val habitTitle = intent.getStringExtra("HABIT_TITLE")

        Log.d("ReminderReceiver", "Reminder received for habit $habitId: $habitTitle")
        // Normally you would trigger a NotificationCompat.Builder here
    }
}
