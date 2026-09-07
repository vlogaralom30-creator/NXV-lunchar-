package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IconShape
import com.example.ui.theme.LauncherThemeEngine

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconItem(
    label: String,
    iconBitmap: Bitmap?,
    iconSize: Dp = 52.dp,
    iconShape: IconShape = IconShape.SQUIRCLE,
    showLabel: Boolean = true,
    labelSizeSp: Int = 12,
    labelColor: Color = Color.White,
    enableAnimation: Boolean = true,
    isDragging: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Icon press & drag scale animation
    val targetScale = when {
        !enableAnimation -> 1.0f
        isDragging -> 1.08f
        isPressed -> 0.96f
        else -> 1.0f
    }

    val scaleAnim by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = if (isDragging) Spring.DampingRatioMediumBouncy else Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "iconScale"
    )

    val rotationAnim by animateFloatAsState(
        targetValue = if (isDragging && enableAnimation) 2.5f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "iconRotation"
    )

    val elevationDp = if (isDragging && enableAnimation) 12.dp else 0.dp

    val shape = LauncherThemeEngine.getShapeForIcon(iconShape)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .scale(scaleAnim)
            .rotate(rotationAnim)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(4.dp)
            .testTag("app_icon_${label.lowercase().replace(" ", "_")}")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(iconSize)
                .shadow(elevationDp, shape)
                .clip(shape)
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap.asImageBitmap(),
                    contentDescription = label,
                    modifier = Modifier
                        .size(iconSize)
                        .clip(shape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(shape)
                        .background(Color(0xFF334155)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label.take(1).uppercase(),
                        color = Color.White,
                        fontSize = (iconSize.value * 0.45f).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (showLabel) {
            Text(
                text = label,
                color = labelColor,
                fontSize = labelSizeSp.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
