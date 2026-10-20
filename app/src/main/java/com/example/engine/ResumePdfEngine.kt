package com.example.engine

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.data.model.ResumeProfileEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ResumePdfEngine {

    /**
     * Generates a clean, professional, page-aligned A4 PDF of the student's resume.
     * Dimensions: 595 x 842 points (standard A4 at 72 dpi).
     */
    fun generateResumePdf(
        context: Context,
        profile: ResumeProfileEntity,
        photoBitmap: Bitmap?
    ): File {
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val primaryColor = Color.rgb(67, 56, 202) // #4338CA Primary Indigo
        val textPrimaryColor = Color.rgb(30, 41, 59) // Slate 800
        val textSecondaryColor = Color.rgb(71, 85, 105) // Slate 600
        val accentBgColor = Color.rgb(238, 242, 255) // Indigo 50
        val dividerColor = Color.rgb(203, 213, 225) // Slate 300

        // Top Accent Color Bar
        val accentBarPaint = Paint().apply {
            color = primaryColor
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 8f, accentBarPaint)

        var currentY = 38f
        val leftMargin = 36f
        val rightMargin = pageWidth - 36f
        val contentWidth = rightMargin - leftMargin

        // 1. Header Section: Profile Photo & Contact Info
        val photoSize = 64
        val hasPhoto = photoBitmap != null
        val headerTextStartX = if (hasPhoto) leftMargin + photoSize + 16f else leftMargin

        if (photoBitmap != null) {
            try {
                val circularBitmap = getCircularBitmap(photoBitmap, photoSize)
                canvas.drawBitmap(circularBitmap, leftMargin, currentY, null)

                // Circular border around photo
                val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = primaryColor
                    style = Paint.Style.STROKE
                    strokeWidth = 2f
                }
                canvas.drawCircle(leftMargin + photoSize / 2f, currentY + photoSize / 2f, photoSize / 2f, borderPaint)
            } catch (e: Exception) {
                // In case of bitmap processing issue, fallback gracefully
            }
        }

        // Full Name
        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = primaryColor
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val candidateName = profile.fullName.ifBlank { "Alex Rivera" }
        canvas.drawText(candidateName, headerTextStartX, currentY + 18f, namePaint)

        // Contact info line 1: Email & Phone
        val contactPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textSecondaryColor
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        val email = profile.email.ifBlank { "alex.rivera@university.edu" }
        val phone = profile.phone.ifBlank { "+1 (555) 019-2834" }
        val github = profile.githubUrl.ifBlank { "github.com/alexrivera" }
        val linkedin = profile.linkedinUrl.ifBlank { "linkedin.com/in/alexrivera" }

        canvas.drawText("$email  •  $phone", headerTextStartX, currentY + 34f, contactPaint)
        canvas.drawText("$github  •  $linkedin", headerTextStartX, currentY + 48f, contactPaint)

        currentY = maxOf(currentY + photoSize + 12f, currentY + 62f)

        // Horizontal Separator
        val dividerPaint = Paint().apply {
            color = dividerColor
            strokeWidth = 1f
        }
        canvas.drawLine(leftMargin, currentY, rightMargin, currentY, dividerPaint)
        currentY += 14f

        // Helper for Section Titles
        fun drawSectionHeader(title: String) {
            val bgPaint = Paint().apply {
                color = accentBgColor
            }
            canvas.drawRoundRect(
                leftMargin,
                currentY,
                rightMargin,
                currentY + 20f,
                4f,
                4f,
                bgPaint
            )

            // Left decorative pill
            val pillPaint = Paint().apply {
                color = primaryColor
            }
            canvas.drawRoundRect(
                leftMargin,
                currentY,
                leftMargin + 4f,
                currentY + 20f,
                2f,
                2f,
                pillPaint
            )

            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = primaryColor
                textSize = 9.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText(title, leftMargin + 10f, currentY + 14f, titlePaint)
            currentY += 26f
        }

        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textPrimaryColor
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val bulletPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = primaryColor
            textSize = 9f
        }

        // 2. Summary
        val summaryText = profile.summary.ifBlank {
            "Motivated Dual-Degree Scholar with rigorous foundations in Systems Software, High-Performance Computing, and Applied AI."
        }
        drawSectionHeader("ACADEMIC PROFILE & OBJECTIVE")
        currentY = drawWrappedText(canvas, summaryText, leftMargin + 6f, currentY, contentWidth - 12f, bodyPaint, 13f)
        currentY += 10f

        // 3. Education
        val eduText = profile.educationJson.ifBlank {
            "National Institute of Technology | B.Tech Computer Science & Engineering (CGPA: 9.25/10.0) | 2024 - 2028\nIIT Madras | BS Data Science & Applications (Foundational GPA: 8.8/10.0) | 2024 - 2028"
        }
        drawSectionHeader("EDUCATION & ACADEMIC STANDING")
        eduText.lines().filter { it.isNotBlank() }.forEach { line ->
            canvas.drawText("•", leftMargin + 6f, currentY, bulletPaint)
            currentY = drawWrappedText(canvas, line.trim(), leftMargin + 16f, currentY, contentWidth - 22f, bodyPaint, 12f)
            currentY += 3f
        }
        currentY += 8f

        // 4. Technical Skills
        val skillsText = profile.skillsJson.ifBlank {
            "Languages: Kotlin, C++, Python, Rust, SQL, Bash\nSystems: Linux Kernel Internals, Multithreading, Distributed Consensus (Raft), Docker\nFrameworks: Jetpack Compose, PyTorch, Ktor, Room SQLite, Android SDK"
        }
        drawSectionHeader("TECHNICAL SKILL ARRAYS")
        skillsText.lines().filter { it.isNotBlank() }.forEach { line ->
            canvas.drawText("•", leftMargin + 6f, currentY, bulletPaint)
            currentY = drawWrappedText(canvas, line.trim(), leftMargin + 16f, currentY, contentWidth - 22f, bodyPaint, 12f)
            currentY += 3f
        }
        currentY += 8f

        // 5. Technical Projects
        val projText = profile.projectsJson.ifBlank {
            "Mini-Raft KV Store: High-performance distributed key-value store with leader election and log replication.\nNeural Compiler Pass: Custom LLVM optimization pass lowering tensor compute graphs to vectorized SIMD.\nOffline Academic Command Center: Native Android M3 suite with local Room architecture and competitive tracking."
        }
        drawSectionHeader("KEY TECHNICAL PROJECTS")
        projText.lines().filter { it.isNotBlank() }.forEach { line ->
            canvas.drawText("•", leftMargin + 6f, currentY, bulletPaint)
            currentY = drawWrappedText(canvas, line.trim(), leftMargin + 16f, currentY, contentWidth - 22f, bodyPaint, 12f)
            currentY += 3f
        }
        currentY += 8f

        // 6. Experience & Honors
        val expText = profile.experienceJson.ifBlank {
            "Undergraduate Researcher — High Performance Systems Lab (Prof. V. Sharma)\nDepartment Academic Peer Mentor — Data Structures & Algorithms\nGATE Candidate — Computer Science & Engineering Focus"
        }
        drawSectionHeader("EXPERIENCE, RESEARCH & HONORS")
        expText.lines().filter { it.isNotBlank() }.forEach { line ->
            canvas.drawText("•", leftMargin + 6f, currentY, bulletPaint)
            currentY = drawWrappedText(canvas, line.trim(), leftMargin + 16f, currentY, contentWidth - 22f, bodyPaint, 12f)
            currentY += 3f
        }

        // Footer at bottom of A4
        val footerY = pageHeight - 24f
        canvas.drawLine(leftMargin, footerY - 10f, rightMargin, footerY - 10f, dividerPaint)
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(148, 163, 184)
            textSize = 7.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        val dateStamp = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        canvas.drawText("Academic Hub • Offline Tabular Resume Builder • Generated on $dateStamp", leftMargin, footerY, footerPaint)

        document.finishPage(page)

        // Save to app cache
        val fileNameSafe = candidateName.replace(Regex("[^a-zA-Z0-9_]"), "_")
        val cachePdfFile = File(context.cacheDir, "Resume_${fileNameSafe}.pdf")
        FileOutputStream(cachePdfFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        return cachePdfFile
    }

    /**
     * Copies the generated PDF into the device's public Downloads directory
     * so the user can easily find it in the Downloads folder or Files app.
     */
    fun savePdfToDownloads(context: Context, pdfFile: File, candidateName: String): Uri? {
        val fileNameSafe = candidateName.replace(Regex("[^a-zA-Z0-9_]"), "_")
        val displayName = "Resume_${fileNameSafe}_${System.currentTimeMillis() % 10000}.pdf"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    pdfFile.inputStream().use { input ->
                        input.copyTo(out)
                    }
                }
            }
            uri
        } else {
            @Suppress("DEPRECATION")
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destFile = File(downloadsDir, displayName)
            pdfFile.copyTo(destFile, overwrite = true)
            Uri.fromFile(destFile)
        }
    }

    /**
     * Builds an Intent to view or share the generated PDF file.
     */
    fun createViewPdfIntent(context: Context, pdfFile: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun createSharePdfIntent(context: Context, pdfFile: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "My Academic Resume")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /**
     * Word wrapping helper for canvas drawing.
     */
    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        x: Float,
        startY: Float,
        maxWidth: Float,
        paint: Paint,
        lineHeight: Float
    ): Float {
        var currentY = startY
        val paragraphs = text.split("\n")
        for (paragraph in paragraphs) {
            val words = paragraph.split(" ")
            var currentLine = StringBuilder()
            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                if (paint.measureText(testLine) <= maxWidth) {
                    currentLine = StringBuilder(testLine)
                } else {
                    if (currentLine.isNotEmpty()) {
                        canvas.drawText(currentLine.toString(), x, currentY, paint)
                        currentY += lineHeight
                    }
                    currentLine = StringBuilder(word)
                }
            }
            if (currentLine.isNotEmpty()) {
                canvas.drawText(currentLine.toString(), x, currentY, paint)
                currentY += lineHeight
            }
        }
        return currentY
    }

    /**
     * Crops and scales bitmap into a smooth circular avatar for the PDF header.
     */
    private fun getCircularBitmap(bitmap: Bitmap, targetSize: Int): Bitmap {
        val output = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect(0, 0, targetSize, targetSize)
        val rectF = RectF(rect)

        val minEdge = minOf(bitmap.width, bitmap.height)
        val srcX = (bitmap.width - minEdge) / 2
        val srcY = (bitmap.height - minEdge) / 2
        val cropped = Bitmap.createBitmap(bitmap, srcX, srcY, minEdge, minEdge)

        canvas.drawOval(rectF, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(cropped, null, rect, paint)

        return output
    }
}
