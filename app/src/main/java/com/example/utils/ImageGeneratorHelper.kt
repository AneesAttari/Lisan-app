package com.example.utils

import android.content.Context
import android.graphics.*
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageGeneratorHelper {

    enum class ImageStyle(val label: String, val promptPrefix: String) {
        REALISTIC("Realistic", "Hyper-realistic professional photography, photorealistic 8k resolution, realistic natural lighting, ultra-detailed realistic textures, cinematic depth of field, crisp focus"),
        CINEMATIC("Cinematic", "Cinematic movie screenshot, shot on 35mm lens, volumetric lighting, dramatic atmosphere, realistic color grading, epic composition"),
        ILLUSTRATION("Illustration", "High-resolution professional educational illustration, clean art style, vibrant colors, clear visual structure, modern graphic design"),
        ARTISTIC("Artistic", "Masterpiece digital artwork, rich expressive color palette, fine artistic details, elegant lighting"),
        THREE_D("3D Render", "High-detail 3D Octane render, realistic materials and reflections, soft studio lighting, clean 3D digital scene")
    }

    enum class ImageAspectRatio(val label: String, val ratioStr: String, val width: Int, val height: Int) {
        SQUARE_1_1("1:1", "1:1", 1024, 1024),
        LANDSCAPE_16_9("16:9", "16:9", 1280, 720),
        PORTRAIT_9_16("9:16", "9:16", 720, 1280),
        STANDARD_4_3("4:3", "4:3", 1024, 768)
    }

    data class GeneratedImageResult(
        val base64Data: String,
        val mimeType: String = "image/png",
        val imageFile: File? = null,
        val width: Int = 1024,
        val height: Int = 1024
    )

    fun enhancePrompt(
        prompt: String,
        style: ImageStyle = ImageStyle.REALISTIC
    ): String {
        val trimmed = prompt.trim()
        val prefix = style.promptPrefix
        return "$prefix, $trimmed. High resolution, stunning visual clarity, professional quality."
    }

    suspend fun generateImageForPrompt(
        context: Context,
        prompt: String,
        additionalInfo: String? = null,
        style: ImageStyle = ImageStyle.REALISTIC,
        aspectRatio: ImageAspectRatio = ImageAspectRatio.SQUARE_1_1
    ): GeneratedImageResult = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val width = aspectRatio.width
        val height = aspectRatio.height
        val enhancedPrompt = enhancePrompt(prompt, style)

        try {
            val encodedPrompt = java.net.URLEncoder.encode(enhancedPrompt, "UTF-8")
            val seed = (1..10000000).random()
            val imageUrl = "https://image.pollinations.ai/prompt/$encodedPrompt?width=$width&height=$height&nologo=true&model=flux&seed=$seed"
            
            val client = okhttp3.OkHttpClient.Builder()
                .connectTimeout(25, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(25, java.util.concurrent.TimeUnit.SECONDS)
                .build()
                
            val request = okhttp3.Request.Builder()
                .url(imageUrl)
                .get()
                .build()
                
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val imageBytes = response.body?.bytes()
                    if (imageBytes != null && imageBytes.isNotEmpty()) {
                        val base64Str = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                        
                        var cacheFile: File? = null
                        try {
                            cacheFile = File(context.cacheDir, "lisan_generated_img_${System.currentTimeMillis()}.png")
                            FileOutputStream(cacheFile).use { fos ->
                                fos.write(imageBytes)
                            }
                        } catch (e: Exception) {
                            Log.e("ImageGeneratorHelper", "Error saving generated image to cache: ${e.message}")
                        }
                        
                        return@withContext GeneratedImageResult(
                            base64Data = base64Str,
                            mimeType = "image/png",
                            imageFile = cacheFile,
                            width = width,
                            height = height
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ImageGeneratorHelper", "Real image generation failed, falling back to local visual canvas synthesizer: ${e.message}", e)
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background Gradient (Editorial Deep Violet to Indigo)
        val bgGradient = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            intArrayOf(
                Color.parseColor("#1E1B4B"), // Deep Indigo
                Color.parseColor("#312E81"), // Rich Purple
                Color.parseColor("#4C1D95")  // Dark Violet
            ),
            floatArrayOf(0.0f, 0.5f, 1.0f),
            Shader.TileMode.CLAMP
        )
        val bgPaint = Paint().apply {
            shader = bgGradient
            isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Decorative Background Graphic Shapes
        val glowPaint = Paint().apply {
            color = Color.parseColor("#8B5CF6")
            alpha = 35
            isAntiAlias = true
            maskFilter = BlurMaskFilter(120f, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawCircle(200f, 200f, 300f, glowPaint)
        canvas.drawCircle((width - 200).toFloat(), (height - 200).toFloat(), 350f, glowPaint)

        val accentCirclePaint = Paint().apply {
            color = Color.parseColor("#A855F7")
            alpha = 20
            setStyle(Paint.Style.STROKE)
            strokeWidth = 3f
            isAntiAlias = true
        }
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width * 0.4f), accentCirclePaint)

        // 3. Top Header Badge
        val badgeRect = RectF(48f, 48f, 480f, 108f)
        val badgeBgPaint = Paint().apply {
            color = Color.parseColor("#10B981") // Emerald accent
            isAntiAlias = true
        }
        canvas.drawRoundRect(badgeRect, 30f, 30f, badgeBgPaint)

        val badgeTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("LISAN AI • VISUAL GRAPHIC", 72f, 86f, badgeTextPaint)

        // 4. Central Glassmorphism Card
        val cardRect = RectF(48f, 130f, width - 48f, height - 90f)
        val cardBgPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            alpha = 220
            isAntiAlias = true
        }
        canvas.drawRoundRect(cardRect, 30f, 30f, cardBgPaint)

        val cardBorderPaint = Paint().apply {
            color = Color.parseColor("#6366F1")
            setStyle(Paint.Style.STROKE)
            strokeWidth = 3f
            isAntiAlias = true
        }
        canvas.drawRoundRect(cardRect, 30f, 30f, cardBorderPaint)

        // 5. Prompt Title Text
        val titleTextPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val promptTitle = if (prompt.length > 80) prompt.take(80) + "..." else prompt
        val titleLines = wrapText(promptTitle, titleTextPaint, width - 180)

        var currentY = 200f
        val linePaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        canvas.drawText("🎨", 80f, currentY + 10f, titleTextPaint)

        for (line in titleLines) {
            canvas.drawText(line, 140f, currentY + 10f, linePaint)
            currentY += 42f
        }

        currentY += 20f

        // 6. Divider line
        val dividerPaint = Paint().apply {
            color = Color.parseColor("#334155")
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawLine(80f, currentY, width - 80f, currentY, dividerPaint)
        currentY += 30f

        // 7. Visual Graphic Diagram / Callout Box
        val innerBoxRect = RectF(80f, currentY, width - 80f, height - 130f)
        val innerBoxBg = Paint().apply {
            color = Color.parseColor("#1E293B")
            isAntiAlias = true
        }
        canvas.drawRoundRect(innerBoxRect, 20f, 20f, innerBoxBg)

        val innerBoxBorder = Paint().apply {
            color = Color.parseColor("#A855F7")
            setStyle(Paint.Style.STROKE)
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawRoundRect(innerBoxRect, 20f, 20f, innerBoxBorder)

        val itemPaint = Paint().apply {
            color = Color.parseColor("#F1F5F9")
            textSize = 26f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val detailText = additionalInfo ?: "English Grammar & Comparative Urdu Illustration\n(آپ کا مانگا گیا تصویری خاکہ)"
        val detailLines = wrapText(detailText, itemPaint, width - 200)

        var boxY = currentY + 45f
        for (line in detailLines.take(5)) {
            canvas.drawText(line, 110f, boxY, itemPaint)
            boxY += 38f
        }

        // 8. Bottom Footer Banner
        val footerY = height - 50f
        val footerPaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            textSize = 18f
            isAntiAlias = true
        }
        canvas.drawText("Generated by Lisan AI • Language Learning Visual Engine", 60f, footerY, footerPaint)

        // 9. Encode to PNG ByteArray & Base64
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val imageBytes = outputStream.toByteArray()
        val base64Str = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

        var cacheFile: File? = null
        try {
            cacheFile = File(context.cacheDir, "lisan_generated_img_${System.currentTimeMillis()}.png")
            FileOutputStream(cacheFile).use { fos ->
                fos.write(imageBytes)
            }
        } catch (e: Exception) {
            Log.e("ImageGeneratorHelper", "Error saving generated image to cache: ${e.message}")
        }

        return@withContext GeneratedImageResult(
            base64Data = base64Str,
            mimeType = "image/png",
            imageFile = cacheFile,
            width = width,
            height = height
        )
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Int): List<String> {
        val lines = mutableListOf<String>()
        val words = text.split(" ")
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val width = paint.measureText(testLine)
            if (width > maxWidth) {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine)
                }
                currentLine = word
            } else {
                currentLine = testLine
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }
        return lines
    }
}
