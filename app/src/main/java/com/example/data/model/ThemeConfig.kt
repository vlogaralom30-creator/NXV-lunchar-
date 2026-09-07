package com.example.data.model

import androidx.compose.ui.graphics.Color

/**
 * Grid layout options supported by NXV Launcher.
 */
enum class GridConfig(val rows: Int, val cols: Int, val displayName: String) {
    GRID_4X5(5, 4, "4 x 5"),
    GRID_4X6(6, 4, "4 x 6"),
    GRID_5X5(5, 5, "5 x 5"),
    GRID_5X6(6, 5, "5 x 6"),
    GRID_6X6(6, 6, "6 x 6")
}

/**
 * Clock styles available for Home Screen header.
 */
enum class ClockStyle(val displayName: String) {
    DIGITAL_CLEAN("Digital Clean"),
    MODERN_BOLD("Modern Bold"),
    MINIMAL_SLIM("Minimal Slim"),
    HIDDEN("Hidden")
}

/**
 * Theme preset modes.
 */
enum class ThemePreset(val themeId: String, val themeName: String) {
    DEFAULT_DARK("nxv_dark", "NXV Obsidian Dark"),
    CLEAN_LIGHT("nxv_light", "NXV Crisp Light"),
    CYBER_NEON("nxv_neon", "Cyber Neon"),
    WARM_ORGANIC("nxv_organic", "Warm Slate")
}

/**
 * Complete Theme & Appearance Configuration model.
 */
data class ThemeConfig(
    val themeId: String = ThemePreset.DEFAULT_DARK.themeId,
    val themeName: String = ThemePreset.DEFAULT_DARK.themeName,
    val gridConfig: GridConfig = GridConfig.GRID_4X6,
    val iconSizeDp: Int = 54,
    val iconShape: IconShape = IconShape.SQUIRCLE,
    val showAppLabels: Boolean = true,
    val appLabelSizeSp: Int = 12,
    val appLabelColorHex: String = "#FFFFFF",
    val wallpaperType: String = "DEFAULT", // DEFAULT, SYSTEM, CUSTOM
    val wallpaperCustomUri: String? = null,
    val wallpaperBlurRadius: Int = 0,
    val wallpaperOverlayDarkness: Float = 0.35f,
    val dockIconSizeDp: Int = 52,
    val showDockBackground: Boolean = true,
    val dockOpacity: Float = 0.85f,
    val clockStyle: ClockStyle = ClockStyle.DIGITAL_CLEAN,
    val clockShowDate: Boolean = true,
    val is24HourClock: Boolean = false,
    val appDrawerSortOrder: AppSortOrder = AppSortOrder.ALPHABETICAL,
    val searchEnableContacts: Boolean = false,
    val animationSpeedMultiplier: Float = 1.0f,
    val homePageCount: Int = 2,
    val animationConfig: AnimationConfig = AnimationConfig()
)
