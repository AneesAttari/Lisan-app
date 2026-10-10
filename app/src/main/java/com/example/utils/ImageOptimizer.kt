package com.example.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageOptimizer {
    private const val TAG = "ImageOptimizer"

    enum class CompressionPreset(
        val displayNameEn: String,
        val displayNameAr: String,
        val maxDimension: Int,
        val quality: Int
    ) {
        HIGH("High Quality", "جودة عالية", 2560, 92),
        BALANCED("Balanced", "متوازن", 1600, 85),
        DATA_SAVER("Data Saver", "توفير البيانات", 1024, 75)
    }

    data class ImageMetadata(
        val originalWidth: Int,
        val originalHeight: Int,
        val originalSizeBytes: Long,
        val mimeType: String
    )

    data class OptimizationResult(
        val base64Data: String,
        val mimeType: String,
        val compressedWidth: Int,
        val compressedHeight: Int,
        val compressedSizeBytes: Int
    )

    /**
     * Extracts metadata (dimensions and size) from a Uri without loading the full image into memory.
     */
    fun getImageMetadata(context: Context, uri: Uri): ImageMetadata? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"

            // Get original size
            var originalSize: Long = 0
            contentResolver.openAssetFileDescriptor(uri, "r")?.use { fd ->
                originalSize = fd.length
            }

            // Fallback for file size if asset file descriptor length is not available
            if (originalSize <= 0) {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    originalSize = inputStream.available().toLong()
                }
            }

            // Get original dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            contentResolver.openInputStream(uri).use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            ImageMetadata(
                originalWidth = options.outWidth,
                originalHeight = options.outHeight,
                originalSizeBytes = originalSize,
                mimeType = mimeType
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read image metadata: ${e.message}", e)
            null
        }
    }

    /**
     * Resizes and compresses an image according to the specified preset.
     */
    fun optimizeImage(
        context: Context,
        uri: Uri,
        preset: CompressionPreset
    ): OptimizationResult? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"

            // Step 1: Decode bounds first to calculate sample size
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            contentResolver.openInputStream(uri).use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            val srcWidth = options.outWidth
            val srcHeight = options.outHeight
            if (srcWidth <= 0 || srcHeight <= 0) return null

            // Step 2: Determine appropriate downsampling (inSampleSize) to save initial memory
            val maxDim = preset.maxDimension
            var inSampleSize = 1
            if (srcWidth > maxDim || srcHeight > maxDim) {
                val halfWidth = srcWidth / 2
                val halfHeight = srcHeight / 2
                while ((halfWidth / inSampleSize) >= maxDim && (halfHeight / inSampleSize) >= maxDim) {
                    inSampleSize *= 2
                }
            }

            options.inJustDecodeBounds = false
            options.inSampleSize = inSampleSize
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            // Step 3: Decode bitmap with sample size
            val sampledBitmap = contentResolver.openInputStream(uri).use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            } ?: return null

            // Step 4: Handle EXIF orientation rotation
            val rotationDegrees = try {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val exif = ExifInterface(inputStream)
                    when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }
                } ?: 0
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse EXIF orientation: ${e.message}")
                0
            }

            val rotatedBitmap = if (rotationDegrees != 0) {
                val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                val rotated = Bitmap.createBitmap(sampledBitmap, 0, 0, sampledBitmap.width, sampledBitmap.height, matrix, true)
                if (rotated != sampledBitmap) {
                    sampledBitmap.recycle()
                }
                rotated
            } else {
                sampledBitmap
            }

            // Step 5: Perform exact resizing if the rotated bitmap is still larger than target dimension
            val finalBitmap = if (rotatedBitmap.width > maxDim || rotatedBitmap.height > maxDim) {
                val ratio = rotatedBitmap.width.toFloat() / rotatedBitmap.height.toFloat()
                val (targetWidth, targetHeight) = if (rotatedBitmap.width > rotatedBitmap.height) {
                    maxDim to (maxDim / ratio).toInt()
                } else {
                    (maxDim * ratio).toInt() to maxDim
                }
                val resized = Bitmap.createScaledBitmap(rotatedBitmap, targetWidth, targetHeight, true)
                if (resized != rotatedBitmap) {
                    rotatedBitmap.recycle()
                }
                resized
            } else {
                rotatedBitmap
            }

            // Step 6: Compress to JPEG byte array
            val outputStream = ByteArrayOutputStream()
            // Even if original is PNG, converting to JPEG is much more size-efficient for Gemini analysis
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, preset.quality, outputStream)
            val compressedBytes = outputStream.toByteArray()

            val base64Data = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)
            val finalWidth = finalBitmap.width
            val finalHeight = finalBitmap.height

            // Clean up bitmap resources
            finalBitmap.recycle()

            OptimizationResult(
                base64Data = base64Data,
                mimeType = "image/jpeg", // We've converted to JPEG
                compressedWidth = finalWidth,
                compressedHeight = finalHeight,
                compressedSizeBytes = compressedBytes.size
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to optimize image: ${e.message}", e)
            null
        }
    }

    /**
     * Format byte count into human-readable representation (KB, MB).
     */
    fun formatBytes(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(1024.0)).toInt()
        val pre = "KMGTPE"[exp - 1] + ""
        return String.format("%.1f %sB", bytes / Math.pow(1024.0, exp.toDouble()), pre)
    }

    /**
     * Persists generated/downloaded image bytes to internal persistent storage (`files/generated_images/`)
     * to avoid holding large Base64 strings in memory while preventing unexpected cache deletion.
     */
    fun saveGeneratedImageBytes(context: Context, bytes: ByteArray, mimeType: String = "image/png"): File? {
        return try {
            val extension = if (mimeType.contains("jpeg") || mimeType.contains("jpg")) "jpg" else "png"
            val dir = File(context.filesDir, "generated_images").apply { mkdirs() }
            val file = File(dir, "lisan_img_${System.currentTimeMillis()}_${(1000..9999).random()}.$extension")
            java.io.FileOutputStream(file).use { fos ->
                fos.write(bytes)
            }
            file
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save generated image to persistent files: ${e.message}", e)
            null
        }
    }

    /**
     * Safely saves a Base64 image string to persistent storage and returns its absolute file path.
     * If the input is already a file path or URL, returns it directly.
     */
    fun saveGeneratedImageBase64(context: Context, base64OrPath: String, mimeType: String = "image/png"): String {
        if (isLocalFilePathOrUri(base64OrPath)) {
            return base64OrPath
        }
        return try {
            val cleanBase64 = if (base64OrPath.contains(",")) {
                base64OrPath.substringAfter(",")
            } else {
                base64OrPath
            }
            val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            val file = saveGeneratedImageBytes(context, bytes, mimeType)
            file?.absolutePath ?: base64OrPath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decode and save base64 image: ${e.message}", e)
            base64OrPath
        }
    }

    /**
     * Determines whether the given string is a local file path, file URI, content URI, or web URL.
     */
    fun isLocalFilePathOrUri(source: String): Boolean {
        val s = source.trim()
        return s.startsWith("/") ||
                s.startsWith("file:") ||
                s.startsWith("content:") ||
                s.startsWith("http:") ||
                s.startsWith("https:") ||
                s.startsWith("android.resource:")
    }

    /**
     * Safely resolves image bytes from either a file path, content URI, or legacy Base64 string.
     */
    fun resolveImageBytes(context: Context, imageSource: String): ByteArray? {
        val s = imageSource.trim()
        return try {
            when {
                s.startsWith("content://") -> {
                    context.contentResolver.openInputStream(Uri.parse(s))?.use { it.readBytes() }
                }
                s.startsWith("file://") -> {
                    val path = s.removePrefix("file://")
                    File(path).readBytes()
                }
                s.startsWith("/") -> {
                    File(s).readBytes()
                }
                else -> {
                    val clean = if (s.contains(",")) s.substringAfter(",") else s
                    Base64.decode(clean, Base64.DEFAULT)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resolve image bytes: ${e.message}", e)
            null
        }
    }
}

