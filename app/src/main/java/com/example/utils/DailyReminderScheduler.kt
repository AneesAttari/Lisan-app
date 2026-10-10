package com.example.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.WorkManager
import java.util.Calendar

/**
 * Schedules daily English practice notifications strictly at the user's fixed time
 * using AlarmManager, ensuring exactly ONE notification per day at the designated hour & minute.
 */
object DailyReminderScheduler {

    private const val TAG = "DailyReminderScheduler"
    private const val REMINDER_REQUEST_CODE = 4001

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        // Cancel any legacy WorkManager background tasks to prevent erratic multi-notifications
        cancelLegacyWorkManagerJobs(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If the time has already passed today, schedule for tomorrow at the same time
            if (timeInMillis <= now.timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, DailyReminderReceiver::class.java).apply {
            action = DailyReminderReceiver.ACTION_FIRE_DAILY_REMINDER
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    target.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    target.timeInMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled next daily practice reminder for: ${target.time}")
        } catch (e: SecurityException) {
            Log.w(TAG, "Exact alarm permission not granted, falling back to inexact window: ${e.message}")
            try {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    target.timeInMillis,
                    pendingIntent
                )
            } catch (fallbackEx: Exception) {
                Log.e(TAG, "Failed to schedule inexact fallback reminder", fallbackEx)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling daily reminder with AlarmManager", e)
        }
    }

    fun cancelDailyReminder(context: Context) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(context, DailyReminderReceiver::class.java).apply {
                action = DailyReminderReceiver.ACTION_FIRE_DAILY_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REMINDER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager?.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Daily reminder cancelled successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling daily reminder", e)
        }
    }

    fun cancelLegacyWorkManagerJobs(context: Context) {
        try {
            val wm = WorkManager.getInstance(context)
            wm.cancelUniqueWork("revision_daily_reminder")
            wm.cancelAllWorkByTag("revision_reminder")
            wm.cancelAllWorkByTag("daily_reminder")
            Log.d(TAG, "Cancelled any legacy WorkManager daily reminder jobs.")
        } catch (e: Exception) {
            Log.w(TAG, "Could not cancel legacy WorkManager jobs: ${e.message}")
        }
    }
}
