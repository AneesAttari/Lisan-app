package com.example.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class IconBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            Log.d("IconBootReceiver", "Boot or package update event received: ${intent.action}")

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val repository = SettingsRepository(context)
                    val settings = repository.userSettingsFlow.first()
                    
                    if (settings.autoIconRotationEnabled) {
                        IconRotationManager.scheduleBackgroundRotation(context)
                        IconRotationManager.checkAndRotateBackground(context, repository)
                    } else {
                        // Maintain the normal base icon alias
                        IconRotationManager.applyIconAlias(context, IconRotationManager.ALIAS_NORMAL)
                    }
                } catch (e: Exception) {
                    Log.e("IconBootReceiver", "Error handling boot/update icon restoration", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
