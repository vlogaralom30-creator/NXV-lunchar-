package com.example.ui.animation

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.example.data.model.AnimationConfig
import com.example.data.model.PageTransitionStyle
import kotlin.math.abs

object LauncherAnimationUtils {

    /**
     * Checks if system reduced motion / animator scale is set to 0.
     */
    fun isSystemAnimationDisabled(context: Context): Boolean {
        return try {
            val scale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            scale == 0f
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Determines whether animations should actually run based on user preferences and system settings.
     */
    @Composable
    fun rememberAnimationsEnabled(config: AnimationConfig): Boolean {
        val context = LocalContext.current
        val systemDisabled = remember(context) { isSystemAnimationDisabled(context) }
        return config.animationsEnabled && !systemDisabled
    }

    /**
     * Scaled tween duration helper.
     */
    fun getScaledDuration(baseMs: Int, config: AnimationConfig): Int {
        if (!config.animationsEnabled) return 0
        return (baseMs * config.speed.multiplier).toInt().coerceAtLeast(1)
    }

    /**
     * Standard spring physics specs for NXV Launcher.
     */
    fun <T> responsiveSpring(config: AnimationConfig): FiniteAnimationSpec<T> {
        if (!config.animationsEnabled) return tween(0)
        return spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow / config.speed.multiplier
        )
    }

    fun <T> fastSpring(config: AnimationConfig): FiniteAnimationSpec<T> {
        if (!config.animationsEnabled) return tween(0)
        return spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh / config.speed.multiplier
        )
    }

    fun <T> gentleSpring(config: AnimationConfig): FiniteAnimationSpec<T> {
        if (!config.animationsEnabled) return tween(0)
        return spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow / config.speed.multiplier
        )
    }

    /**
     * Page transition graphics modifier for Home Screen HorizontalPager (Requirement 6).
     */
    fun Modifier.homePageTransition(
        pageIndex: Int,
        pagerState: PagerState,
        config: AnimationConfig
    ): Modifier = graphicsLayer {
        if (!config.animationsEnabled || !config.pageTransitionEnabled) return@graphicsLayer

        val pageOffset = (
            (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
        )
        val absOffset = abs(pageOffset)

        when (config.pageTransitionStyle) {
            PageTransitionStyle.SLIDE_SCALE -> {
                // Subtle slide + scale effect (0.96 to 1.0)
                val scale = 0.96f + (1f - absOffset.coerceIn(0f, 1f)) * 0.04f
                scaleX = scale
                scaleY = scale
                alpha = 0.6f + (1f - absOffset.coerceIn(0f, 1f)) * 0.4f
            }
            PageTransitionStyle.PARALLAX -> {
                translationX = pageOffset * size.width * 0.35f
                alpha = 1f - (absOffset * 0.3f).coerceIn(0f, 0.5f)
            }
            PageTransitionStyle.FADE_SLIDE -> {
                alpha = 1f - absOffset.coerceIn(0f, 1f)
                translationX = pageOffset * size.width * 0.15f
            }
            PageTransitionStyle.DEPTH -> {
                if (pageOffset > 0) { // Sliding off to the right
                    val scale = 0.88f + (1f - absOffset.coerceIn(0f, 1f)) * 0.12f
                    scaleX = scale
                    scaleY = scale
                    alpha = 1f - absOffset.coerceIn(0f, 1f)
                } else {
                    translationX = 0f
                    scaleX = 1f
                    scaleY = 1f
                    alpha = 1f
                }
            }
            PageTransitionStyle.STANDARD_SLIDE -> {
                // Standard horizontal slide with slight alpha fade at edges
                alpha = 0.85f + (1f - absOffset.coerceIn(0f, 1f)) * 0.15f
            }
        }
    }
}
