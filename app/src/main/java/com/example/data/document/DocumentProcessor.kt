package com.example.data.document

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import android.util.Xml
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.xmlpull.v1.XmlPullParser
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.UUID
import java.util.zip.ZipInputStream

object DocumentProcessor {
    private const val TAG = "DocumentProcessor"
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    suspend fun processDocument(
        context: Context,
        uri: Uri,
        customFileName: String? = null
    ): Pair<DocumentEntity, List<DocumentChunkEntity>> = withContext(Dispatchers.IO) {
        val documentId = UUID.randomUUID().toString()
        val contentResolver = context.contentResolver

        // 1. IDENTIFY TYPE & METADATA
        val fileName = customFileName 
            ?: getFileNameFromUri(context, uri) 
            ?: "Document_${System.currentTimeMillis()}"
        val mimeType = contentResolver.getType(uri) ?: getMimeTypeFromExtension(fileName)
        val fileSize = getFileSize(context, uri)

        Log.d(TAG, "Starting pipeline for $fileName | Mime: $mimeType | Size: $fileSize bytes")

        if (fileSize > 15 * 1024 * 1024) {
            val failedDoc = DocumentEntity(
                documentId = documentId,
                fileName = fileName,
                mimeType = mimeType,
                fileSize = fileSize,
                sourceUri = uri.toString(),
                processingStatus = ProcessingStatus.FAILED.name,
                statusMessage = "The selected file exceeds the 15 MB safety limit. Please upload a smaller document."
            )
            return@withContext Pair(failedDoc, emptyList())
        }

        if (fileSize == 0L) {
            val failedDoc = DocumentEntity(
                documentId = documentId,
                fileName = fileName,
                mimeType = mimeType,
                fileSize = 0L,
                sourceUri = uri.toString(),
                processingStatus = ProcessingStatus.FAILED.name,
                statusMessage = "The selected file is empty."
            )
            return@withContext Pair(failedDoc, emptyList())
        }

        var extractedText = ""
        var pageCount = 0
        var status = ProcessingStatus.READY
        var statusMessage: String? = null
        var pagesList = mutableListOf<DocumentPageMetadata>()
        var chaptersList = mutableListOf<DocumentChapterMetadata>()

        try {
            when {
                // PDF Document Parsing
                mimeType.contains("pdf") || fileName.endsWith(".pdf", ignoreCase = true) -> {
                    val pdfResult = extractPdfContent(context, uri)
                    extractedText = pdfResult.text
                    pageCount = pdfResult.pageCount
                    pagesList = pdfResult.pages.toMutableList()

                    if (pdfResult.isScanned || extractedText.isBlank()) {
                        status = ProcessingStatus.REQUIRES_OCR
                        statusMessage = "Scanned document detected. This PDF contains image-based pages without selectable text. You can capture a snapshot of a specific page using the Camera option to study it with your Tutor!"
                    }
                }

                // Word DOCX Parsing
                mimeType.contains("wordprocessingml") || fileName.endsWith(".docx", ignoreCase = true) -> {
                    val docxResult = extractDocxContent(context, uri)
                    extractedText = docxResult.text
                    chaptersList = docxResult.chapters.toMutableList()
                    pageCount = docxResult.estimatedPageCount
                }

                // PowerPoint PPTX Parsing
                mimeType.contains("presentationml") || fileName.endsWith(".pptx", ignoreCase = true) -> {
                    val pptxResult = extractPptxContent(context, uri)
                    extractedText = pptxResult.text
                    pageCount = pptxResult.slideCount
                    chaptersList = pptxResult.slidesAsChapters.toMutableList()
                }

                // Excel XLSX Parsing
                mimeType.contains("spreadsheetml") || fileName.endsWith(".xlsx", ignoreCase = true) -> {
                    val xlsxResult = extractXlsxContent(context, uri)
                    extractedText = xlsxResult.text
                    pageCount = xlsxResult.sheetCount
                }

                // Plain Text, CSV, Markdown, JSON, RTF
                else -> {
                    extractedText = extractTextContent(context, uri)
                    pageCount = (extractedText.length / 2000).coerceAtLeast(1)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting content from $fileName", e)
            status = ProcessingStatus.FAILED
            statusMessage = "Couldn't analyze this document: ${e.localizedMessage ?: "Parsing error"}"
        }

        // VALIDATE EXTRACTED CONTENT
        if (status == ProcessingStatus.READY && extractedText.trim().isBlank()) {
            status = ProcessingStatus.FAILED
            statusMessage = "Couldn't extract text from this document. Format may be encrypted or unsupported."
        }

        val wordCount = if (extractedText.isNotBlank()) extractedText.split("\\s+".toRegex()).size else 0

        // STRUCTURE CONTENT
        val inferredStructure = inferStructure(extractedText, pagesList, chaptersList)
        val structureJsonString = try {
            json.encodeToString(inferredStructure)
        } catch (e: Exception) {
            null
        }

        // CHUNKING PIPELINE
        val chunks = if (status == ProcessingStatus.READY && extractedText.isNotBlank()) {
            createSensibleChunks(documentId, extractedText, inferredStructure)
        } else {
            emptyList()
        }

        val document = DocumentEntity(
            documentId = documentId,
            fileName = fileName,
            mimeType = mimeType,
            fileSize = fileSize,
            sourceUri = uri.toString(),
            extractedText = extractedText,
            structureJson = structureJsonString,
            processingStatus = status.name,
            statusMessage = statusMessage,
            pageCount = pageCount,
            wordCount = wordCount,
            chunkCount = chunks.size
        )

        Log.d(TAG, "Document processed successfully. Chunks: ${chunks.size}, Words: $wordCount, Status: $status")
        return@withContext Pair(document, chunks)
    }

    // --- PDF PARSER ---
    private data class PdfExtractResult(
        val text: String,
        val pageCount: Int,
        val isScanned: Boolean,
        val pages: List<DocumentPageMetadata>
    )

    private fun extractPdfContent(context: Context, uri: Uri): PdfExtractResult {
        var pageCount = 0
        var isScanned = false
        val pagesList = mutableListOf<DocumentPageMetadata>()
        val textBuilder = StringBuilder()

        try {
            val pfd = context.contentResolver.openFileDescriptor(uri, "r")
            if (pfd != null) {
                val renderer = PdfRenderer(pfd)
                pageCount = renderer.pageCount
                renderer.close()
                pfd.close()
                if (pageCount > 150) {
                    throw IllegalArgumentException("The PDF contains $pageCount pages, which exceeds the 150 pages safety limit. Please upload a shorter document.")
                }
            }
        } catch (e: IllegalArgumentException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "PdfRenderer unavailable for page count: ${e.message}")
        }

        // Stream text extraction from PDF stream incrementally to protect heap memory
        val streamText = try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = InputStreamReader(inputStream, Charsets.ISO_8859_1)
                val buffer = CharArray(16384)
                val builder = StringBuilder()
                var read: Int
                while (reader.read(buffer).also { read = it } != -1) {
                    builder.append(buffer, 0, read)
                    if (builder.length > 15_000_000) { // Safety ceiling on stream length
                        throw IllegalArgumentException("The PDF file stream content is too large to process safely.")
                    }
                }
                builder.toString()
            } ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error streaming raw PDF data", e)
            ""
        }

        // Extract text between BT (Begin Text) and ET (End Text) operators in PDF stream
        val btEtRegex = """BT[\s\S]*?ET""".toRegex()
        val textMatches = btEtRegex.findAll(streamText)

        var currentPage = 1
        var pageTextBuilder = StringBuilder()

        for (match in textMatches) {
            val block = match.value
            // Extract all string literals inside BT ... ET block to handle multi-column layouts and spacing operators
            val stringRegex = """\(([^)]*)\)""".toRegex()
            for (strMatch in stringRegex.findAll(block)) {
                val literal = strMatch.groupValues[1]
                    .replace("""\(""", "(")
                    .replace("""\)""", ")")
                    .replace("""\\""", """\"""")
                if (literal.isNotBlank()) {
                    pageTextBuilder.append(literal).append(" ")
                }
            }
        }

        var fullText = pageTextBuilder.toString().trim()

        // Fallback: If BT/ET extraction yields clean text
        if (fullText.length > 50) {
            // Segment into estimated pages if pageCount > 0
            val avgCharsPerPage = if (pageCount > 0) fullText.length / pageCount else fullText.length
            val chunkSize = avgCharsPerPage.coerceAtLeast(500)
            
            var pIndex = 1
            var offset = 0
            while (offset < fullText.length) {
                val end = (offset + chunkSize).coerceAtMost(fullText.length)
                val pageSegment = fullText.substring(offset, end)
                pagesList.add(DocumentPageMetadata(pageNumber = pIndex, characterCount = pageSegment.length))
                offset = end
                pIndex++
            }
        } else {
            // Check if PDF has raw text stream fallback
            val plainTextFallback = extractTextFromRawPdfStream(streamText)
            if (plainTextFallback.length > 30) {
                fullText = plainTextFallback
                pagesList.add(DocumentPageMetadata(pageNumber = 1, characterCount = fullText.length))
            } else {
                isScanned = true
            }
        }

        return PdfExtractResult(
            text = fullText,
            pageCount = if (pageCount > 0) pageCount else pagesList.size.coerceAtLeast(1),
            isScanned = isScanned,
            pages = pagesList
        )
    }

    private fun extractTextFromRawPdfStream(rawStream: String): String {
        val sb = StringBuilder()
        val regex = """\(([^)]+)\)""".toRegex()
        val matches = regex.findAll(rawStream)
        for (m in matches) {
            val str = m.groupValues[1]
            if (str.length > 3 && str.any { it.isLetter() }) {
                sb.append(str).append(" ")
            }
        }
        return sb.toString().replace("\\s+".toRegex(), " ").trim()
    }

    // --- DOCX PARSER ---
    private data class DocxExtractResult(
        val text: String,
        val chapters: List<DocumentChapterMetadata>,
        val estimatedPageCount: Int
    )

    private fun extractDocxContent(context: Context, uri: Uri): DocxExtractResult {
        val sb = StringBuilder()
        val chapters = mutableListOf<DocumentChapterMetadata>()
        var currentChapterTitle: String? = null
        var currentChapterTextLength = 0

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            ZipInputStream(inputStream).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    if (entry.name == "word/document.xml") {
                        val parser = Xml.newPullParser()
                        parser.setInput(InputStreamReader(zip, Charsets.UTF_8))
                        var eventType = parser.eventType

                        var inParagraph = false
                        var isHeading = false
                        val currentParagraphText = StringBuilder()

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            when (eventType) {
                                XmlPullParser.START_TAG -> {
                                    when (parser.name) {
                                        "p" -> {
                                            inParagraph = true
                                            isHeading = false
                                            currentParagraphText.clear()
                                        }
                                        "pStyle" -> {
                                            val styleVal = parser.getAttributeValue(null, "val") ?: ""
                                            if (styleVal.contains("Heading", ignoreCase = true) || styleVal.contains("Title", ignoreCase = true)) {
                                                isHeading = true
                                            }
                                        }
                                        "t" -> {
                                            if (inParagraph) {
                                                val text = parser.nextText()
                                                currentParagraphText.append(text)
                                            }
                                        }
                                        "tr" -> {
                                            // Table row
                                            sb.append("\n")
                                        }
                                    }
                                }
                                XmlPullParser.END_TAG -> {
                                    if (parser.name == "p") {
                                        inParagraph = false
                                        val pText = currentParagraphText.toString().trim()
                                        if (pText.isNotBlank()) {
                                            if (isHeading || pText.length in 4..60 && pText.first().isUpperCase()) {
                                                currentChapterTitle?.let { title ->
                                                    chapters.add(DocumentChapterMetadata(title = title, startPage = 1, endPage = 1))
                                                }
                                                currentChapterTitle = pText
                                                sb.append("\n\n### ").append(pText).append("\n")
                                            } else {
                                                sb.append(pText).append("\n\n")
                                            }
                                            currentChapterTextLength += pText.length
                                        }
                                    }
                                }
                            }
                            eventType = parser.next()
                        }
                        currentChapterTitle?.let { title ->
                            chapters.add(DocumentChapterMetadata(title = title, startPage = 1, endPage = 1))
                        }
                    }
                    entry = zip.nextEntry
                }
            }
        }

        val text = sb.toString().trim()
        val pageEst = (text.length / 1800).coerceAtLeast(1)
        return DocxExtractResult(text, chapters, pageEst)
    }

    // --- PPTX PARSER ---
    private data class PptxExtractResult(
        val text: String,
        val slideCount: Int,
        val slidesAsChapters: List<DocumentChapterMetadata>
    )

    private fun extractPptxContent(context: Context, uri: Uri): PptxExtractResult {
        val sb = StringBuilder()
        val slidesList = mutableListOf<DocumentChapterMetadata>()
        val slideEntries = mutableMapOf<Int, String>()

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            ZipInputStream(inputStream).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    val name = entry.name
                    if (name.startsWith("ppt/slides/slide") && name.endsWith(".xml")) {
                        val slideNumStr = name.substringAfter("slide").substringBefore(".xml")
                        val slideNum = slideNumStr.toIntOrNull() ?: 999
                        
                        // Extract text content from slide XML
                        val slideText = parseXmlTextNodes(zip, "a:t")
                        if (slideText.isNotBlank()) {
                            slideEntries[slideNum] = slideText
                        }
                    }
                    entry = zip.nextEntry
                }
            }
        }

        val sortedSlides = slideEntries.keys.sorted()
        for (slideNum in sortedSlides) {
            val slideText = slideEntries[slideNum] ?: ""
            val slideTitle = "Slide $slideNum"
            sb.append("\n\n--- ").append(slideTitle).append(" ---\n").append(slideText)
            slidesList.add(DocumentChapterMetadata(title = slideTitle, startPage = slideNum, endPage = slideNum))
        }

        val fullText = sb.toString().trim()
        return PptxExtractResult(fullText, sortedSlides.size.coerceAtLeast(1), slidesList)
    }

    // --- XLSX PARSER ---
    private data class XlsxExtractResult(
        val text: String,
        val sheetCount: Int
    )

    private fun extractXlsxContent(context: Context, uri: Uri): XlsxExtractResult {
        val sb = StringBuilder()
        val sharedStrings = mutableListOf<String>()
        var sheetCount = 0

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            ZipInputStream(inputStream).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    if (entry.name == "xl/sharedStrings.xml") {
                        val parser = Xml.newPullParser()
                        parser.setInput(InputStreamReader(zip, Charsets.UTF_8))
                        var eventType = parser.eventType
                        var inT = false
                        val currentText = StringBuilder()

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            when (eventType) {
                                XmlPullParser.START_TAG -> if (parser.name == "t") inT = true
                                XmlPullParser.TEXT -> if (inT) currentText.append(parser.text)
                                XmlPullParser.END_TAG -> {
                                    if (parser.name == "t") {
                                        inT = false
                                        sharedStrings.add(currentText.toString())
                                        currentText.clear()
                                    }
                                }
                            }
                            eventType = parser.next()
                        }
                    } else if (entry.name.startsWith("xl/worksheets/sheet") && entry.name.endsWith(".xml")) {
                        sheetCount++
                        val sheetName = "Sheet $sheetCount"
                        sb.append("\n\n--- ").append(sheetName).append(" ---\n")
                        val sheetText = parseXlsxSheet(zip, sharedStrings)
                        sb.append(sheetText)
                    }
                    entry = zip.nextEntry
                }
            }
        }

        return XlsxExtractResult(sb.toString().trim(), sheetCount.coerceAtLeast(1))
    }

    private fun parseXlsxSheet(inputStream: InputStream, sharedStrings: List<String>): String {
        val sb = StringBuilder()
        val parser = Xml.newPullParser()
        parser.setInput(InputStreamReader(inputStream, Charsets.UTF_8))
        var eventType = parser.eventType

        var currentCellType = ""
        var currentCellValue = StringBuilder()
        var rowText = StringBuilder()

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (parser.name == "c") {
                        currentCellType = parser.getAttributeValue(null, "t") ?: ""
                        currentCellValue.clear()
                    } else if (parser.name == "v") {
                        currentCellValue.clear()
                    }
                }
                XmlPullParser.TEXT -> {
                    currentCellValue.append(parser.text)
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "c") {
                        val rawVal = currentCellValue.toString()
                        val valFormatted = if (currentCellType == "s") {
                            val idx = rawVal.toIntOrNull()
                            if (idx != null && idx in sharedStrings.indices) sharedStrings[idx] else rawVal
                        } else {
                            rawVal
                        }
                        if (valFormatted.isNotBlank()) {
                            rowText.append(valFormatted).append(" | ")
                        }
                    } else if (parser.name == "row") {
                        if (rowText.isNotBlank()) {
                            sb.append(rowText.toString().removeSuffix(" | ")).append("\n")
                            rowText.clear()
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return sb.toString()
    }

    // Helper XML Text Extractor
    private fun parseXmlTextNodes(inputStream: InputStream, tagName: String): String {
        val sb = StringBuilder()
        val parser = Xml.newPullParser()
        parser.setInput(InputStreamReader(inputStream, Charsets.UTF_8))
        var eventType = parser.eventType
        var inTarget = false

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (parser.name == "t" || parser.name.endsWith(":t")) {
                        inTarget = true
                    }
                }
                XmlPullParser.TEXT -> {
                    if (inTarget) {
                        sb.append(parser.text).append(" ")
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "t" || parser.name.endsWith(":t")) {
                        inTarget = false
                    }
                }
            }
            eventType = parser.next()
        }
        return sb.toString().replace("\\s+".toRegex(), " ").trim()
    }

    // --- PLAIN TEXT / CSV PARSER ---
    private fun extractTextContent(context: Context, uri: Uri): String {
        val sb = StringBuilder()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    sb.append(line).append("\n")
                    line = reader.readLine()
                }
            }
        }
        return sb.toString().trim()
    }

    // --- STRUCTURE INFERENCING ---
    private fun inferStructure(
        text: String,
        pages: List<DocumentPageMetadata>,
        chapters: List<DocumentChapterMetadata>
    ): DocumentStructure {
        val detectedChapters = chapters.toMutableList()

        if (detectedChapters.isEmpty()) {
            // Find headings via regex patterns (e.g. "Chapter 1", "Section 2.1", "Unit 3", "Lesson 4")
            val chapterRegex = """(?i)^(Chapter|Section|Unit|Lesson|Part|باب|فصل)\s+\d+.*""".toRegex(RegexOption.MULTILINE)
            val matches = chapterRegex.findAll(text)

            var lastIdx = 0
            for (match in matches) {
                val title = match.value.trim()
                detectedChapters.add(
                    DocumentChapterMetadata(
                        title = title,
                        startPage = (lastIdx / 1800) + 1,
                        endPage = (match.range.first / 1800) + 1
                    )
                )
                lastIdx = match.range.first
            }
        }

        return DocumentStructure(
            pages = pages,
            chapters = detectedChapters,
            documentType = if (text.contains("Chapter", ignoreCase = true)) "Textbook / Educational Guide" else "Reference Document",
            detectedLanguage = if (text.any { it in '\u0600'..'\u06FF' }) "Urdu / Bilingual" else "English"
        )
    }

    // --- CHUNKING STRATEGY ---
    private fun createSensibleChunks(
        documentId: String,
        fullText: String,
        structure: DocumentStructure
    ): List<DocumentChunkEntity> {
        val chunks = mutableListOf<DocumentChunkEntity>()
        val targetChunkChars = 1000 // ~150 - 200 words per chunk

        // Split by paragraphs first
        val paragraphs = fullText.split("\n\n+".toRegex()).filter { it.isNotBlank() }

        var currentChunkText = StringBuilder()
        var currentChapter: String? = structure.chapters.firstOrNull()?.title
        var currentSection: String? = null
        var currentPageNum = 1
        var chunkIndex = 0

        for (paragraph in paragraphs) {
            val trimmedP = paragraph.trim()

            // Check if paragraph is a heading/chapter line
            if (trimmedP.startsWith("### ") || trimmedP.startsWith("--- ") || trimmedP.matches("""(?i)^(Chapter|Section|Unit|Lesson|Part)\s+\d+.*""".toRegex())) {
                val headingTitle = trimmedP.removePrefix("### ").removePrefix("--- ").trim()
                if (headingTitle.contains("Chapter", ignoreCase = true) || headingTitle.contains("Slide", ignoreCase = true) || headingTitle.contains("Sheet", ignoreCase = true)) {
                    currentChapter = headingTitle
                } else {
                    currentSection = headingTitle
                }
            }

            if (currentChunkText.length + trimmedP.length > targetChunkChars && currentChunkText.isNotBlank()) {
                val textContent = currentChunkText.toString().trim()
                val wordCount = textContent.split("\\s+".toRegex()).size

                chunks.add(
                    DocumentChunkEntity(
                        chunkId = "${documentId}_chunk_$chunkIndex",
                        documentId = documentId,
                        chunkIndex = chunkIndex,
                        pageNumber = currentPageNum,
                        chapter = currentChapter,
                        section = currentSection,
                        heading = currentSection ?: currentChapter,
                        text = textContent,
                        wordCount = wordCount
                    )
                )

                chunkIndex++
                currentPageNum = (chunkIndex * targetChunkChars / 1800) + 1
                currentChunkText = StringBuilder()
            }

            currentChunkText.append(trimmedP).append("\n\n")
        }

        if (currentChunkText.isNotBlank()) {
            val textContent = currentChunkText.toString().trim()
            val wordCount = textContent.split("\\s+".toRegex()).size

            chunks.add(
                DocumentChunkEntity(
                    chunkId = "${documentId}_chunk_$chunkIndex",
                    documentId = documentId,
                    chunkIndex = chunkIndex,
                    pageNumber = currentPageNum,
                    chapter = currentChapter,
                    section = currentSection,
                    heading = currentSection ?: currentChapter,
                    text = textContent,
                    wordCount = wordCount
                )
            )
        }

        return chunks
    }

    // --- HELPER UTILS ---
    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var name: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) name = it.getString(nameIndex)
                }
            }
        }
        if (name == null) {
            name = uri.path?.substringAfterLast('/')
        }
        return name
    }

    private fun getFileSize(context: Context, uri: Uri): Long {
        var size = 0L
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    if (sizeIndex != -1) size = cursor.getLong(sizeIndex)
                }
            }
            if (size == 0L) {
                context.contentResolver.openInputStream(uri)?.use {
                    size = it.available().toLong()
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not determine file size for $uri", e)
        }
        return size
    }

    private fun getMimeTypeFromExtension(fileName: String): String {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        return when (ext) {
            "pdf" -> "application/pdf"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "csv" -> "text/csv"
            "json" -> "application/json"
            "md" -> "text/markdown"
            else -> "text/plain"
        }
    }
}
