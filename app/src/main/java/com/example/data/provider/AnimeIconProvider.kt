package com.example.data.provider

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Color as AndroidColor
import androidx.core.content.ContextCompat
import com.example.data.model.AnimeCharacter
import com.example.data.repository.AppIconPackManager

/**
 * Interface for custom Icon Pack resolution and rendering.
 */
interface IconProvider {
    fun getAppIcon(context: Context, packageName: String, label: String, character: AnimeCharacter): Bitmap?
    fun isAnimeIconPackEnabled(): Boolean
}

/**
 * Custom IconProvider implementation that applies an 'Anime-style' icon pack theme to icons
 * with glowing character accents, translucent squircle containers, and high-contrast borders.
 */
class AnimeIconProvider : IconProvider {
    private var isEnabled: Boolean = true

    override fun isAnimeIconPackEnabled(): Boolean = isEnabled

    fun setAnimeIconPackEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    override fun getAppIcon(
        context: Context,
        packageName: String,
        label: String,
        character: AnimeCharacter
    ): Bitmap? {
        val baseBitmap = AppIconPackManager.getCustomIconBitmap(context, packageName, label)
            ?: return null

        if (!isEnabled) return baseBitmap

        return applyAnimeIconStyle(baseBitmap, character)
    }

    private fun applyAnimeIconStyle(srcBitmap: Bitmap, character: AnimeCharacter): Bitmap {
        val size = 192
        val iconPadding = 32
        val scaledSize = size - (iconPadding * 2)

        val scaledSrc = Bitmap.createScaledBitmap(srcBitmap, scaledSize, scaledSize, true)
        val result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        val accentColor = AndroidColor.parseColor(character.accentColorHex)
        val cardBg = AndroidColor.parseColor("#1E293B")

        // 1. Draw Translucent Dark Squircle Background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = cardBg
            alpha = 230
        }
        val rect = RectF(6f, 6f, (size - 6).toFloat(), (size - 6).toFloat())
        val cornerRadius = 42f
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, bgPaint)

        // 2. Draw Glowing Accent Border Frame
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accentColor
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)

        // 3. Inner White Accent Highlight Line
        val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 2f
            alpha = 70
        }
        val innerRect = RectF(10f, 10f, (size - 10).toFloat(), (size - 10).toFloat())
        canvas.drawRoundRect(innerRect, cornerRadius - 4f, cornerRadius - 4f, highlightPaint)

        // 4. Center scaled app icon
        val left = (size - scaledSrc.width) / 2f
        val top = (size - scaledSrc.height) / 2f
        canvas.drawBitmap(scaledSrc, left, top, null)

        return result
    }
}
