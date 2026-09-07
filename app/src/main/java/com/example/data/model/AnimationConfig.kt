package com.example.data.model

/**
 * Animation System Presets
 */
enum class AnimationPreset(val displayName: String) {
    DEFAULT("Default"),
    MINIMAL("Minimal"),
    SMOOTH("Smooth"),
    DYNAMIC("Dynamic"),
    MATERIAL("Material"),
    FAST("Fast"),
    CUSTOM("Custom");

    fun applyPreset(current: AnimationConfig): AnimationConfig {
        return when (this) {
            DEFAULT -> current.copy(
                preset = DEFAULT,
                speed = AnimationSpeed.NORMAL,
                appOpenStyle = AppOpenStyle.SCALE_FADE,
                appCloseStyle = AppCloseStyle.SCALE_OUT,
                drawerAnimStyle = DrawerAnimStyle.SLIDE_UP,
                pageTransitionStyle = PageTransitionStyle.SLIDE_SCALE
            )
            MINIMAL -> current.copy(
                preset = MINIMAL,
                speed = AnimationSpeed.FAST,
                appOpenStyle = AppOpenStyle.FADE_IN,
                appCloseStyle = AppCloseStyle.FADE,
                drawerAnimStyle = DrawerAnimStyle.SLIDE_UP,
                pageTransitionStyle = PageTransitionStyle.STANDARD_SLIDE
            )
            SMOOTH -> current.copy(
                preset = SMOOTH,
                speed = AnimationSpeed.NORMAL,
                appOpenStyle = AppOpenStyle.ZOOM_IN,
                appCloseStyle = AppCloseStyle.ZOOM_OUT,
                drawerAnimStyle = DrawerAnimStyle.FADE_SLIDE_UP,
                pageTransitionStyle = PageTransitionStyle.PARALLAX
            )
            DYNAMIC -> current.copy(
                preset = DYNAMIC,
                speed = AnimationSpeed.NORMAL,
                appOpenStyle = AppOpenStyle.SLIDE_UP,
                appCloseStyle = AppCloseStyle.SLIDE_DOWN,
                drawerAnimStyle = DrawerAnimStyle.SCALE_FADE,
                pageTransitionStyle = PageTransitionStyle.DEPTH
            )
            MATERIAL -> current.copy(
                preset = MATERIAL,
                speed = AnimationSpeed.NORMAL,
                appOpenStyle = AppOpenStyle.SCALE_FADE,
                appCloseStyle = AppCloseStyle.SCALE_OUT,
                drawerAnimStyle = DrawerAnimStyle.SMOOTH_EXPAND,
                pageTransitionStyle = PageTransitionStyle.FADE_SLIDE
            )
            FAST -> current.copy(
                preset = FAST,
                speed = AnimationSpeed.FAST,
                appOpenStyle = AppOpenStyle.SCALE_IN,
                appCloseStyle = AppCloseStyle.SCALE_OUT,
                drawerAnimStyle = DrawerAnimStyle.SLIDE_UP,
                pageTransitionStyle = PageTransitionStyle.STANDARD_SLIDE
            )
            CUSTOM -> current.copy(preset = CUSTOM)
        }
    }
}

/**
 * Global Animation Speed Multipliers
 */
enum class AnimationSpeed(val displayName: String, val multiplier: Float) {
    SLOW("Slow (1.5x)", 1.5f),
    NORMAL("Normal (1.0x)", 1.0f),
    FAST("Fast (0.7x)", 0.7f)
}

/**
 * App Open Animation Styles
 */
enum class AppOpenStyle(val displayName: String) {
    SCALE_FADE("Scale + Fade"),
    FADE_IN("Fade In"),
    SCALE_IN("Scale In"),
    ZOOM_IN("Zoom In"),
    SLIDE_UP("Slide Up"),
    SLIDE_DOWN("Slide Down"),
    SLIDE_LEFT("Slide Left"),
    SLIDE_RIGHT("Slide Right")
}

/**
 * App Close / Return Animation Styles
 */
enum class AppCloseStyle(val displayName: String) {
    SCALE_OUT("Scale Out"),
    FADE("Fade"),
    ZOOM_OUT("Zoom Out"),
    SLIDE_DOWN("Slide Down"),
    SLIDE_UP("Slide Up"),
    SLIDE_LEFT("Slide Left"),
    SLIDE_RIGHT("Slide Right")
}

/**
 * App Minimize Animation Styles
 */
enum class AppMinimizeStyle(val displayName: String) {
    SHRINK_HOME("Shrink toward Home"),
    SCALE_DOWN("Scale Down"),
    FADE_SCALE("Fade + Scale"),
    SHRINK_DOCK("Shrink toward Dock"),
    SLIDE_DOCK("Slide toward Dock")
}

/**
 * App Drawer Animation Styles
 */
enum class DrawerAnimStyle(val displayName: String) {
    SLIDE_UP("Slide Up"),
    FADE_SLIDE_UP("Fade + Slide Up"),
    SCALE_FADE("Scale + Fade"),
    SMOOTH_EXPAND("Smooth Expand"),
    BLUR_TO_CLEAR("Blur-to-Clear")
}

/**
 * Home Page Side-Scroll Transition Styles
 */
enum class PageTransitionStyle(val displayName: String) {
    SLIDE_SCALE("Subtle Slide + Scale"),
    STANDARD_SLIDE("Standard Horizontal Slide"),
    PARALLAX("Parallax"),
    FADE_SLIDE("Fade + Slide"),
    DEPTH("Depth Style")
}

/**
 * Search Bar / Sheet Animation Styles
 */
enum class SearchAnimStyle(val displayName: String) {
    EXPAND_SEARCH_BAR("Expand from Search Bar"),
    SLIDE_DOWN("Slide Down"),
    FADE_IN("Fade In"),
    SCALE_FADE("Scale + Fade")
}

/**
 * Folder Opening Animation Styles
 */
enum class FolderAnimStyle(val displayName: String) {
    EXPAND_FROM_ICON("Icon-to-Folder Expansion"),
    SCALE_UP("Scale Up"),
    FADE_IN("Fade In"),
    BACKGROUND_FADE("Background Fade")
}

/**
 * Complete Animation Configuration data class for NXV Launcher.
 */
data class AnimationConfig(
    val animationsEnabled: Boolean = true,
    val preset: AnimationPreset = AnimationPreset.DEFAULT,
    val speed: AnimationSpeed = AnimationSpeed.NORMAL,
    val appOpenEnabled: Boolean = true,
    val appOpenStyle: AppOpenStyle = AppOpenStyle.SCALE_FADE,
    val appCloseEnabled: Boolean = true,
    val appCloseStyle: AppCloseStyle = AppCloseStyle.SCALE_OUT,
    val appMinimizeStyle: AppMinimizeStyle = AppMinimizeStyle.SHRINK_HOME,
    val drawerAnimEnabled: Boolean = true,
    val drawerAnimStyle: DrawerAnimStyle = DrawerAnimStyle.SLIDE_UP,
    val pageTransitionEnabled: Boolean = true,
    val pageTransitionStyle: PageTransitionStyle = PageTransitionStyle.SLIDE_SCALE,
    val searchAnimEnabled: Boolean = true,
    val searchAnimStyle: SearchAnimStyle = SearchAnimStyle.EXPAND_SEARCH_BAR,
    val folderAnimEnabled: Boolean = true,
    val folderAnimStyle: FolderAnimStyle = FolderAnimStyle.EXPAND_FROM_ICON,
    val dragAnimEnabled: Boolean = true,
    val enableIconPressFeedback: Boolean = true,
    val themeTransitionEnabled: Boolean = true
)
