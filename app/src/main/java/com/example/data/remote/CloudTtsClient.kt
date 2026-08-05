package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@Serializable
data class SynthesizeRequest(
    val input: SynthesisInput,
    val voice: VoiceSelectionParams,
    val audioConfig: AudioConfig
)

@Serializable
data class SynthesisInput(val text: String)

@Serializable
data class VoiceSelectionParams(val languageCode: String, val name: String)

@Serializable
data class AudioConfig(
    val audioEncoding: String = "MP3",
    val speakingRate: Double = 1.05,
    val pitch: Double = 0.0
)

@Serializable
data class SynthesizeResponse(val audioContent: String)

interface CloudTtsApi {
    @POST("v1/text:synthesize")
    suspend fun synthesizeText(
        @Query("key") apiKey: String,
        @Body request: SynthesizeRequest
    ): SynthesizeResponse
}

object CloudTtsClient {
    private const val BASE_URL = "https://texttospeech.googleapis.com/"
    private const val TAG = "CloudTtsClient"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val api: CloudTtsApi = retrofit.create(CloudTtsApi::class.java)

    fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }
}
