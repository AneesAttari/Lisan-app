package com.example.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val tools: List<JsonObject>? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val role: String? = null,
    val parts: List<Part>,
    @kotlinx.serialization.Transient var debugMetadata: TutorDebugMetadata? = null,
    @kotlinx.serialization.Transient val imageUri: String? = null,
    @kotlinx.serialization.Transient var generatedPdfUri: String? = null,
    @kotlinx.serialization.Transient var generatedPdfPath: String? = null,
    @kotlinx.serialization.Transient var generatedPdfTitle: String? = null,
    @kotlinx.serialization.Transient var generatedPdfSubtitle: String? = null,
    @kotlinx.serialization.Transient var generatedPdfPageCount: Int = 0,
    @kotlinx.serialization.Transient var generatedPdfSizeBytes: Long = 0L,
    @kotlinx.serialization.Transient var generatedImageBase64: String? = null,
    @kotlinx.serialization.Transient var generatedImageMimeType: String? = "image/png",
    @kotlinx.serialization.Transient var generatedImagePrompt: String? = null
)

@Serializable
data class TutorDebugMetadata(
    val requestId: String,
    val submittedMessage: String,
    val provider: String,
    val modelName: String,
    val endpointCalled: String,
    val httpStatus: String,
    val source: String,
    val responseId: String?,
    val errorMessage: String? = null
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String
)

@Serializable
data class ImageConfig(
    val aspectRatio: String? = "1:1",
    val imageSize: String? = "1K"
)

@Serializable
data class GenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val thinkingConfig: ThinkingConfig? = null,
    val responseModalities: List<String>? = null,
    val responseMimeType: String? = null,
    val responseSchema: JsonObject? = null,
    val imageConfig: ImageConfig? = null
)

@Serializable
data class ThinkingConfig(
    val thinkingLevel: String
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null,
    val error: GeminiError? = null
)

@Serializable
data class Candidate(
    val content: Content? = null,
    val groundingMetadata: GroundingMetadata? = null
)

@Serializable
data class GroundingMetadata(
    val webSearchQueries: List<String>? = null
)

@Serializable
data class GeminiError(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)

data class TutorResponseResult(
    val responseText: String,
    val debugMetadata: TutorDebugMetadata
)
