package com.example.data.repository

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.Toast
import com.example.R
import com.example.data.model.AnimeCharacter

data class WallpaperPreset(
    val id: String,
    val title: String,
    val description: String,
    val resId: Int,
    val isLiveWallpaperOption: Boolean = false
)

object WallpaperServiceManager {

    fun getWallpaperPresets(): List<WallpaperPreset> = listOf(
        WallpaperPreset(
            id = "system_live",
            title = "System Live Wallpaper",
            description = "Use device default live or interactive wallpaper",
            resId = R.drawable.img_nxv_wallpaper_1788744490502,
            isLiveWallpaperOption = true
        ),
        WallpaperPreset(
            id = "luffy_red",
            title = "Luffy Gear Crimson",
            description = "High-contrast dark red cyberpunk anime wallpaper",
            resId = R.drawable.img_anime_luffy_1788767968720
        ),
        WallpaperPreset(
            id = "naruto_orange",
            title = "Naruto Sage Flame",
            description = "Vibrant orange ninja scroll wallpaper",
            resId = R.drawable.img_anime_naruto_1788767990980
        ),
        WallpaperPreset(
            id = "tanjiro_emerald",
            title = "Tanjiro Water Slash",
            description = "Emerald green checkerboard anime wallpaper",
            resId = R.drawable.img_anime_tanjiro_1788768011791
        ),
        WallpaperPreset(
            id = "nxv_default",
            title = "NXV Cyberpunk Minimal",
            description = "Dark geometric vector line grid wallpaper",
            resId = R.drawable.img_nxv_wallpaper_1788744490502
        )
    )

    /**
     * Sets static wallpaper to System Home Screen, Lock Screen, or Both.
     */
    fun applyWallpaperFromResource(
        context: Context,
        resId: Int,
        setHomeScreen: Boolean = true,
        setLockScreen: Boolean = true
    ): Boolean {
        return try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val bitmap = BitmapFactory.decodeResource(context.resources, resId) ?: return false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                var flags = 0
                if (setHomeScreen) flags = flags or WallpaperManager.FLAG_SYSTEM
                if (setLockScreen) flags = flags or WallpaperManager.FLAG_LOCK

                wallpaperManager.setBitmap(bitmap, null, true, flags)
            } else {
                wallpaperManager.setBitmap(bitmap)
            }
            Toast.makeText(context, "Wallpaper Applied Successfully!", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to apply wallpaper: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Opens system Live Wallpaper Picker activity.
     */
    fun openLiveWallpaperPicker(context: Context) {
        try {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_SET_WALLPAPER).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Cannot open wallpaper picker", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Retrieves current system wallpaper drawable if permitted.
     */
    fun getSystemWallpaperDrawable(context: Context): Drawable? {
        return try {
            val wm = WallpaperManager.getInstance(context)
            wm.drawable ?: wm.fastDrawable
        } catch (_: Exception) {
            null
        }
    }
}
