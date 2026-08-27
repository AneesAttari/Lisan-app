package com.example

import android.app.Application
import android.util.Log

class LisanApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        try {
            com.example.utils.NotificationHelper.createChannels(this)
        } catch (t: Throwable) {
            Log.e("LisanApplication", "Error creating notification channels", t)
        }

        try {
            com.example.utils.RevisionReminderScheduler.cancelLegacyWorkManagerJobs(this)
        } catch (t: Throwable) {
            Log.e("LisanApplication", "Error in legacy scheduler cleanup", t)
        }
    }

    companion object {
        lateinit var instance: LisanApplication
            private set
    }
}

