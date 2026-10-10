package com.example.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class IconRotationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("IconRotationWorker", "Executing scheduled 24h launcher icon rotation check...")
            val repository = SettingsRepository(appContext)
            
            val userSettings = repository.userSettingsFlow.first()
            if (!userSettings.autoIconRotationEnabled) {
                Log.d("IconRotationWorker", "Auto icon rotation disabled by user in Settings. Skipping.")
                return Result.success()
            }

            val rotated = IconRotationManager.checkAndRotateBackground(appContext, repository)
            Log.d("IconRotationWorker", "Icon rotation check finished. Rotated=$rotated")
            Result.success()
        } catch (e: Exception) {
            Log.e("IconRotationWorker", "Error executing IconRotationWorker", e)
            Result.retry()
        }
    }
}
