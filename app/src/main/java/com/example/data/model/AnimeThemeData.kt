package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R

enum class AnimeCharacter(
    val id: String,
    val characterName: String,
    val heroTitle: String,
    val accentColorHex: String,
    val secondaryColorHex: String,
    val cardBgHex: String,
    @DrawableRes val imageResId: Int
) {
    LUFFY(
        id = "luffy",
        characterName = "Luffy",
        heroTitle = "LUFFY",
        accentColorHex = "#EF4444", // Red accent
        secondaryColorHex = "#F87171",
        cardBgHex = "#1E293B",
        imageResId = R.drawable.img_anime_luffy_1788767968720
    ),
    NARUTO(
        id = "naruto",
        characterName = "Naruto",
        heroTitle = "NARUTO",
        accentColorHex = "#F97316", // Orange accent
        secondaryColorHex = "#FB923C",
        cardBgHex = "#1E293B",
        imageResId = R.drawable.img_anime_naruto_1788767990980
    ),
    TANJIRO(
        id = "tanjiro",
        characterName = "Tanjiro",
        heroTitle = "TANJIRO",
        accentColorHex = "#10B981", // Emerald accent
        secondaryColorHex = "#34D399",
        cardBgHex = "#1E293B",
        imageResId = R.drawable.img_anime_tanjiro_1788768011791
    )
}

data class AnimeThemeState(
    val selectedCharacter: AnimeCharacter = AnimeCharacter.LUFFY,
    val isNavbarEnabled: Boolean = true,
    val selectedLanguage: String = "EN",
    val customCharacterUri: String? = null,
    val userName: String = "Chris Young",
    val userLocation: String = "Kecamatan Kemayoran, Indonesia"
)
