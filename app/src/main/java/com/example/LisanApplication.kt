package com.example

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache

class LisanApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        try {
            com.example.utils.NotificationHelper.createChannels(this)
        } catch (t: Throwable) {
            Log.e("LisanApplication", "Error creating notification channels", t)
        }

        try {
            com.example.utils.DailyReminderScheduler.cancelLegacyWorkManagerJobs(this)
        } catch (t: Throwable) {
            Log.e("LisanApplication", "Error in legacy scheduler cleanup", t)
        }

        try {
            com.example.utils.IconRotationManager.scheduleBackgroundRotation(this)
        } catch (t: Throwable) {
            Log.e("LisanApplication", "Error initializing icon rotation scheduler", t)
        }
    }

    override fun newImageLoader(): ImageLoader {
        val isLowRam = com.example.utils.DeviceCapabilityHelper.isLowRamDevice(this)
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    // Low RAM devices use 10% of heap; standard devices use 15%
                    .maxSizePercent(if (isLowRam) 0.10 else 0.15)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    // Bound disk cache to 50MB to prevent unbounded disk growth
                    .maxSizeBytes(50L * 1024 * 1024)
                    .build()
            }
            .bitmapConfig(if (isLowRam) Bitmap.Config.RGB_565 else Bitmap.Config.ARGB_8888)
            .allowHardware(true)
            .crossfade(true)
            .build()
    }

    companion object {
        lateinit var instance: LisanApplication
            private set
    }
}

