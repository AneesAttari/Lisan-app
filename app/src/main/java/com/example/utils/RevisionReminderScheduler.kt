package com.example.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workers.RevisionReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

object RevisionReminderScheduler {

    const val WORK_NAME_DAILY_REMINDER = "lisan_daily_reminder"
    const val WORK_NAME_DAILY_CHALLENGE = "lisan_daily_challenge_reminder"

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val initialDelay = target.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<RevisionReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME_DAILY_REMINDER,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_DAILY_REMINDER)
    }

    fun scheduleDailyChallengeReminder(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<RevisionReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(12, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME_DAILY_CHALLENGE,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelDailyChallengeReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_DAILY_CHALLENGE)
    }

    fun scheduleRevisionReminder(context: Context) {
        scheduleDailyReminder(context, 9, 0)
    }
}
