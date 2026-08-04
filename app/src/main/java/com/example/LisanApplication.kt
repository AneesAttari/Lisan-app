package com.example

import android.app.Application

class LisanApplication : Application() {
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: LisanApplication
            private set
    }
}
