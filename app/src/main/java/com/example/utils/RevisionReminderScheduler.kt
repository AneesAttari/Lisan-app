package com.example.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workers.RevisionReminderWorker
import java.util.concurrent.TimeUnit

object RevisionReminderScheduler {
    fun scheduleRevisionReminder(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<RevisionReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS) // Example initial delay
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "revision_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
