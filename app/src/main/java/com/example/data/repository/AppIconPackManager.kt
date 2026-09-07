package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.model.AppItem

object AppIconPackManager {

    /**
     * Map of keywords / package substrings to drawable resource IDs from the APP_ICON_ASSETS pack.
     */
    private val systemIconMap = mapOf(
        "phone" to R.drawable.ic_app_phone,
        "dialer" to R.drawable.ic_app_phone,
        "call" to R.drawable.ic_app_phone,

        "messages" to R.drawable.ic_app_messages,
        "messaging" to R.drawable.ic_app_messages,
        "mms" to R.drawable.ic_app_messages,
        "sms" to R.drawable.ic_app_messages,

        "browser" to R.drawable.ic_app_browser,
        "chrome" to R.drawable.ic_app_browser,
        "internet" to R.drawable.ic_app_browser,
        "web" to R.drawable.ic_app_browser,

        "camera" to R.drawable.ic_app_camera,

        "gallery" to R.drawable.ic_app_gallery,
        "photos" to R.drawable.ic_app_photos,

        "settings" to R.drawable.ic_app_settings,

        "calculator" to R.drawable.ic_app_calculator,
        "calc" to R.drawable.ic_app_calculator,

        "calendar" to R.drawable.ic_app_calendar,

        "clock" to R.drawable.ic_app_clock,
        "alarm" to R.drawable.ic_app_clock,

        "contacts" to R.drawable.ic_app_contacts,

        "files" to R.drawable.ic_app_files,
        "file" to R.drawable.ic_app_files,
        "documents" to R.drawable.ic_app_files,

        "music" to R.drawable.ic_app_music,
        "audio" to R.drawable.ic_app_music,

        "video" to R.drawable.ic_app_video,

        "youtube" to R.drawable.ic_app_youtube,

        "facebook" to R.drawable.ic_app_facebook,
        "instagram" to R.drawable.ic_app_instagram,
        "messenger" to R.drawable.ic_app_messenger,
        "telegram" to R.drawable.ic_app_telegram,
        "tiktok" to R.drawable.ic_app_tiktok,
        "whatsapp" to R.drawable.ic_app_whatsapp,

        "downloads" to R.drawable.ic_app_downloads,
        "email" to R.drawable.ic_app_email,
        "gmail" to R.drawable.ic_app_email,
        "maps" to R.drawable.ic_app_maps,
        "notes" to R.drawable.ic_app_notes,
        "weather" to R.drawable.ic_app_weather
    )

    fun getCustomIconRes(packageName: String, label: String): Int? {
        val pkg = packageName.lowercase()
        val lbl = label.lowercase()

        for ((key, resId) in systemIconMap) {
            if (pkg.contains(key) || lbl.contains(key)) {
                return resId
            }
        }
        return null
    }

    fun getCustomIconBitmap(context: Context, packageName: String, label: String): Bitmap? {
        val resId = getCustomIconRes(packageName, label) ?: return null
        return try {
            val drawable = ContextCompat.getDrawable(context, resId) ?: return null
            if (drawable is BitmapDrawable && drawable.bitmap != null) {
                return drawable.bitmap
            }
            val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 192
            val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 192
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    /**
     * All 37 asset icons in organized categories for display in Icon Pack Settings / Preview.
     */
    data class IconCategory(val title: String, val icons: List<Pair<String, Int>>)

    fun getAllAssetCategories(): List<IconCategory> {
        return listOf(
            IconCategory(
                "System Apps",
                listOf(
                    "Phone" to R.drawable.ic_app_phone,
                    "Messages" to R.drawable.ic_app_messages,
                    "Browser" to R.drawable.ic_app_browser,
                    "Camera" to R.drawable.ic_app_camera,
                    "Gallery" to R.drawable.ic_app_gallery,
                    "Settings" to R.drawable.ic_app_settings,
                    "Calculator" to R.drawable.ic_app_calculator,
                    "Calendar" to R.drawable.ic_app_calendar,
                    "Clock" to R.drawable.ic_app_clock,
                    "Contacts" to R.drawable.ic_app_contacts,
                    "Files" to R.drawable.ic_app_files
                )
            ),
            IconCategory(
                "Social Apps",
                listOf(
                    "Facebook" to R.drawable.ic_app_facebook,
                    "Instagram" to R.drawable.ic_app_instagram,
                    "Messenger" to R.drawable.ic_app_messenger,
                    "Telegram" to R.drawable.ic_app_telegram,
                    "TikTok" to R.drawable.ic_app_tiktok,
                    "WhatsApp" to R.drawable.ic_app_whatsapp
                )
            ),
            IconCategory(
                "Media & Entertainment",
                listOf(
                    "Music" to R.drawable.ic_app_music,
                    "Photos" to R.drawable.ic_app_photos,
                    "Video" to R.drawable.ic_app_video,
                    "YouTube" to R.drawable.ic_app_youtube
                )
            ),
            IconCategory(
                "Utilities",
                listOf(
                    "Downloads" to R.drawable.ic_app_downloads,
                    "Email" to R.drawable.ic_app_email,
                    "Maps" to R.drawable.ic_app_maps,
                    "Notes" to R.drawable.ic_app_notes,
                    "Weather" to R.drawable.ic_app_weather
                )
            ),
            IconCategory(
                "Navigation & Actions",
                listOf(
                    "App Drawer" to R.drawable.ic_app_drawer,
                    "Search" to R.drawable.ic_app_search,
                    "Back" to R.drawable.ic_app_back,
                    "Home" to R.drawable.ic_app_home,
                    "Recents" to R.drawable.ic_app_recent_apps
                )
            ),
            IconCategory(
                "Special Actions",
                listOf(
                    "Add" to R.drawable.ic_app_add,
                    "Lock" to R.drawable.ic_app_lock,
                    "Menu" to R.drawable.ic_app_menu,
                    "More" to R.drawable.ic_app_more,
                    "Remove" to R.drawable.ic_app_remove,
                    "Unlock" to R.drawable.ic_app_unlock
                )
            )
        )
    }

    /**
     * Set of standard built-in apps using the icon assets pack.
     */
    fun getBuiltInApps(context: Context): List<AppItem> {
        val appsData = listOf(
            Triple("com.nxv.phone", "Phone", R.drawable.ic_app_phone),
            Triple("com.nxv.messages", "Messages", R.drawable.ic_app_messages),
            Triple("com.nxv.browser", "Browser", R.drawable.ic_app_browser),
            Triple("com.nxv.camera", "Camera", R.drawable.ic_app_camera),
            Triple("com.nxv.gallery", "Gallery", R.drawable.ic_app_gallery),
            Triple("com.nxv.settings", "Settings", R.drawable.ic_app_settings),
            Triple("com.nxv.calculator", "Calculator", R.drawable.ic_app_calculator),
            Triple("com.nxv.calendar", "Calendar", R.drawable.ic_app_calendar),
            Triple("com.nxv.clock", "Clock", R.drawable.ic_app_clock),
            Triple("com.nxv.contacts", "Contacts", R.drawable.ic_app_contacts),
            Triple("com.nxv.files", "Files", R.drawable.ic_app_files),
            Triple("com.nxv.music", "Music", R.drawable.ic_app_music),
            Triple("com.nxv.photos", "Photos", R.drawable.ic_app_photos),
            Triple("com.nxv.video", "Videos", R.drawable.ic_app_video),
            Triple("com.nxv.youtube", "YouTube", R.drawable.ic_app_youtube),
            Triple("com.nxv.facebook", "Facebook", R.drawable.ic_app_facebook),
            Triple("com.nxv.instagram", "Instagram", R.drawable.ic_app_instagram),
            Triple("com.nxv.messenger", "Messenger", R.drawable.ic_app_messenger),
            Triple("com.nxv.telegram", "Telegram", R.drawable.ic_app_telegram),
            Triple("com.nxv.tiktok", "TikTok", R.drawable.ic_app_tiktok),
            Triple("com.nxv.whatsapp", "WhatsApp", R.drawable.ic_app_whatsapp),
            Triple("com.nxv.downloads", "Downloads", R.drawable.ic_app_downloads),
            Triple("com.nxv.email", "Email", R.drawable.ic_app_email),
            Triple("com.nxv.maps", "Maps", R.drawable.ic_app_maps),
            Triple("com.nxv.notes", "Notes", R.drawable.ic_app_notes),
            Triple("com.nxv.weather", "Weather", R.drawable.ic_app_weather)
        )

        return appsData.mapNotNull { (pkg, label, resId) ->
            val bitmap = try {
                val drawable = ContextCompat.getDrawable(context, resId) ?: return@mapNotNull null
                val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 192
                val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 192
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bmp)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            } catch (e: Exception) {
                null
            }

            AppItem(
                packageName = pkg,
                className = "$pkg.MainActivity",
                label = label,
                iconBitmap = bitmap,
                isSystemApp = true
            )
        }
    }

    /**
     * Handles launching built-in apps or fallback web/system intents.
     */
    fun launchBuiltInApp(context: Context, packageName: String): Boolean {
        if (!packageName.startsWith("com.nxv.")) return false

        try {
            val url = when (packageName) {
                "com.nxv.browser" -> "https://www.google.com"
                "com.nxv.youtube" -> "https://www.youtube.com"
                "com.nxv.facebook" -> "https://www.facebook.com"
                "com.nxv.instagram" -> "https://www.instagram.com"
                "com.nxv.messenger" -> "https://www.messenger.com"
                "com.nxv.telegram" -> "https://web.telegram.org"
                "com.nxv.tiktok" -> "https://www.tiktok.com"
                "com.nxv.whatsapp" -> "https://web.whatsapp.com"
                "com.nxv.maps" -> "https://maps.google.com"
                "com.nxv.weather" -> "https://weather.com"
                else -> null
            }

            if (url != null) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return true
            }

            // Standard system app fallback
            val systemIntent = when (packageName) {
                "com.nxv.phone" -> Intent(Intent.ACTION_DIAL)
                "com.nxv.camera" -> Intent("android.media.action.IMAGE_CAPTURE")
                "com.nxv.email" -> Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_EMAIL) }
                "com.nxv.calculator" -> Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_CALCULATOR) }
                "com.nxv.calendar" -> Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_CALENDAR) }
                "com.nxv.settings" -> Intent(android.provider.Settings.ACTION_SETTINGS)
                else -> null
            }

            if (systemIntent != null) {
                systemIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(systemIntent)
                return true
            }

            val appLabel = packageName.removePrefix("com.nxv.").replaceFirstChar { it.uppercase() }
            Toast.makeText(context, "Opening $appLabel...", Toast.LENGTH_SHORT).show()
            return true
        } catch (e: Exception) {
            val appLabel = packageName.removePrefix("com.nxv.").replaceFirstChar { it.uppercase() }
            Toast.makeText(context, "Opening $appLabel", Toast.LENGTH_SHORT).show()
            return true
        }
    }
}
