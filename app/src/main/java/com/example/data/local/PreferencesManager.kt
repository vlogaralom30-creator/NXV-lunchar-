package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AnimationConfig
import com.example.data.model.AnimationPreset
import com.example.data.model.AnimationSpeed
import com.example.data.model.AppCloseStyle
import com.example.data.model.AppMinimizeStyle
import com.example.data.model.AppOpenStyle
import com.example.data.model.AppSortOrder
import com.example.data.model.ClockStyle
import com.example.data.model.DrawerAnimStyle
import com.example.data.model.FolderAnimStyle
import com.example.data.model.GridConfig
import com.example.data.model.IconShape
import com.example.data.model.PageTransitionStyle
import com.example.data.model.SearchAnimStyle
import com.example.data.model.ThemeConfig
import com.example.data.model.ThemePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("nxv_launcher_prefs", Context.MODE_PRIVATE)

    private val _themeConfig = MutableStateFlow(loadThemeConfig())
    val themeConfig: StateFlow<ThemeConfig> = _themeConfig.asStateFlow()

    private fun loadThemeConfig(): ThemeConfig {
        val themeId = prefs.getString("theme_id", ThemePreset.DEFAULT_DARK.themeId) ?: ThemePreset.DEFAULT_DARK.themeId
        val gridName = prefs.getString("grid_config", GridConfig.GRID_4X6.name) ?: GridConfig.GRID_4X6.name
        val gridConfig = runCatching { GridConfig.valueOf(gridName) }.getOrDefault(GridConfig.GRID_4X6)

        val iconShapeName = prefs.getString("icon_shape", IconShape.SQUIRCLE.name) ?: IconShape.SQUIRCLE.name
        val iconShape = runCatching { IconShape.valueOf(iconShapeName) }.getOrDefault(IconShape.SQUIRCLE)

        val clockStyleName = prefs.getString("clock_style", ClockStyle.DIGITAL_CLEAN.name) ?: ClockStyle.DIGITAL_CLEAN.name
        val clockStyle = runCatching { ClockStyle.valueOf(clockStyleName) }.getOrDefault(ClockStyle.DIGITAL_CLEAN)

        val appSortName = prefs.getString("app_sort_order", AppSortOrder.ALPHABETICAL.name) ?: AppSortOrder.ALPHABETICAL.name
        val appSortOrder = runCatching { AppSortOrder.valueOf(appSortName) }.getOrDefault(AppSortOrder.ALPHABETICAL)

        val animConfig = AnimationConfig(
            animationsEnabled = prefs.getBoolean("anim_enabled", true),
            preset = runCatching { AnimationPreset.valueOf(prefs.getString("anim_preset", AnimationPreset.DEFAULT.name)!!) }.getOrDefault(AnimationPreset.DEFAULT),
            speed = runCatching { AnimationSpeed.valueOf(prefs.getString("anim_speed", AnimationSpeed.NORMAL.name)!!) }.getOrDefault(AnimationSpeed.NORMAL),
            appOpenEnabled = prefs.getBoolean("anim_app_open_enabled", true),
            appOpenStyle = runCatching { AppOpenStyle.valueOf(prefs.getString("anim_app_open_style", AppOpenStyle.SCALE_FADE.name)!!) }.getOrDefault(AppOpenStyle.SCALE_FADE),
            appCloseEnabled = prefs.getBoolean("anim_app_close_enabled", true),
            appCloseStyle = runCatching { AppCloseStyle.valueOf(prefs.getString("anim_app_close_style", AppCloseStyle.SCALE_OUT.name)!!) }.getOrDefault(AppCloseStyle.SCALE_OUT),
            appMinimizeStyle = runCatching { AppMinimizeStyle.valueOf(prefs.getString("anim_app_minimize_style", AppMinimizeStyle.SHRINK_HOME.name)!!) }.getOrDefault(AppMinimizeStyle.SHRINK_HOME),
            drawerAnimEnabled = prefs.getBoolean("anim_drawer_enabled", true),
            drawerAnimStyle = runCatching { DrawerAnimStyle.valueOf(prefs.getString("anim_drawer_style", DrawerAnimStyle.SLIDE_UP.name)!!) }.getOrDefault(DrawerAnimStyle.SLIDE_UP),
            pageTransitionEnabled = prefs.getBoolean("anim_page_enabled", true),
            pageTransitionStyle = runCatching { PageTransitionStyle.valueOf(prefs.getString("anim_page_style", PageTransitionStyle.SLIDE_SCALE.name)!!) }.getOrDefault(PageTransitionStyle.SLIDE_SCALE),
            searchAnimEnabled = prefs.getBoolean("anim_search_enabled", true),
            searchAnimStyle = runCatching { SearchAnimStyle.valueOf(prefs.getString("anim_search_style", SearchAnimStyle.EXPAND_SEARCH_BAR.name)!!) }.getOrDefault(SearchAnimStyle.EXPAND_SEARCH_BAR),
            folderAnimEnabled = prefs.getBoolean("anim_folder_enabled", true),
            folderAnimStyle = runCatching { FolderAnimStyle.valueOf(prefs.getString("anim_folder_style", FolderAnimStyle.EXPAND_FROM_ICON.name)!!) }.getOrDefault(FolderAnimStyle.EXPAND_FROM_ICON),
            dragAnimEnabled = prefs.getBoolean("anim_drag_enabled", true),
            enableIconPressFeedback = prefs.getBoolean("anim_icon_press_feedback", true),
            themeTransitionEnabled = prefs.getBoolean("anim_theme_transition_enabled", true)
        )

        return ThemeConfig(
            themeId = themeId,
            themeName = prefs.getString("theme_name", ThemePreset.DEFAULT_DARK.themeName) ?: ThemePreset.DEFAULT_DARK.themeName,
            gridConfig = gridConfig,
            iconSizeDp = prefs.getInt("icon_size_dp", 54),
            iconShape = iconShape,
            showAppLabels = prefs.getBoolean("show_app_labels", true),
            appLabelSizeSp = prefs.getInt("app_label_size_sp", 12),
            appLabelColorHex = prefs.getString("app_label_color_hex", "#FFFFFF") ?: "#FFFFFF",
            wallpaperType = prefs.getString("wallpaper_type", "DEFAULT") ?: "DEFAULT",
            wallpaperCustomUri = prefs.getString("wallpaper_custom_uri", null),
            wallpaperBlurRadius = prefs.getInt("wallpaper_blur_radius", 0),
            wallpaperOverlayDarkness = prefs.getFloat("wallpaper_overlay_darkness", 0.35f),
            dockIconSizeDp = prefs.getInt("dock_icon_size_dp", 52),
            showDockBackground = prefs.getBoolean("show_dock_background", true),
            dockOpacity = prefs.getFloat("dock_opacity", 0.85f),
            clockStyle = clockStyle,
            clockShowDate = prefs.getBoolean("clock_show_date", true),
            is24HourClock = prefs.getBoolean("is_24_hour_clock", false),
            appDrawerSortOrder = appSortOrder,
            searchEnableContacts = prefs.getBoolean("search_enable_contacts", false),
            animationSpeedMultiplier = animConfig.speed.multiplier,
            homePageCount = prefs.getInt("home_page_count", 2),
            animationConfig = animConfig
        )
    }

    fun updateThemeConfig(updateBlock: (ThemeConfig) -> ThemeConfig) {
        val updated = updateBlock(_themeConfig.value)
        _themeConfig.value = updated
        saveThemeConfig(updated)
    }

    private fun saveThemeConfig(config: ThemeConfig) {
        val anim = config.animationConfig
        prefs.edit()
            .putString("theme_id", config.themeId)
            .putString("theme_name", config.themeName)
            .putString("grid_config", config.gridConfig.name)
            .putInt("icon_size_dp", config.iconSizeDp)
            .putString("icon_shape", config.iconShape.name)
            .putBoolean("show_app_labels", config.showAppLabels)
            .putInt("app_label_size_sp", config.appLabelSizeSp)
            .putString("app_label_color_hex", config.appLabelColorHex)
            .putString("wallpaper_type", config.wallpaperType)
            .putString("wallpaper_custom_uri", config.wallpaperCustomUri)
            .putInt("wallpaper_blur_radius", config.wallpaperBlurRadius)
            .putFloat("wallpaper_overlay_darkness", config.wallpaperOverlayDarkness)
            .putInt("dock_icon_size_dp", config.dockIconSizeDp)
            .putBoolean("show_dock_background", config.showDockBackground)
            .putFloat("dock_opacity", config.dockOpacity)
            .putString("clock_style", config.clockStyle.name)
            .putBoolean("clock_show_date", config.clockShowDate)
            .putBoolean("is_24_hour_clock", config.is24HourClock)
            .putString("app_sort_order", config.appDrawerSortOrder.name)
            .putBoolean("search_enable_contacts", config.searchEnableContacts)
            .putFloat("animation_speed_multiplier", anim.speed.multiplier)
            .putInt("home_page_count", config.homePageCount)
            .putBoolean("anim_enabled", anim.animationsEnabled)
            .putString("anim_preset", anim.preset.name)
            .putString("anim_speed", anim.speed.name)
            .putBoolean("anim_app_open_enabled", anim.appOpenEnabled)
            .putString("anim_app_open_style", anim.appOpenStyle.name)
            .putBoolean("anim_app_close_enabled", anim.appCloseEnabled)
            .putString("anim_app_close_style", anim.appCloseStyle.name)
            .putString("anim_app_minimize_style", anim.appMinimizeStyle.name)
            .putBoolean("anim_drawer_enabled", anim.drawerAnimEnabled)
            .putString("anim_drawer_style", anim.drawerAnimStyle.name)
            .putBoolean("anim_page_enabled", anim.pageTransitionEnabled)
            .putString("anim_page_style", anim.pageTransitionStyle.name)
            .putBoolean("anim_search_enabled", anim.searchAnimEnabled)
            .putString("anim_search_style", anim.searchAnimStyle.name)
            .putBoolean("anim_folder_enabled", anim.folderAnimEnabled)
            .putString("anim_folder_style", anim.folderAnimStyle.name)
            .putBoolean("anim_drag_enabled", anim.dragAnimEnabled)
            .putBoolean("anim_icon_press_feedback", anim.enableIconPressFeedback)
            .putBoolean("anim_theme_transition_enabled", anim.themeTransitionEnabled)
            .apply()
    }
}
