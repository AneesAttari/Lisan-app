package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import androidx.core.content.FileProvider
import com.example.data.model.Content
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * High-craft PDF Generation Engine for Lisan AI.
 * Creates formatted, multi-page study guides, chat transcripts, vocabulary sheets, and worksheets.
 */
object PdfCreatorHelper {
    private const val TAG = "PdfCreatorHelper"

    // Standard A4 dimensions in PostScript points (72 points per inch)
    const val PAGE_WIDTH = 595
    const val PAGE_HEIGHT = 842
    const val MARGIN_HORIZONTAL = 40f
    const val MARGIN_TOP = 45f
    const val MARGIN_BOTTOM = 45f
    val CONTENT_WIDTH = PAGE_WIDTH - (MARGIN_HORIZONTAL * 2)

    enum class PdfColorTheme(
        val primaryColor: Int,
        val secondaryColor: Int,
        val accentColor: Int,
        val cardBgColor: Int,
        val cardBorderColor: Int,
        val headerBgColor: Int,
        val textColor: Int,
        val textMutedColor: Int
    ) {
        CLASSIC_EDITORIAL(
            primaryColor = Color.rgb(24, 49, 83),       // Deep Navy #183153
            secondaryColor = Color.rgb(41, 128, 185),  // Ocean Blue #2980B9
            accentColor = Color.rgb(22, 160, 133),     // Teal Accent #16A085
            cardBgColor = Color.rgb(248, 250, 252),    // Slate Tint #F8FAFC
            cardBorderColor = Color.rgb(226, 232, 240),// Slate 200
            headerBgColor = Color.rgb(238, 242, 246),  // Header BG
            textColor = Color.rgb(15, 23, 42),         // Slate 900
            textMutedColor = Color.rgb(100, 116, 139)  // Slate 500
        ),
        EMERALD_STUDY(
            primaryColor = Color.rgb(6, 95, 70),       // Deep Emerald #065F46
            secondaryColor = Color.rgb(5, 150, 105),   // Green #059669
            accentColor = Color.rgb(16, 185, 129),     // Light Emerald
            cardBgColor = Color.rgb(240, 253, 244),    // Mint Tint
            cardBorderColor = Color.rgb(187, 247, 208),// Green 200
            headerBgColor = Color.rgb(236, 253, 245),  // Emerald Header
            textColor = Color.rgb(6, 78, 59),          // Dark Forest
            textMutedColor = Color.rgb(82, 114, 101)   // Forest Muted
        ),
        ROYAL_PURPLE(
            primaryColor = Color.rgb(76, 29, 149),     // Deep Purple #4C1D95
            secondaryColor = Color.rgb(124, 58, 237),  // Purple #7C3AED
            accentColor = Color.rgb(168, 85, 247),     // Purple Accent
            cardBgColor = Color.rgb(250, 245, 255),    // Purple Tint
            cardBorderColor = Color.rgb(233, 213, 255),// Purple 200
            headerBgColor = Color.rgb(243, 232, 255),  // Purple Header
            textColor = Color.rgb(46, 16, 101),        // Dark Violet
            textMutedColor = Color.rgb(107, 90, 138)   // Purple Muted
        ),
        WARM_AMBER(
            primaryColor = Color.rgb(120, 53, 15),     // Deep Amber #78350F
            secondaryColor = Color.rgb(217, 119, 6),   // Amber #D97706
            accentColor = Color.rgb(245, 158, 11),     // Amber Light
            cardBgColor = Color.rgb(255, 251, 235),    // Amber Tint
            cardBorderColor = Color.rgb(253, 230, 138),// Amber 200
            headerBgColor = Color.rgb(254, 243, 199),  // Amber Header
            textColor = Color.rgb(69, 26, 3),          // Dark Warm Brown
            textMutedColor = Color.rgb(146, 110, 80)   // Warm Muted
        )
    }

    data class PdfMetadata(
        val title: String,
        val subtitle: String = "Lisan AI Study & Revision Guide",
        val learnerName: String = "Learner",
        val proficiencyLevel: String = "All Levels",
        val dateString: String = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
        val theme: PdfColorTheme = PdfColorTheme.CLASSIC_EDITORIAL,
        val includeUrduTranslations: Boolean = true,
        val includeGrammarBreakdown: Boolean = true,
        val includeTimestamps: Boolean = true
    )

    sealed class PdfBlock {
        data class SectionHeader(val title: String, val subtitle: String? = null) : PdfBlock()
        data class ChatBubble(
            val sender: String,
            val text: String,
            val isUser: Boolean,
            val timestamp: String? = null,
            val urduTranslation: String? = null,
            val grammarTags: List<String> = emptyList()
        ) : PdfBlock()
        data class Callout(
            val title: String,
            val text: String,
            val urduText: String? = null,
            val isAccent: Boolean = false
        ) : PdfBlock()
        data class VocabCard(
            val word: String,
            val partOfSpeech: String,
            val urduMeaning: String,
            val exampleEnglish: String,
            val exampleUrdu: String? = null
        ) : PdfBlock()
        data class ExerciseItem(
            val number: Int,
            val question: String,
            val options: List<String> = emptyList(),
            val urduHint: String? = null,
            val answerKey: String? = null
        ) : PdfBlock()
        data class KeyValueRow(val key: String, val value: String, val urduValue: String? = null) : PdfBlock()
        data class Table(val headers: List<String>, val rows: List<List<String>>) : PdfBlock()
        data class Paragraph(val text: String, val isMuted: Boolean = false) : PdfBlock()
        object Spacer : PdfBlock()
    }

    data class GeneratedPdfResult(
        val file: File,
        val uri: Uri,
        val pageCount: Int,
        val fileSizeBytes: Long
    )

    /**
     * Generates a PDF Document with the provided blocks and metadata.
     */
    fun createPdf(
        context: Context,
        metadata: PdfMetadata,
        blocks: List<PdfBlock>
    ): GeneratedPdfResult {
        val pdfDir = File(context.filesDir, "pdfs").apply { mkdirs() }
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val sanitizedTitle = metadata.title.replace(Regex("[^a-zA-Z0-9_]"), "_").take(30)
        val pdfFile = File(pdfDir, "Lisan_${sanitizedTitle}_$timestamp.pdf")

        val pdfDocument = try {
            PdfDocument()
        } catch (e: Throwable) {
            Log.e(TAG, "PdfDocument not supported in current environment: ${e.message}")
            if (!pdfFile.exists() || pdfFile.length() == 0L) {
                pdfFile.writeText("%PDF-1.4\n% Lisan AI Educational Generated PDF Document\n1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\ntrailer << /Root 1 0 R >>\n%%EOF\n")
            }
            return GeneratedPdfResult(
                file = pdfFile,
                uri = Uri.fromFile(pdfFile),
                pageCount = 1,
                fileSizeBytes = pdfFile.length()
            )
        }

        var finalPageCount = 1
        try {
            class PageState(
                var page: PdfDocument.Page,
                var canvas: Canvas,
                var currentY: Float,
                var currentPageNumber: Int
            )

            val initialPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val initialPage = pdfDocument.startPage(initialPageInfo)
            val state = PageState(
                page = initialPage,
                canvas = initialPage.canvas,
                currentY = MARGIN_TOP,
                currentPageNumber = 1
            )

            // Helper paints
            val textPaint = TextPaint().apply {
                isAntiAlias = true
                color = metadata.theme.textColor
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }

            val primaryPaint = Paint().apply {
                isAntiAlias = true
                color = metadata.theme.primaryColor
            }

            val secondaryPaint = Paint().apply {
                isAntiAlias = true
                color = metadata.theme.secondaryColor
            }

            val fillPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
            }

            val strokePaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }

            // Draw First Page Cover Header
            state.currentY = drawCoverHeader(state.canvas, metadata, state.currentY)

            fun createNewPage(): Canvas {
                // Draw footer on current page before finishing
                drawPageFooter(state.canvas, metadata, state.currentPageNumber)
                pdfDocument.finishPage(state.page)

                state.currentPageNumber++
                val newPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, state.currentPageNumber).create()
                state.page = pdfDocument.startPage(newPageInfo)
                state.canvas = state.page.canvas
                state.currentY = MARGIN_TOP

                // Draw compact running header on subsequent pages
                state.currentY = drawRunningHeader(state.canvas, metadata, state.currentY)
                return state.canvas
            }

            fun ensureSpace(requiredHeight: Float) {
                if (state.currentY + requiredHeight > PAGE_HEIGHT - MARGIN_BOTTOM - 20f) {
                    createNewPage()
                }
            }

            // Render each block
            for (block in blocks) {
                when (block) {
                    is PdfBlock.SectionHeader -> {
                        ensureSpace(50f)
                        state.currentY = drawSectionHeader(state.canvas, block, metadata, state.currentY)
                    }
                    is PdfBlock.ChatBubble -> {
                        val approxHeight = estimateChatBubbleHeight(block, textPaint, CONTENT_WIDTH)
                        ensureSpace(approxHeight)
                        state.currentY = drawChatBubble(state.canvas, block, metadata, textPaint, fillPaint, strokePaint, state.currentY)
                    }
                    is PdfBlock.Callout -> {
                        val approxHeight = estimateCalloutHeight(block, textPaint, CONTENT_WIDTH)
                        ensureSpace(approxHeight)
                        state.currentY = drawCallout(state.canvas, block, metadata, textPaint, fillPaint, strokePaint, state.currentY)
                    }
                    is PdfBlock.VocabCard -> {
                        ensureSpace(70f)
                        state.currentY = drawVocabCard(state.canvas, block, metadata, textPaint, fillPaint, strokePaint, state.currentY)
                    }
                    is PdfBlock.ExerciseItem -> {
                        ensureSpace(60f + (block.options.size * 14f))
                        state.currentY = drawExerciseItem(state.canvas, block, metadata, textPaint, fillPaint, strokePaint, state.currentY)
                    }
                    is PdfBlock.KeyValueRow -> {
                        ensureSpace(24f)
                        state.currentY = drawKeyValueRow(state.canvas, block, metadata, textPaint, state.currentY)
                    }
                    is PdfBlock.Table -> {
                        ensureSpace(30f + (block.rows.size * 22f))
                        state.currentY = drawTable(state.canvas, block, metadata, textPaint, fillPaint, strokePaint, state.currentY)
                    }
                    is PdfBlock.Paragraph -> {
                        val layout = createStaticLayout(block.text, textPaint, CONTENT_WIDTH.toInt())
                        ensureSpace(layout.height.toFloat() + 10f)
                        state.currentY = drawParagraph(state.canvas, layout, block.isMuted, metadata, state.currentY)
                    }
                    is PdfBlock.Spacer -> {
                        state.currentY += 12f
                    }
                }
                state.currentY += 8f
            }

            // Draw final page footer
            drawPageFooter(state.canvas, metadata, state.currentPageNumber)
            pdfDocument.finishPage(state.page)

            finalPageCount = state.currentPageNumber

            // Write to file
            FileOutputStream(pdfFile).use { out ->
                pdfDocument.writeTo(out)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "PdfDocument rendering fallback in current environment: ${e.message}")
            if (!pdfFile.exists() || pdfFile.length() == 0L) {
                pdfFile.writeText("%PDF-1.4\n% Lisan AI Educational Generated PDF Document\n1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\ntrailer << /Root 1 0 R >>\n%%EOF\n")
            }
        } finally {
            try {
                pdfDocument.close()
            } catch (_: Throwable) {}
        }

        val uri = try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error obtaining FileProvider URI", e)
            Uri.fromFile(pdfFile)
        }

        return GeneratedPdfResult(
            file = pdfFile,
            uri = uri,
            pageCount = finalPageCount,
            fileSizeBytes = pdfFile.length()
        )
    }

    // --- Header & Footer Drawing ---

    private fun drawCoverHeader(canvas: Canvas, metadata: PdfMetadata, startY: Float): Float {
        var y = startY

        // Background banner box
        val bgPaint = Paint().apply {
            isAntiAlias = true
            color = metadata.theme.headerBgColor
            style = Paint.Style.FILL
        }
        val borderPaint = Paint().apply {
            isAntiAlias = true
            color = metadata.theme.cardBorderColor
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }
        val headerRect = RectF(MARGIN_HORIZONTAL, y, PAGE_WIDTH - MARGIN_HORIZONTAL, y + 80f)
        canvas.drawRoundRect(headerRect, 8f, 8f, bgPaint)
        canvas.drawRoundRect(headerRect, 8f, 8f, borderPaint)

        // Brand Icon / Badge Box
        val badgePaint = Paint().apply {
            isAntiAlias = true
            color = metadata.theme.primaryColor
            style = Paint.Style.FILL
        }
        val badgeRect = RectF(MARGIN_HORIZONTAL + 12f, y + 14f, MARGIN_HORIZONTAL + 52f, y + 54f)
        canvas.drawRoundRect(badgeRect, 6f, 6f, badgePaint)

        // Badge Text (Lisan Arabic Calligraphy / Logo mark)
        val badgeTextPaint = TextPaint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("لسان", badgeRect.centerX(), badgeRect.centerY() + 5f, badgeTextPaint)

        // Brand & Title
        val brandPaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.secondaryColor
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("LISAN AI • COMPARATIVE ENGLISH & URDU STUDY", MARGIN_HORIZONTAL + 62f, y + 25f, brandPaint)

        val titlePaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.primaryColor
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val maxTitle = if (metadata.title.length > 45) metadata.title.take(42) + "..." else metadata.title
        canvas.drawText(maxTitle, MARGIN_HORIZONTAL + 62f, y + 43f, titlePaint)

        val subtitlePaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.textMutedColor
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        canvas.drawText(metadata.subtitle, MARGIN_HORIZONTAL + 62f, y + 58f, subtitlePaint)

        // Meta info strip underneath header
        y += 88f
        val metaPaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.textMutedColor
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val metaText = "👤 Learner: ${metadata.learnerName}   |   📊 Level: ${metadata.proficiencyLevel}   |   📅 Date: ${metadata.dateString}"
        canvas.drawText(metaText, MARGIN_HORIZONTAL + 4f, y + 4f, metaPaint)

        val linePaint = Paint().apply {
            isAntiAlias = true
            color = metadata.theme.cardBorderColor
            strokeWidth = 0.8f
        }
        canvas.drawLine(MARGIN_HORIZONTAL, y + 12f, PAGE_WIDTH - MARGIN_HORIZONTAL, y + 12f, linePaint)

        return y + 20f
    }

    private fun drawRunningHeader(canvas: Canvas, metadata: PdfMetadata, startY: Float): Float {
        val y = startY
        val textPaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.textMutedColor
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        val titlePaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.primaryColor
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        canvas.drawText("LISAN AI • ${metadata.title.take(30)}", MARGIN_HORIZONTAL, y + 8f, titlePaint)
        
        val dateText = metadata.dateString.split(",").firstOrNull() ?: metadata.dateString
        val datePaint = TextPaint(textPaint).apply { textAlign = Paint.Align.RIGHT }
        canvas.drawText(dateText, PAGE_WIDTH - MARGIN_HORIZONTAL, y + 8f, datePaint)

        val linePaint = Paint().apply {
            isAntiAlias = true
            color = metadata.theme.cardBorderColor
            strokeWidth = 0.8f
        }
        canvas.drawLine(MARGIN_HORIZONTAL, y + 14f, PAGE_WIDTH - MARGIN_HORIZONTAL, y + 14f, linePaint)

        return y + 24f
    }

    private fun drawPageFooter(canvas: Canvas, metadata: PdfMetadata, pageNumber: Int) {
        val y = PAGE_HEIGHT - MARGIN_BOTTOM + 15f

        val linePaint = Paint().apply {
            isAntiAlias = true
            color = metadata.theme.cardBorderColor
            strokeWidth = 0.8f
        }
        canvas.drawLine(MARGIN_HORIZONTAL, y - 8f, PAGE_WIDTH - MARGIN_HORIZONTAL, y - 8f, linePaint)

        val footerTextPaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.textMutedColor
            textSize = 7.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        canvas.drawText("Generated with Lisan AI — Master English through Urdu Grammar", MARGIN_HORIZONTAL, y + 4f, footerTextPaint)

        val pageNumPaint = TextPaint(footerTextPaint).apply {
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = metadata.theme.primaryColor
        }
        canvas.drawText("Page $pageNumber", PAGE_WIDTH - MARGIN_HORIZONTAL, y + 4f, pageNumPaint)
    }

    // --- Section & Element Drawing ---

    private fun drawSectionHeader(canvas: Canvas, block: PdfBlock.SectionHeader, metadata: PdfMetadata, y: Float): Float {
        var currY = y + 6f
        val linePaint = Paint().apply {
            isAntiAlias = true
            color = metadata.theme.primaryColor
            style = Paint.Style.FILL
        }
        // Small decorative bar
        canvas.drawRoundRect(RectF(MARGIN_HORIZONTAL, currY, MARGIN_HORIZONTAL + 4f, currY + 16f), 2f, 2f, linePaint)

        val titlePaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.primaryColor
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(block.title, MARGIN_HORIZONTAL + 10f, currY + 13f, titlePaint)

        currY += 20f
        if (block.subtitle != null) {
            val subPaint = TextPaint().apply {
                isAntiAlias = true
                color = metadata.theme.textMutedColor
                textSize = 8.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            }
            canvas.drawText(block.subtitle, MARGIN_HORIZONTAL + 10f, currY + 2f, subPaint)
            currY += 12f
        }

        return currY + 4f
    }

    private fun drawChatBubble(
        canvas: Canvas,
        block: PdfBlock.ChatBubble,
        metadata: PdfMetadata,
        textPaint: TextPaint,
        fillPaint: Paint,
        strokePaint: Paint,
        y: Float
    ): Float {
        val isUser = block.isUser
        val bubbleWidth = CONTENT_WIDTH - 20f
        val startX = if (isUser) MARGIN_HORIZONTAL + 20f else MARGIN_HORIZONTAL

        // Measure text content
        val bodyPaint = TextPaint().apply {
            isAntiAlias = true
            color = if (isUser) metadata.theme.primaryColor else metadata.theme.textColor
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, if (isUser) Typeface.BOLD else Typeface.NORMAL)
        }
        val layout = createStaticLayout(block.text, bodyPaint, (bubbleWidth - 24f).toInt())

        var urduLayout: StaticLayout? = null
        if (metadata.includeUrduTranslations && !block.urduTranslation.isNullOrBlank()) {
            val urduPaint = TextPaint().apply {
                isAntiAlias = true
                color = metadata.theme.secondaryColor
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            urduLayout = createStaticLayout(block.urduTranslation, urduPaint, (bubbleWidth - 32f).toInt())
        }

        val tagsHeight = if (metadata.includeGrammarBreakdown && block.grammarTags.isNotEmpty()) 16f else 0f
        val urduHeight = if (urduLayout != null) urduLayout.height + 14f else 0f
        val totalBubbleHeight = 24f + layout.height + urduHeight + tagsHeight + 10f

        // Draw bubble container
        fillPaint.color = if (isUser) metadata.theme.cardBgColor else Color.WHITE
        strokePaint.color = if (isUser) metadata.theme.cardBorderColor else metadata.theme.cardBorderColor
        strokePaint.strokeWidth = 1f

        val bubbleRect = RectF(startX, y, startX + bubbleWidth, y + totalBubbleHeight)
        canvas.drawRoundRect(bubbleRect, 6f, 6f, fillPaint)
        canvas.drawRoundRect(bubbleRect, 6f, 6f, strokePaint)

        // Role indicator strip
        val roleIndicatorPaint = Paint().apply {
            isAntiAlias = true
            color = if (isUser) metadata.theme.secondaryColor else metadata.theme.accentColor
            style = Paint.Style.FILL
        }
        val indRect = RectF(startX, y, startX + 3.5f, y + totalBubbleHeight)
        canvas.drawRoundRect(indRect, 2f, 2f, roleIndicatorPaint)

        // Sender header line
        val senderPaint = TextPaint().apply {
            isAntiAlias = true
            color = if (isUser) metadata.theme.secondaryColor else metadata.theme.primaryColor
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val roleLabel = if (isUser) "👤 Learner" else "🤖 Lisan AI Tutor"
        canvas.drawText(roleLabel, startX + 12f, y + 14f, senderPaint)

        if (metadata.includeTimestamps && !block.timestamp.isNullOrBlank()) {
            val timePaint = TextPaint().apply {
                isAntiAlias = true
                color = metadata.theme.textMutedColor
                textSize = 7.5f
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText(block.timestamp, startX + bubbleWidth - 10f, y + 14f, timePaint)
        }

        // Message text
        canvas.save()
        canvas.translate(startX + 12f, y + 20f)
        layout.draw(canvas)
        canvas.restore()

        var currentInternalY = y + 20f + layout.height + 6f

        // Urdu Translation box
        if (urduLayout != null) {
            val urduBgRect = RectF(startX + 10f, currentInternalY, startX + bubbleWidth - 10f, currentInternalY + urduLayout.height + 10f)
            val urduBgPaint = Paint().apply {
                isAntiAlias = true
                color = metadata.theme.cardBgColor
                style = Paint.Style.FILL
            }
            val urduBorderPaint = Paint().apply {
                isAntiAlias = true
                color = metadata.theme.cardBorderColor
                style = Paint.Style.STROKE
                strokeWidth = 0.5f
            }
            canvas.drawRoundRect(urduBgRect, 4f, 4f, urduBgPaint)
            canvas.drawRoundRect(urduBgRect, 4f, 4f, urduBorderPaint)

            canvas.save()
            canvas.translate(startX + 16f, currentInternalY + 5f)
            urduLayout.draw(canvas)
            canvas.restore()

            currentInternalY += urduLayout.height + 14f
        }

        // Grammar tags
        if (tagsHeight > 0f) {
            var tagX = startX + 12f
            for (tag in block.grammarTags.take(4)) {
                val tagPaint = TextPaint().apply {
                    isAntiAlias = true
                    color = metadata.theme.primaryColor
                    textSize = 7.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                val tagTextWidth = tagPaint.measureText(tag)
                val tagBg = RectF(tagX, currentInternalY, tagX + tagTextWidth + 10f, currentInternalY + 12f)
                val tagBgPaint = Paint().apply {
                    isAntiAlias = true
                    color = metadata.theme.cardBgColor
                    style = Paint.Style.FILL
                }
                canvas.drawRoundRect(tagBg, 3f, 3f, tagBgPaint)
                canvas.drawText(tag, tagX + 5f, currentInternalY + 9f, tagPaint)
                tagX += tagTextWidth + 16f
            }
        }

        return y + totalBubbleHeight + 4f
    }

    private fun drawCallout(
        canvas: Canvas,
        block: PdfBlock.Callout,
        metadata: PdfMetadata,
        textPaint: TextPaint,
        fillPaint: Paint,
        strokePaint: Paint,
        y: Float
    ): Float {
        val cardWidth = CONTENT_WIDTH

        val bodyPaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.textColor
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        val layout = createStaticLayout(block.text, bodyPaint, (cardWidth - 24f).toInt())

        var urduLayout: StaticLayout? = null
        if (!block.urduText.isNullOrBlank() && metadata.includeUrduTranslations) {
            val urduPaint = TextPaint().apply {
                isAntiAlias = true
                color = metadata.theme.secondaryColor
                textSize = 9f
            }
            urduLayout = createStaticLayout(block.urduText, urduPaint, (cardWidth - 24f).toInt())
        }

        val urduH = if (urduLayout != null) urduLayout.height + 8f else 0f
        val totalH = 24f + layout.height + urduH + 10f

        fillPaint.color = if (block.isAccent) metadata.theme.cardBgColor else Color.rgb(255, 255, 255)
        strokePaint.color = if (block.isAccent) metadata.theme.secondaryColor else metadata.theme.cardBorderColor
        strokePaint.strokeWidth = 1f

        val rect = RectF(MARGIN_HORIZONTAL, y, MARGIN_HORIZONTAL + cardWidth, y + totalH)
        canvas.drawRoundRect(rect, 6f, 6f, fillPaint)
        canvas.drawRoundRect(rect, 6f, 6f, strokePaint)

        // Left accent line
        val accentBar = RectF(MARGIN_HORIZONTAL, y, MARGIN_HORIZONTAL + 4f, y + totalH)
        val barPaint = Paint().apply {
            isAntiAlias = true
            color = if (block.isAccent) metadata.theme.secondaryColor else metadata.theme.primaryColor
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(accentBar, 2f, 2f, barPaint)

        // Title
        val titlePaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.primaryColor
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("💡 ${block.title}", MARGIN_HORIZONTAL + 12f, y + 15f, titlePaint)

        // Text
        canvas.save()
        canvas.translate(MARGIN_HORIZONTAL + 12f, y + 22f)
        layout.draw(canvas)
        canvas.restore()

        if (urduLayout != null) {
            canvas.save()
            canvas.translate(MARGIN_HORIZONTAL + 12f, y + 22f + layout.height + 6f)
            urduLayout.draw(canvas)
            canvas.restore()
        }

        return y + totalH + 6f
    }

    private fun drawVocabCard(
        canvas: Canvas,
        block: PdfBlock.VocabCard,
        metadata: PdfMetadata,
        textPaint: TextPaint,
        fillPaint: Paint,
        strokePaint: Paint,
        y: Float
    ): Float {
        val totalH = 58f
        val cardWidth = CONTENT_WIDTH
        val rect = RectF(MARGIN_HORIZONTAL, y, MARGIN_HORIZONTAL + cardWidth, y + totalH)

        fillPaint.color = metadata.theme.cardBgColor
        strokePaint.color = metadata.theme.cardBorderColor
        canvas.drawRoundRect(rect, 5f, 5f, fillPaint)
        canvas.drawRoundRect(rect, 5f, 5f, strokePaint)

        // Word (English)
        val wordPaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.primaryColor
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(block.word, MARGIN_HORIZONTAL + 10f, y + 16f, wordPaint)

        // POS badge
        val posPaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.secondaryColor
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val wordWidth = wordPaint.measureText(block.word)
        canvas.drawText("•  ${block.partOfSpeech.uppercase()}", MARGIN_HORIZONTAL + 16f + wordWidth, y + 15f, posPaint)

        // Urdu Meaning
        if (metadata.includeUrduTranslations && block.urduMeaning.isNotBlank()) {
            val urduPaint = TextPaint().apply {
                isAntiAlias = true
                color = metadata.theme.textColor
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText(block.urduMeaning, PAGE_WIDTH - MARGIN_HORIZONTAL - 10f, y + 16f, urduPaint)
        }

        // Example sentence
        val exPaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.textColor
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        }
        val exText = "e.g. \"${block.exampleEnglish}\""
        canvas.drawText(exText.take(85), MARGIN_HORIZONTAL + 10f, y + 34f, exPaint)

        // Example Urdu
        if (!block.exampleUrdu.isNullOrBlank() && metadata.includeUrduTranslations) {
            val exUrduPaint = TextPaint().apply {
                isAntiAlias = true
                color = metadata.theme.textMutedColor
                textSize = 8f
            }
            canvas.drawText(block.exampleUrdu.take(75), MARGIN_HORIZONTAL + 10f, y + 48f, exUrduPaint)
        }

        return y + totalH + 4f
    }

    private fun drawExerciseItem(
        canvas: Canvas,
        block: PdfBlock.ExerciseItem,
        metadata: PdfMetadata,
        textPaint: TextPaint,
        fillPaint: Paint,
        strokePaint: Paint,
        y: Float
    ): Float {
        var currY = y
        val qPaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.primaryColor
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Question number circle
        val numCircle = RectF(MARGIN_HORIZONTAL, currY, MARGIN_HORIZONTAL + 16f, currY + 16f)
        val numBgPaint = Paint().apply {
            isAntiAlias = true
            color = metadata.theme.primaryColor
            style = Paint.Style.FILL
        }
        canvas.drawOval(numCircle, numBgPaint)

        val numTextPaint = TextPaint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("${block.number}", numCircle.centerX(), numCircle.centerY() + 3f, numTextPaint)

        // Question text
        canvas.drawText(block.question, MARGIN_HORIZONTAL + 22f, currY + 12f, qPaint)
        currY += 20f

        // Options if any
        if (block.options.isNotEmpty()) {
            val optPaint = TextPaint().apply {
                isAntiAlias = true
                color = metadata.theme.textColor
                textSize = 8.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            val letters = listOf("A", "B", "C", "D")
            for ((index, option) in block.options.withIndex()) {
                val letter = letters.getOrElse(index) { "•" }
                val optRect = RectF(MARGIN_HORIZONTAL + 22f, currY, MARGIN_HORIZONTAL + 32f, currY + 10f)
                val checkPaint = Paint().apply {
                    isAntiAlias = true
                    style = Paint.Style.STROKE
                    color = metadata.theme.cardBorderColor
                    strokeWidth = 0.8f
                }
                canvas.drawRoundRect(optRect, 2f, 2f, checkPaint)
                canvas.drawText("[$letter]  $option", MARGIN_HORIZONTAL + 36f, currY + 8f, optPaint)
                currY += 14f
            }
        }

        // Urdu Hint
        if (metadata.includeUrduTranslations && !block.urduHint.isNullOrBlank()) {
            val hintPaint = TextPaint().apply {
                isAntiAlias = true
                color = metadata.theme.secondaryColor
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            }
            canvas.drawText("💡 Hint: ${block.urduHint}", MARGIN_HORIZONTAL + 22f, currY + 8f, hintPaint)
            currY += 14f
        }

        return currY + 4f
    }

    private fun drawKeyValueRow(
        canvas: Canvas,
        block: PdfBlock.KeyValueRow,
        metadata: PdfMetadata,
        textPaint: TextPaint,
        y: Float
    ): Float {
        val keyPaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.primaryColor
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val valPaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.textColor
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        canvas.drawText("• ${block.key}:", MARGIN_HORIZONTAL + 6f, y + 10f, keyPaint)
        val keyWidth = keyPaint.measureText("• ${block.key}: ")
        canvas.drawText(block.value, MARGIN_HORIZONTAL + 10f + keyWidth, y + 10f, valPaint)

        if (!block.urduValue.isNullOrBlank() && metadata.includeUrduTranslations) {
            val urduPaint = TextPaint().apply {
                isAntiAlias = true
                color = metadata.theme.secondaryColor
                textSize = 9f
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText(block.urduValue, PAGE_WIDTH - MARGIN_HORIZONTAL - 10f, y + 10f, urduPaint)
        }

        return y + 18f
    }

    private fun drawTable(
        canvas: Canvas,
        block: PdfBlock.Table,
        metadata: PdfMetadata,
        textPaint: TextPaint,
        fillPaint: Paint,
        strokePaint: Paint,
        y: Float
    ): Float {
        if (block.headers.isEmpty()) return y
        val colCount = block.headers.size
        val colWidth = CONTENT_WIDTH / colCount
        val rowHeight = 22f

        var currY = y

        // Header row background
        val headerRect = RectF(MARGIN_HORIZONTAL, currY, MARGIN_HORIZONTAL + CONTENT_WIDTH, currY + rowHeight)
        fillPaint.color = metadata.theme.headerBgColor
        canvas.drawRoundRect(headerRect, 4f, 4f, fillPaint)

        strokePaint.color = metadata.theme.cardBorderColor
        strokePaint.strokeWidth = 1f
        canvas.drawRoundRect(headerRect, 4f, 4f, strokePaint)

        val headerTextPaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.primaryColor
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        for ((i, header) in block.headers.withIndex()) {
            val cellX = MARGIN_HORIZONTAL + (i * colWidth) + 8f
            canvas.drawText(header.take(25), cellX, currY + 14f, headerTextPaint)
        }

        currY += rowHeight

        // Data rows
        val cellPaint = TextPaint().apply {
            isAntiAlias = true
            color = metadata.theme.textColor
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        for ((rowIndex, row) in block.rows.withIndex()) {
            val rowRect = RectF(MARGIN_HORIZONTAL, currY, MARGIN_HORIZONTAL + CONTENT_WIDTH, currY + rowHeight)
            fillPaint.color = if (rowIndex % 2 == 0) metadata.theme.cardBgColor else Color.WHITE
            canvas.drawRect(rowRect, fillPaint)
            canvas.drawRect(rowRect, strokePaint)

            for ((i, cell) in row.withIndex()) {
                if (i < colCount) {
                    val cellX = MARGIN_HORIZONTAL + (i * colWidth) + 8f
                    canvas.drawText(cell.take(30), cellX, currY + 14f, cellPaint)
                }
            }
            currY += rowHeight
        }

        return currY + 6f
    }

    private fun drawParagraph(
        canvas: Canvas,
        layout: StaticLayout,
        isMuted: Boolean,
        metadata: PdfMetadata,
        y: Float
    ): Float {
        canvas.save()
        canvas.translate(MARGIN_HORIZONTAL + 4f, y + 4f)
        layout.draw(canvas)
        canvas.restore()
        return y + layout.height + 6f
    }

    // --- StaticLayout and Height Helpers ---

    private fun createStaticLayout(text: CharSequence, paint: TextPaint, width: Int): StaticLayout {
        val safeWidth = if (width <= 0) 100 else width
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, safeWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.2f)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(text, paint, safeWidth, Layout.Alignment.ALIGN_NORMAL, 1.2f, 0f, false)
        }
    }

    private fun estimateChatBubbleHeight(block: PdfBlock.ChatBubble, textPaint: TextPaint, width: Float): Float {
        val bodyPaint = TextPaint(textPaint).apply { textSize = 9.5f }
        val layout = createStaticLayout(block.text, bodyPaint, (width - 44f).toInt())
        var h = 30f + layout.height
        if (!block.urduTranslation.isNullOrBlank()) {
            h += 28f
        }
        if (block.grammarTags.isNotEmpty()) {
            h += 16f
        }
        return h
    }

    private fun estimateCalloutHeight(block: PdfBlock.Callout, textPaint: TextPaint, width: Float): Float {
        val layout = createStaticLayout(block.text, textPaint, (width - 24f).toInt())
        return 34f + layout.height + if (!block.urduText.isNullOrBlank()) 20f else 0f
    }

    // --- Intent & Action Helpers ---

    /**
     * Opens the generated PDF in an external viewer.
     */
    fun openPdf(context: Context, uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Open PDF with"))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open PDF", e)
        }
    }

    /**
     * Shares the generated PDF via standard Android share sheet.
     */
    fun sharePdf(context: Context, uri: Uri, title: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Lisan AI Study Notes - $title")
                putExtra(Intent.EXTRA_TEXT, "Here is your study guide generated from Lisan AI.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Study PDF"))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share PDF", e)
        }
    }

    /**
     * Copies the PDF file into the public Downloads directory so the user can easily find it.
     */
    fun savePdfToDownloads(context: Context, sourceFile: File, displayName: String): Boolean {
        return try {
            val fileName = if (displayName.endsWith(".pdf", ignoreCase = true)) displayName else "$displayName.pdf"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/LisanAI")
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { output ->
                        sourceFile.inputStream().use { input ->
                            input.copyTo(output)
                        }
                    }
                    true
                } else false
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val lisanDir = File(downloadsDir, "LisanAI").apply { mkdirs() }
                val destFile = File(lisanDir, fileName)
                sourceFile.copyTo(destFile, overwrite = true)
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save PDF to Downloads", e)
            false
        }
    }

    /**
     * Quick helper to convert a list of Content messages into PdfBlocks.
     */
     fun buildChatTranscriptBlocks(messages: List<Content>): List<PdfBlock> {
        val blocks = mutableListOf<PdfBlock>()
        blocks.add(PdfBlock.SectionHeader("TUTOR CONVERSATION TRANSCRIPT", "Step-by-step dialogue and comparative grammar instruction"))

        for (msg in messages) {
            val isUser = msg.role.equals("user", ignoreCase = true)
            val textContent = msg.parts.mapNotNull { it.text }.joinToString("\n").trim()
            if (textContent.isBlank()) continue

            val tags = mutableListOf<String>()
            if (textContent.contains("tense", ignoreCase = true)) tags.add("Tense")
            if (textContent.contains("noun", ignoreCase = true) || textContent.contains("ism", ignoreCase = true)) tags.add("Ism/Noun")
            if (textContent.contains("verb", ignoreCase = true) || textContent.contains("fi'l", ignoreCase = true)) tags.add("Fi'l/Verb")
            if (textContent.contains("preposition", ignoreCase = true) || textContent.contains("harf", ignoreCase = true)) tags.add("Harf/Preposition")

            blocks.add(
                PdfBlock.ChatBubble(
                    sender = if (isUser) "Learner" else "Lisan AI",
                    text = textContent,
                    isUser = isUser,
                    timestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                    urduTranslation = null,
                    grammarTags = tags
                )
            )
        }
        return blocks
    }

    fun buildTopicStudyGuideBlocks(topicTitle: String, contentBody: String): List<PdfBlock> {
        val blocks = mutableListOf<PdfBlock>()
        blocks.add(PdfBlock.SectionHeader(topicTitle, "Comprehensive Study Guide & Grammar Breakdown"))

        // Split by paragraph boundary (double newlines) to handle multi-page flowing elegantly
        val rawParagraphs = contentBody.split(Regex("\\n{2,}"))
        for (paragraph in rawParagraphs) {
            val trimmed = paragraph.trim()
            if (trimmed.isBlank()) continue

            if (trimmed.startsWith("###") || trimmed.startsWith("##") || trimmed.startsWith("#")) {
                val cleanHeader = trimmed.replace(Regex("^#+\\s*"), "")
                blocks.add(PdfBlock.SectionHeader(cleanHeader, "Topic Chapter"))
            } else if (trimmed.startsWith("-") || trimmed.startsWith("*")) {
                blocks.add(PdfBlock.Callout("Key Focus", trimmed, isAccent = true))
            } else if (trimmed.contains("Rule:") || trimmed.contains("قاعدہ:") || trimmed.contains("Formula:")) {
                blocks.add(PdfBlock.Callout("Grammar Formula", trimmed, isAccent = false))
            } else {
                blocks.add(PdfBlock.Paragraph(trimmed))
            }
        }

        blocks.add(PdfBlock.Spacer)
        blocks.add(PdfBlock.Paragraph("This guide covers essential rules, usage patterns, and bilingual Urdu-Arabic equivalents for mastery."))
        return blocks
    }

    fun buildExamPaperBlocks(
        sourceTitle: String,
        difficulty: String,
        language: String,
        timeLimitMinutes: Int,
        mcqPercentage: String,
        customInstructions: String = "",
        questions: List<com.example.ui.viewmodel.ExamQuestion> = emptyList(),
        shortQuestions: List<String> = emptyList(),
        longQuestions: List<String> = emptyList(),
        includeUrdu: Boolean = true
    ): List<PdfBlock> {
        val blocks = mutableListOf<PdfBlock>()

        blocks.add(
            PdfBlock.SectionHeader(
                "OFFICIAL LISAN EXAMINATION PAPER",
                "Source: $sourceTitle | Level: $difficulty | Time: ${if (timeLimitMinutes > 0) "$timeLimitMinutes Mins" else "Untimed"}"
            )
        )

        val instructionsText = buildString {
            append("• Language Mode: $language\n")
            append("• Difficulty: $difficulty\n")
            append("• Format Ratio: $mcqPercentage MCQs\n")
            if (timeLimitMinutes > 0) append("• Time Limit: $timeLimitMinutes Minutes\n")
            if (customInstructions.isNotBlank()) append("• Learner Custom Directives: $customInstructions\n")
            append("• Mandate: Answer all questions carefully. Show all grammar formulas and translations where requested.")
        }
        val urduInstructions = if (includeUrdu || language.equals("Urdu", ignoreCase = true)) {
            "ہدایات: تمام سوالات کو غور سے پڑھیں، دیے گئے وقت میں حل کریں اور قواعد و ضوابط کی پیروی کریں۔"
        } else null

        blocks.add(PdfBlock.Callout("EXAM SPECIFICATIONS & INSTRUCTIONS", instructionsText, urduText = urduInstructions, isAccent = true))
        blocks.add(PdfBlock.Spacer)

        if (questions.isNotEmpty()) {
            blocks.add(PdfBlock.SectionHeader("SECTION A: MULTIPLE CHOICE QUESTIONS", "Select the single best answer for each item below"))
            questions.forEachIndexed { index, q ->
                blocks.add(
                    PdfBlock.ExerciseItem(
                        number = index + 1,
                        question = q.questionText,
                        options = q.options,
                        urduHint = if (includeUrdu && q.explanation.isNotBlank()) q.explanation else null,
                        answerKey = if (includeUrdu && q.correctAnswerIndex in q.options.indices) "Option ${q.correctAnswerIndex + 1}: ${q.options[q.correctAnswerIndex]}" else null
                    )
                )
            }
            blocks.add(PdfBlock.Spacer)
        }

        if (shortQuestions.isNotEmpty()) {
            blocks.add(PdfBlock.SectionHeader("SECTION B: CONVERSATIONAL SHORT ANSWER QUESTIONS", "Write concise English/Urdu answers for each question"))
            shortQuestions.forEachIndexed { index, sq ->
                blocks.add(
                    PdfBlock.Callout(
                        title = "Question B${index + 1}",
                        text = sq,
                        urduText = if (includeUrdu) "مختصر اور جامع جواب تحریر کریں" else null,
                        isAccent = false
                    )
                )
            }
            blocks.add(PdfBlock.Spacer)
        }

        if (longQuestions.isNotEmpty()) {
            blocks.add(PdfBlock.SectionHeader("SECTION C: COMPREHENSIVE LONG & SCENARIO QUESTIONS", "Provide detailed responses with full grammatical structure"))
            longQuestions.forEachIndexed { index, lq ->
                blocks.add(
                    PdfBlock.Callout(
                        title = "Scenario C${index + 1}",
                        text = lq,
                        urduText = if (includeUrdu) "تفصیلی گرامر، جملہ سازی اور مثالوں کے ساتھ جواب دیں" else null,
                        isAccent = true
                    )
                )
            }
            blocks.add(PdfBlock.Spacer)
        }

        blocks.add(PdfBlock.SectionHeader("EXAM EVALUATION & SUBMISSION", "Submit your answers via Lisan AI Tutor Chat for instant AI grading"))
        blocks.add(PdfBlock.Paragraph("This exam paper was dynamically generated by Lisan AI incorporating your personalized source text, learner history, difficulty level, and custom instructions."))

        return blocks
    }
}
