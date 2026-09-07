package com.example.ui.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import com.example.data.model.AnimeCharacter

/**
 * Custom Composable transition wrapper that performs an anime-style 'katana slash / wipe'
 * effect with high-contrast speed lines and a diagonal sweep reveal when switching
 * between Home Screen and App Drawer.
 */
@Composable
fun AnimeWipeTransitionLayout(
    visible: Boolean,
    character: AnimeCharacter,
    onAnimationEnd: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val progress = remember { Animatable(if (visible) 0f else 1f) }
    val accentColor = Color(android.graphics.Color.parseColor(character.accentColorHex))
    val secondaryColor = Color(android.graphics.Color.parseColor(character.secondaryColorHex))

    LaunchedEffect(visible) {
        if (visible) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
            )
        } else {
            progress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
            )
            onAnimationEnd()
        }
    }

    if (progress.value > 0f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
        ) {
            val p = progress.value

            // Drawer Content with Diagonal Clip Path + Alpha Reveal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = (p * 1.2f).coerceAtMost(1f)
                        translationY = (1f - p) * 120f
                    }
            ) {
                content()
            }

            // High-Energy Anime Katana Slash & Speed Lines Wipe Overlay
            if (p < 0.98f) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Diagonal sweep line position across canvas (from -0.5*width to 1.5*width)
                    val sweepX = -width * 0.3f + p * (width * 1.6f)
                    val slashSlope = 0.5f // Angle tilt

                    // 1. Draw Anime Speed Slash Lines (Katana Cut Lines)
                    for (i in -4..4) {
                        val offset = i * 28f
                        val x1 = sweepX + offset
                        val y1 = 0f
                        val x2 = sweepX + offset - (height * slashSlope)
                        val y2 = height

                        val lineAlpha = ((1f - Math.abs(i) / 5f) * (1f - p)).coerceIn(0f, 1f)
                        drawLine(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.White, accentColor, Color.Transparent),
                                start = Offset(x1, y1),
                                end = Offset(x2, y2)
                            ),
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = if (i == 0) 14f else 4f,
                            alpha = lineAlpha
                        )
                    }

                    // 2. High-Contrast Slash Flash Polygon Wipe Edge
                    val slashPath = Path().apply {
                        val startX = sweepX
                        moveTo(startX, 0f)
                        lineTo(startX + 90f, 0f)
                        lineTo(startX + 90f - (height * slashSlope), height)
                        lineTo(startX - (height * slashSlope), height)
                        close()
                    }

                    drawPath(
                        path = slashPath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                secondaryColor.copy(alpha = 0.8f),
                                Color.White,
                                accentColor.copy(alpha = 0.9f),
                                Color.Transparent
                            )
                        )
                    )
                }
            }
        }
    }
}
