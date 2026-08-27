package com.example.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build

object DeviceCapabilityHelper {

    @Volatile
    private var isLowRamCached: Boolean? = null

    /**
     * Determines whether the current device should use low-RAM / performance-constrained optimizations.
     * Combines system `isLowRamDevice` flags, total physical RAM detection, and memory class.
     */
    fun isLowRamDevice(context: Context): Boolean {
        isLowRamCached?.let { return it }

        val isLow = try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            if (am != null) {
                if (am.isLowRamDevice) {
                    true
                } else {
                    val memInfo = ActivityManager.MemoryInfo()
                    am.getMemoryInfo(memInfo)
                    val totalMemMb = memInfo.totalMem / (1024 * 1024)
                    // If total device RAM <= 3072 MB (3GB) or memory class <= 192MB, treat as performance-constrained
                    val isMemoryConstrained = totalMemMb <= 3072
                    val isClassLow = am.memoryClass <= 192
                    isMemoryConstrained || isClassLow
                }
            } else {
                false
            }
        } catch (_: Throwable) {
            false
        }

        isLowRamCached = isLow
        return isLow
    }
}
