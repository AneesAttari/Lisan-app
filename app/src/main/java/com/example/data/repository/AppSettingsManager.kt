package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AppSettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("lisan_app_settings_v2", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

    private val _settings = MutableStateFlow<AppSettings>(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    companion object {
        private const val TAG = "AppSettingsManager"
        private const val KEY_SETTINGS_JSON = "settings_json_v1"
    }

    private fun loadSettings(): AppSettings {
        val raw = prefs.getString(KEY_SETTINGS_JSON, null)
        if (!raw.isNullOrBlank()) {
            try {
                return json.decodeFromString<AppSettings>(raw)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse saved AppSettings", e)
            }
        }
        return AppSettings()
    }

    fun updateSettings(update: (AppSettings) -> AppSettings) {
        val newSettings = update(_settings.value)
        _settings.value = newSettings
        saveToPrefs(newSettings)
    }

    private fun saveToPrefs(settings: AppSettings) {
        try {
            val raw = json.encodeToString(settings)
            prefs.edit().putString(KEY_SETTINGS_JSON, raw).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save AppSettings", e)
        }
    }

    fun resetToDefaults() {
        val defaultSettings = AppSettings()
        _settings.value = defaultSettings
        saveToPrefs(defaultSettings)
    }
}
