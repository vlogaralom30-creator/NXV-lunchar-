package com.example.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.data.model.IconShape
import com.example.data.model.ThemeConfig
import com.example.data.model.ThemePreset

data class NxvLauncherThemeData(
    val themeId: String,
    val themeName: String,
    val colorScheme: ColorScheme,
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val accent: Color,
    val dockBackground: Color,
    val searchBarBackground: Color,
    val folderBackground: Color,
    val iconShape: Shape
)

object LauncherThemeEngine {

    fun getShapeForIcon(iconShape: IconShape): Shape {
        return when (iconShape) {
            IconShape.CIRCLE -> CircleShape
            IconShape.SQUIRCLE -> RoundedCornerShape(22.dp)
            IconShape.ROUNDED_CORNER -> RoundedCornerShape(12.dp)
            IconShape.TEARDROP -> RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomEnd = 24.dp, bottomStart = 6.dp)
            IconShape.HEXAGON -> RoundedCornerShape(16.dp)
        }
    }

    fun getThemeData(config: ThemeConfig): NxvLauncherThemeData {
        val iconShape = getShapeForIcon(config.iconShape)

        return when (config.themeId) {
            ThemePreset.CLEAN_LIGHT.themeId -> {
                val bg = Color(0xFFF8FAFC)
                val surface = Color(0xFFFFFFFF)
                val primaryText = Color(0xFF0F172A)
                val secondaryText = Color(0xFF64748B)
                val accent = Color(0xFF2563EB)

                NxvLauncherThemeData(
                    themeId = config.themeId,
                    themeName = "NXV Crisp Light",
                    colorScheme = lightColorScheme(
                        primary = accent,
                        surface = surface,
                        background = bg,
                        onBackground = primaryText,
                        onSurface = primaryText
                    ),
                    isDark = false,
                    background = bg,
                    surface = surface,
                    primaryText = primaryText,
                    secondaryText = secondaryText,
                    accent = accent,
                    dockBackground = Color(0xCCFFFFFF),
                    searchBarBackground = Color(0xFFE2E8F0),
                    folderBackground = Color(0xEEF1F5F9),
                    iconShape = iconShape
                )
            }
            ThemePreset.CYBER_NEON.themeId -> {
                val bg = Color(0xFF09090B)
                val surface = Color(0xFF18181B)
                val primaryText = Color(0xFFFAFAFA)
                val secondaryText = Color(0xFFA1A1AA)
                val accent = Color(0xFF06B6D4)

                NxvLauncherThemeData(
                    themeId = config.themeId,
                    themeName = "Cyber Neon",
                    colorScheme = darkColorScheme(
                        primary = accent,
                        surface = surface,
                        background = bg,
                        onBackground = primaryText,
                        onSurface = primaryText
                    ),
                    isDark = true,
                    background = bg,
                    surface = surface,
                    primaryText = primaryText,
                    secondaryText = secondaryText,
                    accent = accent,
                    dockBackground = Color(0xBB18181B),
                    searchBarBackground = Color(0xFF27272A),
                    folderBackground = Color(0xDD27272A),
                    iconShape = iconShape
                )
            }
            ThemePreset.WARM_ORGANIC.themeId -> {
                val bg = Color(0xFF1C1917)
                val surface = Color(0xFF292524)
                val primaryText = Color(0xFFF5F5F4)
                val secondaryText = Color(0xFFA8A29E)
                val accent = Color(0xFFF97316)

                NxvLauncherThemeData(
                    themeId = config.themeId,
                    themeName = "Warm Slate",
                    colorScheme = darkColorScheme(
                        primary = accent,
                        surface = surface,
                        background = bg,
                        onBackground = primaryText,
                        onSurface = primaryText
                    ),
                    isDark = true,
                    background = bg,
                    surface = surface,
                    primaryText = primaryText,
                    secondaryText = secondaryText,
                    accent = accent,
                    dockBackground = Color(0xCC292524),
                    searchBarBackground = Color(0xFF44403C),
                    folderBackground = Color(0xDD44403C),
                    iconShape = iconShape
                )
            }
            else -> { // DEFAULT DARK
                val bg = Color(0xFF0F172A)
                val surface = Color(0xFF1E293B)
                val primaryText = Color(0xFFF8FAFC)
                val secondaryText = Color(0xFF94A3B8)
                val accent = Color(0xFF38BDF8)

                NxvLauncherThemeData(
                    themeId = ThemePreset.DEFAULT_DARK.themeId,
                    themeName = "NXV Obsidian Dark",
                    colorScheme = darkColorScheme(
                        primary = accent,
                        surface = surface,
                        background = bg,
                        onBackground = primaryText,
                        onSurface = primaryText
                    ),
                    isDark = true,
                    background = bg,
                    surface = surface,
                    primaryText = primaryText,
                    secondaryText = secondaryText,
                    accent = accent,
                    dockBackground = Color(0xAA1E293B),
                    searchBarBackground = Color(0xFF334155),
                    folderBackground = Color(0xDD1E293B),
                    iconShape = iconShape
                )
            }
        }
    }
}
