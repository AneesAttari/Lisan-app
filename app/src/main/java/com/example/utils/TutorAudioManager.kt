package com.example.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TutorAudioManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    
    private val _currentlyPlayingId = MutableStateFlow<String?>(null)
    val currentlyPlayingId: StateFlow<String?> = _currentlyPlayingId

    private val _isLoading = MutableStateFlow<String?>(null)
    val isLoading: StateFlow<String?> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun playIdiomAudio(idiomId: String) {
        // Stop any previous playback and clear states
        stopPlayback()
        
        _isLoading.value = idiomId
        _error.value = null

        // Simulate server-side audio URL retrieval
        val audioUrl = "https://storage.googleapis.com/lisan-grammar-assets/audio/en_male_$idiomId.mp3"

        try {
            val player = MediaPlayer()
            mediaPlayer = player
            player.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, Uri.parse(audioUrl))
                setOnPreparedListener {
                    if (_isLoading.value == idiomId) {
                        _isLoading.value = null
                        _currentlyPlayingId.value = idiomId
                        start()
                    }
                }
                setOnCompletionListener {
                    _currentlyPlayingId.value = null
                    release()
                    mediaPlayer = null
                }
                setOnErrorListener { _, _, _ ->
                    _isLoading.value = null
                    _currentlyPlayingId.value = null
                    _error.value = "Audio is unavailable. Please try again."
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _isLoading.value = null
            _error.value = "Audio is unavailable. Please try again."
        }
    }

    fun stopPlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        _currentlyPlayingId.value = null
        _isLoading.value = null
    }

    fun release() {
        stopPlayback()
    }
}
