package com.example.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

/**
 * Manages the Lisan AI single permanent launcher icon identity and its 24-hour inactivity visual effect.
 *
 * Rule:
 * "Lisan AI keeps the same launcher icon, but if the user hasn't opened the app for 24 hours,
 * the icon gets a subtle visual effect; opening the app returns it to normal."
 */
object IconRotationManager {

    private const val TAG = "IconInactivityManager"
    private val mutex = Mutex()

    // 24 Hours in milliseconds for the inactivity threshold
    const val ROTATION_INTERVAL_MS = 24 * 60 * 60 * 1000L // 24 Hours
    const val INACTIVITY_THRESHOLD_MS = 24 * 60 * 60 * 1000L

    const val STATE_NORMAL = "Normal"
    const val STATE_INACTIVE = "Inactive"

    const val ALIAS_NORMAL = "com.example.MainActivityAliasNormal"
    const val ALIAS_INACTIVE = "com.example.MainActivityAliasInactive"

    const val WORK_NAME = "lisan_daily_icon_inactivity_work"

    /**
     * Called whenever MainActivity is opened by the user.
     * 1. Records current timestamp into lastAppOpenTimestamp.
     * 2. Immediately restores the base normal Lisan AI icon if it was in the subtle inactivity state.
     * 3. Begins a fresh 24-hour inactivity timer.
     * 4. Enqueues background check worker.
     */
    suspend fun onAppOpened(context: Context, settingsRepository: SettingsRepository) = mutex.withLock {
        val now = System.currentTimeMillis()
        Log.d(TAG, "onAppOpened: recording activity at timestamp $now")

        val currentSettings = settingsRepository.userSettingsFlow.first()

        // Always record current app open timestamp and reset icon state to normal
        settingsRepository.updateSettings {
            it.copy(
                lastAppOpenTimestamp = now,
                currentIconKey = STATE_NORMAL,
                lastIconChangeTimestamp = now
            )
        }

        // Restore normal base icon
        applyIconAlias(context, ALIAS_NORMAL)

        if (currentSettings.autoIconRotationEnabled) {
            scheduleBackgroundRotation(context)
        }
    }

    /**
     * Background check performed by WorkManager or BootReceiver.
     * If 24+ hours have elapsed without the user opening the app, applies the subtle visual variation
     * of the SAME Lisan AI icon. If already in inactive state, keeps it without secondary changes.
     */
    suspend fun checkAndRotateBackground(context: Context, settingsRepository: SettingsRepository): Boolean = mutex.withLock {
        val now = System.currentTimeMillis()
        val currentSettings = settingsRepository.userSettingsFlow.first()

        if (!currentSettings.autoIconRotationEnabled) {
            Log.d(TAG, "checkAndRotateBackground: auto inactivity effect disabled")
            return@withLock false
        }

        val lastOpen = currentSettings.lastAppOpenTimestamp
        val lastChange = currentSettings.lastIconChangeTimestamp
        val referenceTime = if (lastOpen > 0L) lastOpen else lastChange

        val elapsed = if (referenceTime > 0L) now - referenceTime else 0L

        if (elapsed >= INACTIVITY_THRESHOLD_MS) {
            if (currentSettings.currentIconKey != STATE_INACTIVE) {
                Log.d(TAG, "24h+ inactivity reached ($elapsed ms). Applying subtle visual variation to Lisan AI icon.")
                val success = applyIconAlias(context, ALIAS_INACTIVE)
                if (success) {
                    settingsRepository.updateSettings {
                        it.copy(
                            currentIconKey = STATE_INACTIVE,
                            lastIconChangeTimestamp = now
                        )
                    }
                }
                return@withLock success
            } else {
                Log.d(TAG, "Already in subtle inactive state. Preserving current Lisan AI identity.")
                return@withLock false
            }
        } else {
            Log.d(TAG, "Active within 24h. Remaining ms: ${INACTIVITY_THRESHOLD_MS - elapsed}")
            return@withLock false
        }
    }

    /**
     * Sets the icon state directly (e.g. from Settings preview/toggle).
     */
    suspend fun setManualIcon(context: Context, settingsRepository: SettingsRepository, iconKey: String): Boolean = mutex.withLock {
        val now = System.currentTimeMillis()
        val isInactive = iconKey.equals(STATE_INACTIVE, ignoreCase = true) || iconKey.equals("InactiveVariation", ignoreCase = true)
        val targetAlias = if (isInactive) ALIAS_INACTIVE else ALIAS_NORMAL
        val targetState = if (isInactive) STATE_INACTIVE else STATE_NORMAL

        val success = applyIconAlias(context, targetAlias)
        if (success) {
            settingsRepository.updateSettings {
                it.copy(
                    currentIconKey = targetState,
                    lastIconChangeTimestamp = now
                )
            }
            if (settingsRepository.userSettingsFlow.first().autoIconRotationEnabled) {
                scheduleBackgroundRotation(context)
            }
        }
        return@withLock success
    }

    /**
     * Toggles automatic 24-hour background inactivity effect.
     */
    suspend fun setAutoRotationEnabled(context: Context, settingsRepository: SettingsRepository, enabled: Boolean) = mutex.withLock {
        settingsRepository.updateSettings {
            it.copy(autoIconRotationEnabled = enabled)
        }
        if (enabled) {
            scheduleBackgroundRotation(context)
        } else {
            cancelBackgroundRotation(context)
            // If disabled, ensure base normal icon is active
            applyIconAlias(context, ALIAS_NORMAL)
            settingsRepository.updateSettings {
                it.copy(currentIconKey = STATE_NORMAL)
            }
        }
    }

    /**
     * DEBUG / TEST ONLY: Simulates the passage of time by shifting timestamps backwards by [simulatedHours] hours
     * in DataStore and triggering the inactivity check logic.
     */
    suspend fun simulateTimeElapsedForTesting(
        context: Context,
        settingsRepository: SettingsRepository,
        simulatedHours: Long
    ): Boolean = mutex.withLock {
        val shiftMs = simulatedHours * 60 * 60 * 1000L
        val now = System.currentTimeMillis()

        // Shift saved timestamps back by simulatedHours
        settingsRepository.updateSettings { current ->
            current.copy(
                lastAppOpenTimestamp = if (current.lastAppOpenTimestamp > 0) current.lastAppOpenTimestamp - shiftMs else now - shiftMs,
                lastIconChangeTimestamp = if (current.lastIconChangeTimestamp > 0) current.lastIconChangeTimestamp - shiftMs else now - shiftMs
            )
        }

        val currentSettings = settingsRepository.userSettingsFlow.first()
        if (!currentSettings.autoIconRotationEnabled) {
            Log.d(TAG, "[DEBUG SIMULATION] Auto inactivity effect disabled. Skipping.")
            return@withLock false
        }

        val lastOpen = currentSettings.lastAppOpenTimestamp
        val elapsed = now - lastOpen

        if (elapsed >= INACTIVITY_THRESHOLD_MS) {
            if (currentSettings.currentIconKey != STATE_INACTIVE) {
                Log.d(TAG, "[DEBUG SIMULATION] 24h inactivity threshold met ($elapsed ms / $simulatedHours h simulated). Activating subtle variation.")
                val success = applyIconAlias(context, ALIAS_INACTIVE)
                if (success) {
                    settingsRepository.updateSettings {
                        it.copy(
                            currentIconKey = STATE_INACTIVE,
                            lastIconChangeTimestamp = now
                        )
                    }
                }
                return@withLock success
            } else {
                Log.d(TAG, "[DEBUG SIMULATION] Already in subtle inactive state. Remaining in inactive state without rotating.")
                return@withLock false
            }
        } else {
            Log.d(TAG, "[DEBUG SIMULATION] Inactivity threshold NOT met ($elapsed ms < 24h). Keeping normal icon.")
            return@withLock false
        }
    }

    /**
     * Resolves the actual registered ComponentName for a launcher activity alias.
     * Supports namespace-qualified manifest names (e.g., com.example.MainActivityAliasNormal)
     * as well as applicationId-qualified names if configured.
     */
    fun getAliasComponentName(context: Context, aliasNameOrSimple: String): ComponentName {
        val packageName = context.packageName
        val simple = aliasNameOrSimple.substringAfterLast(".")
        val pm = context.packageManager

        val candidates = listOf(
            ComponentName(packageName, "com.example.$simple"),
            ComponentName(packageName, if (aliasNameOrSimple.contains(".")) aliasNameOrSimple else "$packageName.$simple"),
            ComponentName(packageName, "$packageName.$simple")
        ).distinct()

        for (candidate in candidates) {
            try {
                pm.getComponentEnabledSetting(candidate)
                return candidate
            } catch (_: Exception) {
                // Try next candidate
            }
        }
        return ComponentName(packageName, "com.example.$simple")
    }

    /**
     * Programmatically switches activity-alias component state at Android Launcher level.
     */
    fun applyIconAlias(context: Context, targetAliasClassName: String): Boolean {
        val pm = context.packageManager
        val targetSimple = targetAliasClassName.substringAfterLast(".")

        val allAliases = listOf(
            "MainActivityAliasNormal",
            "MainActivityAliasInactive"
        )

        return try {
            val targetName = if (targetSimple in allAliases) targetSimple else "MainActivityAliasNormal"
            val targetComponent = getAliasComponentName(context, targetName)

            pm.setComponentEnabledSetting(
                targetComponent,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            allAliases.forEach { alias ->
                if (alias != targetName) {
                    val comp = getAliasComponentName(context, alias)
                    pm.setComponentEnabledSetting(
                        comp,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
            }

            Log.d(TAG, "Launcher icon state set to: ${targetComponent.className}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error applying launcher icon alias: ${e.message}", e)
            false
        }
    }

    /**
     * Schedules WorkManager task to run background checks every 12 hours.
     */
    fun scheduleBackgroundRotation(context: Context) {
        try {
            val workManager = WorkManager.getInstance(context)

            val periodicWorkRequest = PeriodicWorkRequestBuilder<IconRotationWorker>(
                12, TimeUnit.HOURS
            )
                .setConstraints(Constraints.NONE)
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )

            Log.d(TAG, "WorkManager inactivity icon check task scheduled.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule WorkManager inactivity task", e)
        }
    }

    fun cancelBackgroundRotation(context: Context) {
        try {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(WORK_NAME)
            Log.d(TAG, "WorkManager inactivity icon check task cancelled.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel WorkManager inactivity task", e)
        }
    }
}
