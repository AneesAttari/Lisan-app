package com.example.utils

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Base64
import android.util.Log
import com.example.data.remote.AudioConfig
import com.example.data.remote.CloudTtsClient
import com.example.data.remote.SynthesisInput
import com.example.data.remote.SynthesizeRequest
import com.example.data.remote.VoiceSelectionParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

data class ServerVoice(val id: String, val name: String, val languageCode: String)

class TutorTtsManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var mediaPlayer: MediaPlayer? = null
    private var nativeTts: TextToSpeech? = null
    private var isNativeTtsReady = false
    private var isInitializingNativeTts = false

    private val isLowRam = DeviceCapabilityHelper.isLowRamDevice(context)

    init {
        if (!isLowRam) {
            initNativeTtsInternal()
        }
    }

    private fun initNativeTtsInternal(onReady: (() -> Unit)? = null) {
        if (isNativeTtsReady || isInitializingNativeTts) {
            if (isNativeTtsReady) onReady?.invoke()
            return
        }
        isInitializingNativeTts = true
        try {
            nativeTts = TextToSpeech(context.applicationContext) { status ->
                isInitializingNativeTts = false
                if (status == TextToSpeech.SUCCESS) {
                    Log.d("TutorTtsManager", "Native TextToSpeech initialized successfully")
                    isNativeTtsReady = true
                    nativeTts?.language = Locale.US
                    checkUrduVoiceAvailability()
                    onReady?.invoke()
                } else {
                    Log.e("TutorTtsManager", "Native TextToSpeech init failed: $status")
                }
            }
        } catch (e: Exception) {
            isInitializingNativeTts = false
            Log.e("TutorTtsManager", "Error initializing native TextToSpeech", e)
        }
    }
    
    private val _isUrduVoiceAvailable = MutableStateFlow(true)
    val isUrduVoiceAvailable: StateFlow<Boolean> = _isUrduVoiceAvailable.asStateFlow()

    private fun checkUrduVoiceAvailability() {
        scope.launch {
            val preferredVoiceFound = nativeTts?.voices?.any { v ->
                val matchesUrdu = v.locale.language == "ur" || v.locale.country == "PK"
                matchesUrdu && isPreferredVoice(v.name, v.features)
            } ?: false
            _isUrduVoiceAvailable.value = preferredVoiceFound
        }
    }

    private val _currentlySpeaking = MutableStateFlow<String?>(null)
    val currentlySpeaking: StateFlow<String?> = _currentlySpeaking.asStateFlow()

    private val _ttsLoadingStatus = MutableStateFlow<String?>(null)
    val ttsLoadingStatus: StateFlow<String?> = _ttsLoadingStatus.asStateFlow()

    private var currentSpeakerPreset: String = "Young Voice"

    companion object {
        fun isFemaleVoiceId(voiceId: String): Boolean {
            val upper = voiceId.uppercase(Locale.getDefault())
            if (upper.contains("FEMALE") || upper.contains("WOMAN") || upper.contains("GIRL") ||
                upper.contains("ZIRA") || upper.contains("HAZEL") || upper.contains("SUSAN")) return true
            
            // Native Urdu/Arabic female voice identifiers
            if (upper.contains("-URF-") || upper.contains("-ARF-")) return true

            val lastSegment = upper.substringAfterLast("-")
            if (upper.startsWith("EN-")) {
                return listOf("A", "C", "E", "F", "G", "H", "O").contains(lastSegment)
            }
            if (upper.startsWith("AR-")) {
                return listOf("A", "D").contains(lastSegment)
            }
            if (upper.startsWith("UR-")) {
                return listOf("A").contains(lastSegment)
            }
            return false
        }

        fun isPreferredVoice(voiceName: String, features: Set<String>? = null): Boolean {
            val nameLower = voiceName.lowercase(Locale.getDefault())
            val upper = voiceName.uppercase(Locale.US)
            
            if (nameLower.contains("female") || nameLower.contains("woman") || nameLower.contains("girl") ||
                nameLower.contains("zira") || nameLower.contains("hazel") || nameLower.contains("susan")) {
                return false
            }

            if (upper.contains("-URF-") || upper.contains("-ARF-")) return false

            val lastSegment = upper.substringAfterLast("-")
            if (upper.startsWith("EN-") && listOf("A", "C", "E", "F", "G", "H", "O").contains(lastSegment)) return false
            if (upper.startsWith("AR-") && listOf("A", "D").contains(lastSegment)) return false
            if (upper.startsWith("UR-") && listOf("A").contains(lastSegment)) return false

            if (features != null) {
                if (features.contains("male")) return true
                if (features.contains("female")) return false
            }

            if (nameLower.contains("male") || nameLower.contains("guy") || nameLower.contains("boy") || nameLower.contains("man") ||
                upper.contains("-URM-") || upper.contains("-URD-") || upper.contains("-ARM-") || upper.contains("-ARD-")) {
                return true
            }

            if (upper.startsWith("EN-") && listOf("B", "D", "I", "J", "N", "R").contains(lastSegment)) return true
            if (upper.startsWith("AR-") && listOf("B", "C").contains(lastSegment)) return true
            if (upper.startsWith("UR-") && listOf("B").contains(lastSegment)) return true

            return true
        }
    }

    // Fixed server-side voices catalog (STRICTLY preferred voices only for Urdu, English, and Arabic)
    private val _availableVoices = MutableStateFlow<List<ServerVoice>>(listOf(
        ServerVoice("ur-PK-Wavenet-B", "Urdu Native Young (پاکستانی گويا - Wavenet)", "ur-PK"),
        ServerVoice("ur-PK-Standard-B", "Urdu Native Mature (پاکستانی گويا - Standard)", "ur-PK"),
        ServerVoice("en-US-Standard-B", "English Young (US - Standard B)", "en-US"),
        ServerVoice("en-US-Standard-I", "English Mature (US - Standard I)", "en-US"),
        ServerVoice("en-US-Standard-D", "English Gentle (US - Standard D)", "en-US"),
        ServerVoice("ar-SA-Standard-B", "Arabic Native Young (ar-SA)", "ar-SA"),
        ServerVoice("ar-XA-Wavenet-B", "Arabic Native Mature (ar-XA)", "ar-XA"),
        ServerVoice("ar-SA-Standard-C", "Arabic Native Gentle (ar-SA C)", "ar-SA")
    ))
    val availableVoices: StateFlow<List<ServerVoice>> = _availableVoices.asStateFlow()

    private val prefs = context.getSharedPreferences("tutor_tts_prefs", Context.MODE_PRIVATE)

    private val _selectedVoiceIdEn = MutableStateFlow<String?>(
        prefs.getString("selected_voice_en", "en-US-Standard-B")?.let {
            if (isFemaleVoiceId(it)) "en-US-Standard-B" else it
        } ?: "en-US-Standard-B"
    )
    val selectedVoiceIdEn: StateFlow<String?> = _selectedVoiceIdEn.asStateFlow()

    private val _selectedVoiceIdAr = MutableStateFlow<String?>(
        prefs.getString("selected_voice_ar", "ar-SA-Standard-B")?.let {
            if (isFemaleVoiceId(it)) "ar-SA-Standard-B" else it
        } ?: "ar-SA-Standard-B"
    )
    val selectedVoiceIdAr: StateFlow<String?> = _selectedVoiceIdAr.asStateFlow()

    private val _selectedVoiceIdUr = MutableStateFlow<String?>(
        prefs.getString("selected_voice_ur", "ur-PK-Wavenet-B")?.let {
            if (isFemaleVoiceId(it)) "ur-PK-Wavenet-B" else it
        } ?: "ur-PK-Wavenet-B"
    )
    val selectedVoiceIdUr: StateFlow<String?> = _selectedVoiceIdUr.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _pitch = MutableStateFlow(prefs.getFloat("voice_pitch", 1.0f))
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    private val _voiceVolume = MutableStateFlow(prefs.getFloat("voice_volume", 1.0f))
    val voiceVolume: StateFlow<Float> = _voiceVolume.asStateFlow()

    private var speechQueue = mutableListOf<SpeechAction>()
    private var isPlayingQueue = false
    private var lastSpokenText: String? = null

    sealed class SpeechAction
    data class SpeakChunk(
        val text: String, 
        val languageTag: String, 
        val pitchMod: Float,
        @Volatile var preparedFile: File? = null,
        @Volatile var isPreparing: Boolean = false
    ) : SpeechAction()
    data class PauseChunk(val durationMs: Long) : SpeechAction()

    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate
        setPlaybackSpeed(rate)
    }

    fun setPitch(p: Float) {
        _pitch.value = p
        prefs.edit().putFloat("voice_pitch", p).apply()
        try {
            nativeTts?.setPitch(p)
        } catch (e: Exception) {}
    }

    fun setVoiceVolume(vol: Float) {
        _voiceVolume.value = vol
        prefs.edit().putFloat("voice_volume", vol).apply()
        try {
            mediaPlayer?.setVolume(vol, vol)
        } catch (e: Exception) {}
    }

    fun applySpeakerPreset(speaker: String) {
        val mappedPreset = when {
            speaker.contains("Young", ignoreCase = true) -> "Young Voice"
            speaker.contains("Gentle", ignoreCase = true) -> "Gentle Voice"
            else -> "Mature Voice"
        }
        if (currentSpeakerPreset == mappedPreset) return
        
        val wasSpeaking = isPlayingQueue
        val textToResume = lastSpokenText
        val idToResume = _currentlySpeaking.value
        
        if (wasSpeaking) {
            stop()
        }
        
        currentSpeakerPreset = mappedPreset
        if (mappedPreset == "Young Voice") {
            setEnglishVoice("en-US-Standard-B")
            setArabicVoice("ar-SA-Standard-B")
            setUrduVoice("ur-PK-Wavenet-B")
        } else if (mappedPreset == "Gentle Voice") {
            setEnglishVoice("en-US-Standard-D")
            setArabicVoice("ar-SA-Standard-C")
            setUrduVoice("ur-PK-Standard-B")
        } else {
            setEnglishVoice("en-US-Standard-I")
            setArabicVoice("ar-XA-Wavenet-B")
            setUrduVoice("ur-PK-Standard-B")
        }
        
        if (wasSpeaking && !textToResume.isNullOrBlank()) {
            speak(textToResume, idToResume)
        }
    }

    fun replay() {
        lastSpokenText?.let { speak(it) }
    }

    fun setEnglishVoice(voiceId: String) {
        val finalId = if (voiceId.isBlank() || isFemaleVoiceId(voiceId)) "en-US-Standard-B" else voiceId
        _selectedVoiceIdEn.value = finalId
        prefs.edit().putString("selected_voice_en", finalId).apply()
    }

    fun setArabicVoice(voiceId: String) {
        val finalId = if (voiceId.isBlank() || isFemaleVoiceId(voiceId)) "ar-SA-Standard-B" else voiceId
        _selectedVoiceIdAr.value = finalId
        prefs.edit().putString("selected_voice_ar", finalId).apply()
    }

    fun setUrduVoice(voiceId: String) {
        val finalId = if (voiceId.isBlank() || isFemaleVoiceId(voiceId)) "ur-PK-Wavenet-B" else voiceId
        _selectedVoiceIdUr.value = finalId
        prefs.edit().putString("selected_voice_ur", finalId).apply()
    }

    private val _playbackSpeed = MutableStateFlow(prefs.getFloat("playback_speed", 1.0f))
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        currentRate = speed
        prefs.edit().putFloat("playback_speed", speed).apply()
        // Apply to current playback if possible
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // Just for safety
             try {
                 mediaPlayer?.let { 
                     if (it.isPlaying) {
                         val baseRateMultiplier = if (currentSpeakerPreset == "Young Voice") 1.05f else 1.0f
                         it.playbackParams = it.playbackParams.setSpeed(speed * baseRateMultiplier)
                     }
                 }
                 nativeTts?.setSpeechRate(speed)
             } catch (e: Exception) {}
        }
    }

    private var isPaused = false

    fun pause() {
        if (isPlayingQueue) {
            isPaused = true
            try {
                mediaPlayer?.pause()
                nativeTts?.stop() // Native TTS can't really pause easily, so we just stop and it won't resume perfectly
            } catch (e: Exception) {}
        }
    }

    fun resume() {
        if (isPlayingQueue && isPaused) {
            isPaused = false
            try {
                mediaPlayer?.start()
                // For Native TTS, we would need to resume from the chunk. 
                // Since native stop() clears the buffer, resume won't work easily for native chunks.
                // But Cloud TTS (mediaPlayer) will work.
            } catch (e: Exception) {}
        }
    }

    fun stop() {
        // Clean up any prepared files to prevent disk usage build up
        for (action in speechQueue) {
            if (action is SpeakChunk) {
                try {
                    action.preparedFile?.delete()
                } catch (_: Exception) {}
                action.preparedFile = null
            }
        }
        speechQueue.clear()
        isPlayingQueue = false
        isPaused = false
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e("TutorTtsManager", "Error stopping MediaPlayer", e)
        }
        mediaPlayer = null
        try {
            nativeTts?.stop()
        } catch (e: Exception) {
            Log.e("TutorTtsManager", "Error stopping Native TTS", e)
        }
        _currentlySpeaking.value = null
    }

    private var currentRate: Float = 1.0f

    private fun normalizeTextForTts(text: String): String {
        return TtsSanitizer.sanitizeForSpeech(text)
    }

    private fun preSynthesizeRemainingChunks() {
        if (isCloudTtsDisabled) return
        
        // Loop through all actions currently in the speechQueue and pre-synthesize SpeakChunks
        for (action in speechQueue) {
            if (action is SpeakChunk && action.preparedFile == null && !action.isPreparing) {
                val chunk = action
                chunk.isPreparing = true
                
                scope.launch(Dispatchers.IO) {
                    try {
                        val voiceId = when (chunk.languageTag) {
                            "ur" -> {
                                val selected = _selectedVoiceIdUr.value ?: "ur-PK-Wavenet-B"
                                if (isFemaleVoiceId(selected)) "ur-PK-Wavenet-B" else selected
                            }
                            "ar" -> {
                                val selected = _selectedVoiceIdAr.value ?: "ar-SA-Standard-B"
                                if (isFemaleVoiceId(selected)) "ar-SA-Standard-B" else selected
                            }
                            else -> {
                                val selected = _selectedVoiceIdEn.value ?: "en-US-Standard-B"
                                if (isFemaleVoiceId(selected)) "en-US-Standard-B" else selected
                            }
                        }
                        
                        if (voiceId != null) {
                            val langCode = when (chunk.languageTag) {
                                "ur" -> "ur-PK"
                                "ar" -> "ar-SA"
                                else -> "en-US"
                            }

                            val apiKey = CloudTtsClient.getApiKey()
                            if (apiKey.isNotEmpty()) {
                                val isUrduOrArabic = chunk.languageTag == "ur" || chunk.languageTag == "ar"
                                val baseCloudPitch = if (isUrduOrArabic) {
                                    0.5
                                } else if (currentSpeakerPreset == "Young Voice") {
                                    1.5
                                } else {
                                    -2.5
                                }
                                val cloudPitch = baseCloudPitch + ((chunk.pitchMod - 1.0) * 10.0)

                                val request = SynthesizeRequest(
                                    input = SynthesisInput(text = chunk.text),
                                    voice = VoiceSelectionParams(languageCode = langCode, name = voiceId),
                                    audioConfig = AudioConfig(
                                        audioEncoding = "MP3",
                                        speakingRate = currentRate.toDouble(),
                                        pitch = cloudPitch
                                    )
                                )

                                val response = CloudTtsClient.api.synthesizeText(apiKey, request)
                                val decodedBytes = Base64.decode(response.audioContent, Base64.DEFAULT)
                                val file = File.createTempFile("tts_chunk_", ".mp3", context.cacheDir)
                                FileOutputStream(file).use { it.write(decodedBytes) }
                                
                                chunk.preparedFile = file
                                Log.d("TutorTtsManager", "Pre-synthesized chunk successfully: ${chunk.text.take(20)}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.w("TutorTtsManager", "Pre-synthesis failed for chunk: ${chunk.text.take(20)}: ${e.message}")
                    } finally {
                        chunk.isPreparing = false
                    }
                }
            }
        }
    }

    fun speak(text: String, id: String? = null, speed: Float? = null) {
        Log.d("TutorTtsManager", "speak called: text='$text', id=$id, rate=$speed")
        val rate = speed ?: _playbackSpeed.value
        currentRate = rate
        stop()
        lastSpokenText = text
        _currentlySpeaking.value = id ?: text
        
        val actions = parseActions(text)
        speechQueue.addAll(actions)
        Log.d("TutorTtsManager", "speechQueue size: ${speechQueue.size}, actions: $actions")
        
        if (speechQueue.isNotEmpty()) {
            isPlayingQueue = true
            preSynthesizeRemainingChunks()
            processNextChunk()
        } else {
            Log.d("TutorTtsManager", "speechQueue is empty, nothing to speak")
            _currentlySpeaking.value = null
        }
    }

    private var isCloudTtsDisabled = false

    private fun hasLocalHighQualityVoice(languageTag: String): Boolean {
        if (!isNativeTtsReady || nativeTts == null) return false
        return try {
            nativeTts?.voices?.any { v ->
                val matchesLang = when (languageTag) {
                    "ur" -> v.locale.language == "ur" || v.locale.country == "PK"
                    "ar" -> v.locale.language == "ar"
                    else -> v.locale.language == "en"
                }
                matchesLang && isPreferredVoice(v.name, v.features)
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    private fun processNextChunk() {
        if (speechQueue.isEmpty() || !isPlayingQueue) {
            isPlayingQueue = false
            _currentlySpeaking.value = null
            _ttsLoadingStatus.value = null
            return
        }

        val action = speechQueue.removeAt(0)

        scope.launch {
            if (action is PauseChunk) {
                kotlinx.coroutines.delay(action.durationMs)
                processNextChunk()
                return@launch
            }
            
            val chunk = action as SpeakChunk

            try {
                // Pre-synthesis optimization check
                var tempFile = chunk.preparedFile
                
                // If it is preparing, wait a brief moment for it to complete
                var waitTime = 0
                while (chunk.isPreparing && chunk.preparedFile == null && waitTime < 20) {
                    kotlinx.coroutines.delay(100)
                    waitTime++
                }
                
                if (chunk.preparedFile != null) {
                    tempFile = chunk.preparedFile
                }

                if (tempFile != null) {
                    _ttsLoadingStatus.value = null
                    playAudioFile(tempFile, chunk)
                    return@launch
                }

                if (isCloudTtsDisabled) {
                    _ttsLoadingStatus.value = null
                    speakWithNativeTts(chunk)
                    return@launch
                }

                val voiceId = when (chunk.languageTag) {
                    "ur" -> {
                        val selected = _selectedVoiceIdUr.value ?: "ur-PK-Wavenet-B"
                        if (isFemaleVoiceId(selected)) "ur-PK-Wavenet-B" else selected
                    }
                    "ar" -> {
                        val selected = _selectedVoiceIdAr.value ?: "ar-SA-Standard-B"
                        if (isFemaleVoiceId(selected)) "ar-SA-Standard-B" else selected
                    }
                    else -> {
                        val selected = _selectedVoiceIdEn.value ?: "en-US-Standard-B"
                        if (isFemaleVoiceId(selected)) "en-US-Standard-B" else selected
                    }
                }
                
                if (voiceId == null) {
                    processNextChunk()
                    return@launch
                }

                // Device Voice Check & dynamic download state triggering
                val localVoiceAvailable = hasLocalHighQualityVoice(chunk.languageTag)
                if (!localVoiceAvailable) {
                    _ttsLoadingStatus.value = "Downloading native voice pack..."
                } else {
                    _ttsLoadingStatus.value = null
                }

                val langCode = when (chunk.languageTag) {
                    "ur" -> "ur-PK"
                    "ar" -> "ar-SA"
                    else -> "en-US"
                }

                val apiKey = CloudTtsClient.getApiKey()
                if (apiKey.isEmpty()) {
                    Log.w("TutorTtsManager", "No API key found for Cloud TTS, falling back to Native TTS")
                    isCloudTtsDisabled = true
                    _ttsLoadingStatus.value = null
                    speakWithNativeTts(chunk)
                    return@launch
                }

                // Adjust speech rate and pitch based on age variant
                val isUrduOrArabic = chunk.languageTag == "ur" || chunk.languageTag == "ar"
                val baseCloudPitch = if (isUrduOrArabic) {
                    0.5 // clear, loud, confident, and professional tone
                } else if (currentSpeakerPreset == "Young Voice") {
                    1.5
                } else {
                    -2.5
                }
                val cloudPitch = baseCloudPitch + ((chunk.pitchMod - 1.0) * 10.0)

                val request = SynthesizeRequest(
                    input = SynthesisInput(text = chunk.text),
                    voice = VoiceSelectionParams(languageCode = langCode, name = voiceId),
                    audioConfig = AudioConfig(
                        audioEncoding = "MP3",
                        speakingRate = currentRate.toDouble(),
                        pitch = cloudPitch
                    )
                )

                val response = withContext(Dispatchers.IO) {
                    CloudTtsClient.api.synthesizeText(apiKey, request)
                }

                val resultFile = withContext(Dispatchers.IO) {
                    val decodedBytes = Base64.decode(response.audioContent, Base64.DEFAULT)
                    val file = File.createTempFile("tts_chunk_", ".mp3", context.cacheDir)
                    FileOutputStream(file).use { it.write(decodedBytes) }
                    file
                }

                _ttsLoadingStatus.value = null
                playAudioFile(resultFile, chunk)

            } catch (e: retrofit2.HttpException) {
                _ttsLoadingStatus.value = null
                if (e.code() == 401 || e.code() == 403) {
                    Log.w("TutorTtsManager", "Cloud TTS unauthorized (HTTP ${e.code()}), switching to device Native TTS")
                    isCloudTtsDisabled = true
                } else {
                    Log.w("TutorTtsManager", "Cloud TTS HTTP error (${e.code()}), using Native TTS fallback")
                }
                speakWithNativeTts(chunk)
            } catch (e: Exception) {
                _ttsLoadingStatus.value = null
                Log.w("TutorTtsManager", "Cloud TTS error (${e.message}), using Native TTS fallback")
                speakWithNativeTts(chunk)
            }
        }
    }

    private fun playAudioFile(file: File, chunk: SpeakChunk) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                val vol = _voiceVolume.value
                setVolume(vol, vol)
                setOnPreparedListener {
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            val baseRateMultiplier = if (currentSpeakerPreset == "Young Voice") 1.05f else 1.0f
                            val speed = currentRate * baseRateMultiplier
                            it.playbackParams = it.playbackParams.setSpeed(speed)
                        }
                    } catch (e: Exception) {
                        Log.e("TutorTtsManager", "Error setting playback speed on MediaPlayer", e)
                    }
                    it.start()
                }
                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                    file.delete()
                    processNextChunk()
                }
                setOnErrorListener { it, what, extra ->
                    Log.e("TutorTtsManager", "MediaPlayer error: $what, $extra")
                    it.release()
                    mediaPlayer = null
                    file.delete()
                    speakWithNativeTts(chunk)
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("TutorTtsManager", "Error setting up MediaPlayer", e)
            file.delete()
            speakWithNativeTts(chunk)
        }
    }

    private fun speakWithNativeTts(chunk: SpeakChunk) {
        if (!isNativeTtsReady || nativeTts == null) {
            initNativeTtsInternal {
                speakWithNativeTts(chunk)
            }
            return
        }

        val targetLocale = when (chunk.languageTag) {
            "ur" -> Locale("ur", "PK")
            "ar" -> Locale("ar")
            else -> Locale.US
        }

        try {
            nativeTts?.let { tts ->
                val result = tts.setLanguage(targetLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w("TutorTtsManager", "Language $targetLocale not supported on Native TTS, trying default")
                }
                
                // Adjust speech rate and pitch based on age variant
                val isUrduOrArabic = chunk.languageTag == "ur" || chunk.languageTag == "ar"
                val baseRateMultiplier = if (isUrduOrArabic) {
                    1.05f // Energetic, clear, confident pacing
                } else if (currentSpeakerPreset == "Young Voice") {
                    1.05f
                } else {
                    1.0f
                }
                val adjustedSpeakingRate = currentRate * baseRateMultiplier
                val nativePitch = if (isUrduOrArabic) {
                    1.02f * chunk.pitchMod // Confident, bright pitch, never lazy or exhausted
                } else {
                    (if (currentSpeakerPreset == "Young Voice") 1.1f else 0.85f) * chunk.pitchMod
                }

                tts.setSpeechRate(adjustedSpeakingRate)
                tts.setPitch(nativePitch)

                // Strictly filter and apply native preferred voice for English, Arabic, and Urdu
                try {
                    val targetLang = chunk.languageTag
                    val preferredVoice = tts.voices?.find { v ->
                        val matchesLang = when (targetLang) {
                            "ur" -> v.locale.language == "ur" || v.locale.country == "PK"
                            "ar" -> v.locale.language == "ar"
                            else -> v.locale.language == "en"
                        }
                        matchesLang && isPreferredVoice(v.name, v.features)
                    }
                    if (preferredVoice != null) {
                        tts.voice = preferredVoice
                    } else {
                        Log.w("TutorTtsManager", "No suitable preferred voice found in Native TTS for ${chunk.languageTag}, using default device voice")
                    }
                } catch (e: Exception) {
                    Log.e("TutorTtsManager", "Could not set native preferred voice", e)
                }

                val params = android.os.Bundle()
                val utteranceId = "utt_${System.currentTimeMillis()}"
                
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        scope.launch { processNextChunk() }
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        scope.launch { processNextChunk() }
                    }
                })

                tts.speak(chunk.text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
            } ?: run {
                processNextChunk()
            }
        } catch (e: Exception) {
            Log.e("TutorTtsManager", "Error speaking with Native TTS", e)
            processNextChunk()
        }
    }

    private fun parseActions(text: String): List<SpeechAction> {
        val cleanText = text.replace(Regex("(\\*\\*|\\*|_|#|`|\\||---|===)"), "")
            .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1")
            
        val regex = Regex("\\[(pause_short|pause_long|pitch_high|pitch_normal|pitch_low)\\]")
        val parts = cleanText.split(regex)
        val matches = regex.findAll(cleanText).toList()
        
        val actions = mutableListOf<SpeechAction>()
        var currentPitchMod = 1.0f
        
        for (i in parts.indices) {
            val partText = normalizeTextForTts(parts[i])
            if (partText.isNotEmpty()) {
                val langChunks = splitTextByLanguage(partText, currentPitchMod)
                actions.addAll(langChunks)
            }
            if (i < matches.size) {
                when (matches[i].groupValues[1]) {
                    "pause_short" -> actions.add(PauseChunk(400L))
                    "pause_long" -> actions.add(PauseChunk(1000L))
                    "pitch_high" -> currentPitchMod = 1.25f
                    "pitch_low" -> currentPitchMod = 0.85f
                    "pitch_normal" -> currentPitchMod = 1.0f
                }
            }
        }
        
        val mergedActions = mutableListOf<SpeechAction>()
        for (action in actions) {
            if (action is SpeakChunk && mergedActions.isNotEmpty() && mergedActions.last() is SpeakChunk) {
                val last = mergedActions.last() as SpeakChunk
                if (last.languageTag == action.languageTag && last.pitchMod == action.pitchMod) {
                    mergedActions.removeAt(mergedActions.lastIndex)
                    mergedActions.add(SpeakChunk("${last.text} ${action.text}", last.languageTag, last.pitchMod))
                    continue
                }
            }
            mergedActions.add(action)
        }
        
        return mergedActions
    }

    internal fun segmentTextByScript(text: String): List<Pair<String, String>> {
        val segments = mutableListOf<Pair<String, String>>()
        if (text.isEmpty()) return segments
        
        var currentScript = "" // "latin", "arabic_script", or ""
        val currentBuffer = java.lang.StringBuilder()
        
        for (i in 0 until text.length) {
            val char = text[i]
            val isLatin = char in 'A'..'Z' || char in 'a'..'z'
            val isArabicScript = char in '\u0600'..'\u06FF' || 
                                 char in '\u0750'..'\u077F' || 
                                 char in '\u08A0'..'\u08FF' || 
                                 char in '\uFB50'..'\uFDFF' || 
                                 char in '\uFE70'..'\uFEFF'
                                 
            val charScript = when {
                isLatin -> "latin"
                isArabicScript -> "arabic_script"
                else -> "neutral"
            }
            
            if (charScript != "neutral") {
                if (currentScript == "") {
                    currentScript = charScript
                } else if (currentScript != charScript) {
                    val content = currentBuffer.toString()
                    if (content.trim().isNotEmpty()) {
                        segments.add(Pair(content, currentScript))
                    }
                    currentBuffer.setLength(0)
                    currentScript = charScript
                }
            }
            currentBuffer.append(char)
        }
        
        if (currentBuffer.isNotEmpty()) {
            val content = currentBuffer.toString()
            if (content.trim().isNotEmpty()) {
                segments.add(Pair(content, if (currentScript == "") "latin" else currentScript))
            }
        }
        
        return segments
    }

    internal fun detectArabicOrUrdu(text: String): String {
        var urduScore = 0
        var arabicScore = 0
        
        // Character-level heuristics
        for (char in text) {
            if (char in listOf('ٹ', 'ڈ', 'ڑ', 'ں', 'ے', 'پ', 'چ', 'ژ', 'گ', 'ھ', 'ۓ', '۔')) {
                urduScore += 15
            }
            if (char in listOf('ة', 'ى', 'ً', 'ٍ', 'ٌ', 'َ', 'ُ', 'ِ', 'ْ', 'ّ')) {
                arabicScore += 15
            }
        }
        
        // Word-level heuristics
        val words = text.split(Regex("\\s+"))
        val urduWords = setOf(
            "ہے", "ہیں", "کا", "کی", "کے", "کو", "میں", "سے", "پر", "نہیں", "تھا", "تھے", "تھی", 
            "یہ", "وہ", "آپ", "کر", "ہو", "اور", "اگر", "گیا", "گئی", "ہم", "تم", "ان", "اس", 
            "جو", "کہ", "یا", "نے", "ہوں", "کریں", "گے", "گی", "تھا", "تھے", "تھی", "ہوں", "ہیں۔",
            "کیسے", "کیوں", "کیا", "تفصیلی", "وضاحت", "گرامر", "مثال", "کتاب", "طلبہ", "طالب", "علم", "استاد"
        )
        val arabicWords = setOf(
            "من", "في", "على", "إلى", "عن", "مع", "هذا", "هذه", "ذلك", "تلك", "هو", "هي", "أنا", 
            "نحن", "أنت", "أنتم", "هم", "كان", "كانت", "التي", "الذي", "إن", "أن", "لا", "ما", 
            "لم", "لن", "قد", "كل", "بعض", "أو", "أم", "ثم", "بل", "لكن", "حتى", "سوف", "السلام", 
            "عليكم", "رحمة", "الله", "وبركاته", "مرحبا", "بكم", "دروس", "قواعد", "اللغة", "العربية", 
            "الإنجليزية", "الفعل", "الاسم", "الحرف", "جملة", "مبتدأ", "خبر", "فاعل", "مفعول", "طالب", "مستمر"
        )
        
        for (word in words) {
            val cleanWord = word.replace(Regex("[^\\u0600-\\u06FF]"), "")
            if (urduWords.contains(cleanWord)) {
                urduScore += 10
            }
            if (arabicWords.contains(cleanWord)) {
                arabicScore += 10
            }
        }
        
        return when {
            urduScore > arabicScore -> "ur"
            arabicScore > urduScore -> "ar"
            else -> {
                val activeLang = getSelectedVoiceLanguage()
                if (activeLang == "Arabic") "ar" else "ur"
            }
        }
    }

    internal fun splitTextByLanguage(text: String, pitchMod: Float): List<SpeakChunk> {
        val segments = segmentTextByScript(text)
        val initialChunks = mutableListOf<SpeakChunk>()
        
        for (segment in segments) {
            val trimmed = segment.first.trim()
            if (trimmed.isEmpty()) continue
            
            val langTag = when (segment.second) {
                "latin" -> "en"
                "arabic_script" -> detectArabicOrUrdu(trimmed)
                else -> "en"
            }
            initialChunks.add(SpeakChunk(trimmed, langTag, pitchMod))
        }

        val mergedChunks = mutableListOf<SpeakChunk>()
        for (chunk in initialChunks) {
            if (mergedChunks.isNotEmpty() && mergedChunks.last().languageTag == chunk.languageTag) {
                val last = mergedChunks.removeAt(mergedChunks.lastIndex)
                mergedChunks.add(SpeakChunk("${last.text} ${chunk.text}", last.languageTag, pitchMod))
            } else {
                mergedChunks.add(chunk)
            }
        }
        
        return mergedChunks
    }

    private fun getSelectedVoiceLanguage(): String {
        return try {
            val appPrefs = context.getSharedPreferences("lisan_app_settings_v2", Context.MODE_PRIVATE)
            val raw = appPrefs.getString("settings_json_v1", null)
            if (!raw.isNullOrBlank()) {
                if (raw.contains("\"selectedVoiceLanguage\":\"Urdu\"")) {
                    "Urdu"
                } else if (raw.contains("\"selectedVoiceLanguage\":\"Arabic\"")) {
                    "Arabic"
                } else {
                    "English"
                }
            } else {
                "English"
            }
        } catch (e: Exception) {
            "English"
        }
    }

    fun shutdown() {
        stop()
        try {
            nativeTts?.shutdown()
        } catch (e: Exception) {
            Log.e("TutorTtsManager", "Error shutting down Native TTS", e)
        }
        nativeTts = null
    }
}
