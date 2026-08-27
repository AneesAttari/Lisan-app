package com.example.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class PictureTtsManager(private val context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isInitializing = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val isLowRam = DeviceCapabilityHelper.isLowRamDevice(context)

    init {
        if (!isLowRam) {
            initTtsInternal()
        }
    }

    private fun initTtsInternal(onReady: (() -> Unit)? = null) {
        if (isInitialized || isInitializing) {
            if (isInitialized) onReady?.invoke()
            return
        }
        isInitializing = true
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                isInitializing = false
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale.US)
                    isInitialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
                    if (isInitialized) {
                        onReady?.invoke()
                    }
                } else {
                    Log.e("PictureTtsManager", "TTS initialization failed with status: $status")
                }
            }
        } catch (e: Exception) {
            isInitializing = false
            Log.e("PictureTtsManager", "Error initializing TTS", e)
        }
    }

    fun speak(text: String) {
        if (!isInitialized) {
            initTtsInternal {
                speak(text)
            }
            return
        }
        try {
            _isSpeaking.value = true
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "vocab_${System.currentTimeMillis()}")
            // Reset speaking state after a short delay
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                _isSpeaking.value = false
            }, 1200L)
        } catch (e: Exception) {
            Log.e("PictureTtsManager", "Error speaking text: $text", e)
            _isSpeaking.value = false
        }
    }

    fun stop() {
        try {
            tts?.stop()
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e("PictureTtsManager", "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e("PictureTtsManager", "Error shutting down TTS", e)
        }
    }
}
