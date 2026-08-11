package com.example.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageOptimizer {
    private const val TAG = "ImageOptimizer"

    enum class CompressionPreset(
        val displayNameEn: String,
        val displayNameAr: String,
        val maxDimension: Int,
        val quality: Int
    ) {
        HIGH("High Quality", "جودة عالية", 1280, 85),
        BALANCED("Balanced", "متوازن", 800, 75),
        DATA_SAVER("Data Saver", "توفير البيانات", 512, 60)
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

            // Step 4: Perform exact resizing if the sampled bitmap is still larger than target dimension
            val finalBitmap = if (sampledBitmap.width > maxDim || sampledBitmap.height > maxDim) {
                val ratio = sampledBitmap.width.toFloat() / sampledBitmap.height.toFloat()
                val (targetWidth, targetHeight) = if (sampledBitmap.width > sampledBitmap.height) {
                    maxDim to (maxDim / ratio).toInt()
                } else {
                    (maxDim * ratio).toInt() to maxDim
                }
                val resized = Bitmap.createScaledBitmap(sampledBitmap, targetWidth, targetHeight, true)
                if (resized != sampledBitmap) {
                    sampledBitmap.recycle()
                }
                resized
            } else {
                sampledBitmap
            }

            // Step 5: Compress to JPEG byte array
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
}
