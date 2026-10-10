package com.example.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * BroadcastReceiver responsible for delivering the daily practice reminder
 * strictly at the user's configured fixed time, and preventing any duplicate
 * or multiple notification firings.
 */
class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "DailyReminderReceiver onReceive: action=$action")

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = SettingsRepository(context)
                val settings = repository.userSettingsFlow.first()

                if (action == Intent.ACTION_BOOT_COMPLETED) {
                    // Device restarted: Reschedule the alarm for the user's fixed time
                    if (settings.dailyReminderEnabled) {
                        DailyReminderScheduler.scheduleDailyReminder(
                            context = context,
                            hour = settings.reminderHour,
                            minute = settings.reminderMinute
                        )
                        Log.d(TAG, "Rescheduled daily reminder on boot for ${settings.reminderHour}:${settings.reminderMinute}")
                    }
                    return@launch
                }

                if (action == ACTION_FIRE_DAILY_REMINDER) {
                    if (!settings.dailyReminderEnabled) {
                        Log.d(TAG, "Daily reminder is disabled in user settings. Skipping notification.")
                        return@launch
                    }

                    // Check if already notified today to prevent any duplicate notifications
                    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    val todayKey = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
                    val lastNotifiedDate = prefs.getString(KEY_LAST_NOTIFIED_DATE, null)

                    if (lastNotifiedDate == todayKey) {
                        Log.d(TAG, "Daily reminder already delivered today ($todayKey). Skipping duplicate.")
                    } else {
                        // Deliver the single daily reminder notification with user's selected style
                        NotificationHelper.showDailyReminderNotification(
                            context = context,
                            style = settings.reminderMessageStyle
                        )
                        prefs.edit().putString(KEY_LAST_NOTIFIED_DATE, todayKey).apply()
                        Log.d(TAG, "Daily reminder notification delivered successfully for date: $todayKey")
                    }

                    // Reschedule for the next day at the user's fixed hour & minute
                    DailyReminderScheduler.scheduleDailyReminder(
                        context = context,
                        hour = settings.reminderHour,
                        minute = settings.reminderMinute
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing reminder broadcast", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val TAG = "DailyReminderReceiver"
        const val ACTION_FIRE_DAILY_REMINDER = "com.example.ACTION_DAILY_REMINDER"
        private const val PREFS_NAME = "lisan_notification_tracking"
        private const val KEY_LAST_NOTIFIED_DATE = "last_daily_reminder_date"
    }
}
