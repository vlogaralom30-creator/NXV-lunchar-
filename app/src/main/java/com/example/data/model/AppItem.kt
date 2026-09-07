package com.example.data.model

import android.graphics.Bitmap

/**
 * Represents an installed launchable application on the Android device.
 */
data class AppItem(
    val packageName: String,
    val className: String = "",
    val label: String,
    val iconBitmap: Bitmap? = null,
    val installTime: Long = 0L,
    val lastUsedTime: Long = System.currentTimeMillis(),
    val launchCount: Int = 0,
    val isSystemApp: Boolean = false
) {
    val id: String
        get() = if (className.isNotEmpty()) "$packageName/$className" else packageName
}

/**
 * Shape variants for app icons.
 */
enum class IconShape(val displayName: String) {
    CIRCLE("Circle"),
    SQUIRCLE("Squircle"),
    ROUNDED_CORNER("Rounded Square"),
    TEARDROP("Teardrop"),
    HEXAGON("Hexagon")
}

/**
 * App Drawer sorting options.
 */
enum class AppSortOrder(val displayName: String) {
    ALPHABETICAL("Alphabetical (A-Z)"),
    RECENTLY_USED("Recently Used"),
    MOST_USED("Most Used")
}
