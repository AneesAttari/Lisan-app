package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Content
import com.example.data.model.GenerateContentRequest
import com.example.data.model.GenerationConfig
import com.example.data.model.Part
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class ExamQuestion(
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

@Serializable
data class ExamData(
    val questions: List<ExamQuestion>
)

class ExamViewModel(application: Application) : AndroidViewModel(application) {
    private val _isGeneratingExam = MutableStateFlow(false)
    val isGeneratingExam: StateFlow<Boolean> = _isGeneratingExam.asStateFlow()

    private val _examError = MutableStateFlow<String?>(null)
    val examError: StateFlow<String?> = _examError.asStateFlow()
    
    private val _examData = MutableStateFlow<ExamData?>(null)
    val examData: StateFlow<ExamData?> = _examData.asStateFlow()

    fun startExam(topic: String) {
        viewModelScope.launch {
            _isGeneratingExam.value = true
            _examError.value = null
            try {
                val prefs = getApplication<Application>().getSharedPreferences("lisan_prefs", Context.MODE_PRIVATE)
                val apiKey = prefs.getString("user_gemini_api_key", null)?.takeIf { it.isNotBlank() }
                    ?: com.example.BuildConfig.GEMINI_API_KEY
                
                if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                    _examError.value = "Gemini API key is not set."
                    _isGeneratingExam.value = false
                    return@launch
                }

                val schema = buildJsonObject {
                    put("type", "OBJECT")
                    put("properties", buildJsonObject {
                        put("questions", buildJsonObject {
                            put("type", "ARRAY")
                            put("items", buildJsonObject {
                                put("type", "OBJECT")
                                put("properties", buildJsonObject {
                                    put("text", buildJsonObject { put("type", "STRING") })
                                    put("options", buildJsonObject { 
                                        put("type", "ARRAY")
                                        put("items", buildJsonObject { put("type", "STRING") })
                                    })
                                    put("correctAnswerIndex", buildJsonObject { put("type", "INTEGER") })
                                    put("explanation", buildJsonObject { put("type", "STRING") })
                                })
                                put("required", buildJsonArray {
                                    add("text")
                                    add("options")
                                    add("correctAnswerIndex")
                                    add("explanation")
                                })
                            })
                        })
                    })
                    put("required", buildJsonArray { add("questions") })
                }

                val config = GenerationConfig(
                    responseMimeType = "application/json",
                    responseSchema = schema
                )
                
                val prompt = "Generate a 5-question multiple choice exam about '$topic' in the context of Arabic grammar learning for English speakers. Ensure output strictly matches the requested JSON schema."
                val request = GenerateContentRequest(
                    contents = listOf(Content(role = "user", parts = listOf(Part(text = prompt)))),
                    generationConfig = config
                )

                val response = RetrofitClient.service.generateContent(
                    model = "gemini-3.5-flash",
                    apiKey = apiKey,
                    request = request
                )
                
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (responseText != null) {
                    val json = Json { ignoreUnknownKeys = true }
                    val parsedData = json.decodeFromString<ExamData>(responseText)
                    _examData.value = parsedData
                } else {
                    val errorMsg = response.error?.message ?: "Failed to generate exam."
                    _examError.value = errorMsg
                }
            } catch (e: Exception) {
                Log.e("ExamViewModel", "Error generating exam", e)
                _examError.value = e.message ?: "An unknown error occurred"
            } finally {
                _isGeneratingExam.value = false
            }
        }
    }
    
    fun dismissError() {
        _examError.value = null
    }
    
    fun clearExam() {
        _examData.value = null
    }
}
