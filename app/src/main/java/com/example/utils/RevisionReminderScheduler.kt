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
object RevisionReminderScheduler {

    private const val TAG = "RevisionReminderScheduler"
    private const val REMINDER_REQUEST_CODE = 4001

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        // 1. Cancel any legacy WorkManager background tasks to prevent erratic multi-notifications
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
            Log.d(TAG, "Daily reminder scheduled precisely for: ${target.time} (Hour: $hour, Min: $minute)")
        } catch (se: SecurityException) {
            Log.w(TAG, "Exact alarm permission restricted, falling back to setAndAllowWhileIdle", se)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        target.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        target.timeInMillis,
                        pendingIntent
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to schedule fallback daily reminder alarm", e)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule daily reminder alarm", e)
        }
    }

    fun cancelDailyReminder(context: Context) {
        cancelLegacyWorkManagerJobs(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, DailyReminderReceiver::class.java).apply {
            action = DailyReminderReceiver.ACTION_FIRE_DAILY_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Cancelled daily reminder alarm")
        }
    }

    fun cancelLegacyWorkManagerJobs(context: Context) {
        try {
            val wm = WorkManager.getInstance(context)
            wm.cancelUniqueWork("lisan_daily_reminder")
            wm.cancelUniqueWork("lisan_daily_challenge_reminder")
        } catch (t: Throwable) {
            Log.e(TAG, "Error cancelling legacy WorkManager jobs", t)
        }
    }

    // Keep legacy helper signatures for backward compatibility if called elsewhere
    fun scheduleDailyChallengeReminder(context: Context) {
        // Daily challenge is now aligned with the user's fixed daily reminder
    }

    fun cancelDailyChallengeReminder(context: Context) {
        // Handled via cancelLegacyWorkManagerJobs
    }

    fun scheduleRevisionReminder(context: Context) {
        // No-op to avoid hardcoded overrides of user's custom fixed time
    }
}
