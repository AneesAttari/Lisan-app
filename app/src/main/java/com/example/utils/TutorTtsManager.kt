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

    init {
        try {
            nativeTts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    Log.d("TutorTtsManager", "Native TextToSpeech initialized successfully")
                    isNativeTtsReady = true
                    nativeTts?.language = Locale("ur", "PK")
                } else {
                    Log.e("TutorTtsManager", "Native TextToSpeech init failed: $status")
                }
            }
        } catch (e: Exception) {
            Log.e("TutorTtsManager", "Error initializing native TextToSpeech", e)
        }
    }
    
    private val _isReady = MutableStateFlow(true)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _currentlySpeaking = MutableStateFlow<String?>(null)
    val currentlySpeaking: StateFlow<String?> = _currentlySpeaking.asStateFlow()

    companion object {
        fun isFemaleVoiceId(voiceId: String): Boolean {
            val upper = voiceId.uppercase(Locale.getDefault())
            if (upper.contains("FEMALE") || upper.contains("WOMAN")) return true
            
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
    }

    // Fixed server-side voices catalog (STRICTLY Male voices only for Urdu, English, and Arabic)
    private val _availableVoices = MutableStateFlow<List<ServerVoice>>(listOf(
        ServerVoice("ur-PK-Wavenet-B", "Urdu Male Primary (پاکستانی گويا - Wavenet Natural)", "ur-PK"),
        ServerVoice("ur-PK-Standard-B", "Urdu Male Backup (پاکستانی گويا - Standard)", "ur-PK"),
        ServerVoice("en-US-Standard-D", "English Male Primary", "en-US"),
        ServerVoice("en-US-Standard-I", "English Male Backup", "en-US"),
        ServerVoice("en-US-Wavenet-D", "English Male Wavenet", "en-US"),
        ServerVoice("ar-XA-Standard-B", "Arabic Male Primary", "ar-XA"),
        ServerVoice("ar-XA-Wavenet-B", "Arabic Male Backup", "ar-XA")
    ))
    val availableVoices: StateFlow<List<ServerVoice>> = _availableVoices.asStateFlow()

    private val prefs = context.getSharedPreferences("tutor_tts_prefs", Context.MODE_PRIVATE)

    private val _selectedVoiceIdEn = MutableStateFlow<String?>(
        prefs.getString("selected_voice_en", "en-US-Standard-D")?.let {
            if (isFemaleVoiceId(it)) "en-US-Standard-D" else it
        } ?: "en-US-Standard-D"
    )
    val selectedVoiceIdEn: StateFlow<String?> = _selectedVoiceIdEn.asStateFlow()

    private val _selectedVoiceIdAr = MutableStateFlow<String?>(
        prefs.getString("selected_voice_ar", "ar-XA-Standard-B")?.let {
            if (isFemaleVoiceId(it)) "ar-XA-Standard-B" else it
        } ?: "ar-XA-Standard-B"
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

    private val _pitch = MutableStateFlow(1.0f)
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    private var speechQueue = mutableListOf<SpeechChunk>()
    private var isPlayingQueue = false

    data class SpeechChunk(val text: String, val languageTag: String)

    fun setSpeechRate(rate: Float) { _speechRate.value = rate }
    fun setPitch(p: Float) { _pitch.value = p }

    fun setEnglishVoice(voiceId: String) {
        val finalId = if (voiceId.isBlank() || isFemaleVoiceId(voiceId)) "en-US-Standard-D" else voiceId
        _selectedVoiceIdEn.value = finalId
        prefs.edit().putString("selected_voice_en", finalId).apply()
    }

    fun setArabicVoice(voiceId: String) {
        val finalId = if (voiceId.isBlank() || isFemaleVoiceId(voiceId)) "ar-XA-Standard-B" else voiceId
        _selectedVoiceIdAr.value = finalId
        prefs.edit().putString("selected_voice_ar", finalId).apply()
    }

    fun setUrduVoice(voiceId: String) {
        val finalId = if (voiceId.isBlank() || isFemaleVoiceId(voiceId)) "ur-PK-Wavenet-B" else voiceId
        _selectedVoiceIdUr.value = finalId
        prefs.edit().putString("selected_voice_ur", finalId).apply()
    }

    fun stop() {
        speechQueue.clear()
        isPlayingQueue = false
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

    fun speak(text: String, id: String? = null) {
        Log.d("TutorTtsManager", "speak called: $text, id: $id")
        stop()
        _currentlySpeaking.value = id ?: text
        
        val chunks = splitTextByLanguage(text)
        speechQueue.addAll(chunks)
        Log.d("TutorTtsManager", "speechQueue size: ${speechQueue.size}")
        
        if (speechQueue.isNotEmpty()) {
            isPlayingQueue = true
            processNextChunk()
        } else {
            _currentlySpeaking.value = null
        }
    }

    private fun processNextChunk() {
        if (speechQueue.isEmpty() || !isPlayingQueue) {
            isPlayingQueue = false
            _currentlySpeaking.value = null
            return
        }

        val chunk = speechQueue.removeAt(0)

        scope.launch {
            try {
                val voiceId = when (chunk.languageTag) {
                    "ur" -> {
                        val selected = _selectedVoiceIdUr.value ?: "ur-PK-Wavenet-B"
                        if (isFemaleVoiceId(selected)) "ur-PK-Wavenet-B" else selected
                    }
                    "ar" -> {
                        val selected = _selectedVoiceIdAr.value ?: "ar-XA-Standard-B"
                        if (isFemaleVoiceId(selected)) "ar-XA-Standard-B" else selected
                    }
                    else -> {
                        val selected = _selectedVoiceIdEn.value ?: "en-US-Standard-D"
                        if (isFemaleVoiceId(selected)) "en-US-Standard-D" else selected
                    }
                }

                val langCode = when (chunk.languageTag) {
                    "ur" -> "ur-PK"
                    "ar" -> "ar-XA"
                    else -> "en-US"
                }

                val apiKey = CloudTtsClient.getApiKey()
                if (apiKey.isEmpty()) {
                    Log.w("TutorTtsManager", "No API key found for Cloud TTS, falling back to Native TTS")
                    speakWithNativeTts(chunk)
                    return@launch
                }

                val request = SynthesizeRequest(
                    input = SynthesisInput(text = chunk.text),
                    voice = VoiceSelectionParams(languageCode = langCode, name = voiceId),
                    audioConfig = AudioConfig(audioEncoding = "MP3", speakingRate = 1.05, pitch = 0.0)
                )

                val response = withContext(Dispatchers.IO) {
                    CloudTtsClient.api.synthesizeText(apiKey, request)
                }

                val tempFile = withContext(Dispatchers.IO) {
                    val decodedBytes = Base64.decode(response.audioContent, Base64.DEFAULT)
                    val file = File.createTempFile("tts_chunk_", ".mp3", context.cacheDir)
                    FileOutputStream(file).use { it.write(decodedBytes) }
                    file
                }

                playAudioFile(tempFile, chunk)

            } catch (e: Exception) {
                Log.e("TutorTtsManager", "Error synthesizing text with Cloud TTS, using Native TTS fallback", e)
                speakWithNativeTts(chunk)
            }
        }
    }

    private fun playAudioFile(file: File, chunk: SpeechChunk) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnPreparedListener {
                    it.start()
                }
                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                    file.delete()
                    processNextChunk()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("TutorTtsManager", "MediaPlayer error: $what, $extra")
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

    private fun speakWithNativeTts(chunk: SpeechChunk) {
        if (!isNativeTtsReady || nativeTts == null) {
            Log.e("TutorTtsManager", "Native TTS is not ready yet")
            processNextChunk()
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
                
                // Human natural fluency tuning (prevent slow robotic speech)
                tts.setSpeechRate(1.05f)
                tts.setPitch(0.95f)

                // Strictly filter and apply native male voice for English, Arabic, and Urdu
                try {
                    val targetLang = chunk.languageTag
                    val maleVoice = tts.voices?.find { v ->
                        val matchesLang = when (targetLang) {
                            "ur" -> v.locale.language == "ur" || v.locale.country == "PK"
                            "ar" -> v.locale.language == "ar"
                            else -> v.locale.language == "en"
                        }
                        val voiceName = v.name.lowercase(Locale.getDefault())
                        val isFemale = voiceName.contains("female") ||
                                       voiceName.contains("woman") ||
                                       voiceName.contains("-a") ||
                                       voiceName.contains("-c") ||
                                       voiceName.contains("-e") ||
                                       voiceName.contains("-f") ||
                                       voiceName.contains("-g") ||
                                       voiceName.contains("-h") ||
                                       voiceName.contains("-o") ||
                                       voiceName.contains("ura") ||
                                       (v.features != null && v.features.contains("female"))
                        matchesLang && !isFemale
                    }
                    if (maleVoice != null) {
                        tts.voice = maleVoice
                    }
                } catch (e: Exception) {
                    Log.e("TutorTtsManager", "Could not set native male voice", e)
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

    private fun splitTextByLanguage(text: String): List<SpeechChunk> {
        val cleanText = text.replace(Regex("(\\*\\*|\\*|_|#|`|\\||---|===)"), "")
            .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1")
            
        val rawLines = cleanText.split(Regex("(?<=[.?!])\\s+|\n+"))
        val initialChunks = mutableListOf<SpeechChunk>()
        
        for (line in rawLines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue
            
            val hasUrduMarkers = trimmed.any { 
                it in listOf('ٹ', 'ڈ', 'ڑ', 'ں', 'ے', 'پ', 'چ', 'ژ', 'گ', 'ھ', 'آ', 'ئ', 'ۓ') 
            } || trimmed.contains(Regex("(ہے۔|ہیں|کا|کی|کے|کو|میں|سے|پر|نہیں|تھا|تھے|تھی|یہ|وہ|آپ|کر|ہو|اور|اگر|گیا|گئی|ہم|تم|ان|اس|جو|کہ|یا|تفصیلی|وضاحت|گرامر|مثال)"))

            val hasArabicSpecific = trimmed.any { 
                it in listOf('ة', 'ى', 'ً', 'ٍ', 'ٌ', 'َ', 'ُ', 'ِ', 'ْ', 'ّ') 
            } || trimmed.contains(Regex("(الجملة|الفعل|الاسم|الفاعل|المفعول|المبتدأ|الخبر|المضارع|الماضي|علامة|ضمير)"))

            val hasArabicScript = trimmed.any { it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' }
            
            val langTag = when {
                hasUrduMarkers -> "ur"
                hasArabicSpecific -> "ar"
                hasArabicScript -> "ur" // Default Arabic-script text to Urdu for Urdu native speakers
                else -> "en"
            }
            
            initialChunks.add(SpeechChunk(trimmed, langTag))
        }

        // Merge contiguous chunks of the same language to eliminate stuttering & audio gaps
        val mergedChunks = mutableListOf<SpeechChunk>()
        for (chunk in initialChunks) {
            if (mergedChunks.isNotEmpty() && mergedChunks.last().languageTag == chunk.languageTag) {
                val last = mergedChunks.removeAt(mergedChunks.lastIndex)
                mergedChunks.add(SpeechChunk("${last.text} ${chunk.text}", last.languageTag))
            } else {
                mergedChunks.add(chunk)
            }
        }
        
        return mergedChunks
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
