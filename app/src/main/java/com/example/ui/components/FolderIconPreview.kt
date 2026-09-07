package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppItem
import com.example.data.model.HomeItem
import com.example.data.model.IconShape
import com.example.ui.theme.LauncherThemeEngine

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderIconPreview(
    folder: HomeItem.Folder,
    appMap: Map<String, AppItem>,
    iconSize: Dp = 52.dp,
    iconShape: IconShape = IconShape.SQUIRCLE,
    showLabel: Boolean = true,
    labelSizeSp: Int = 12,
    labelColor: Color = Color.White,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy, stiffness = androidx.compose.animation.core.Spring.StiffnessHigh),
        label = "folderScale"
    )

    val folderShape = LauncherThemeEngine.getShapeForIcon(iconShape)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .scale(scaleAnim)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(4.dp)
            .testTag("folder_preview_${folder.name.lowercase()}")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(iconSize)
                .clip(folderShape)
                .background(Color.White.copy(alpha = 0.15f))
                .padding(6.dp)
        ) {
            val previewApps = folder.appPackages.mapNotNull { appMap[it] }.take(4)

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.size(iconSize)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.weight(1f)
                ) {
                    previewApps.getOrNull(0)?.let { MiniIcon(it, iconSize / 2.5f) }
                    previewApps.getOrNull(1)?.let { MiniIcon(it, iconSize / 2.5f) }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.weight(1f)
                ) {
                    previewApps.getOrNull(2)?.let { MiniIcon(it, iconSize / 2.5f) }
                    previewApps.getOrNull(3)?.let { MiniIcon(it, iconSize / 2.5f) }
                }
            }
        }

        if (showLabel) {
            Text(
                text = folder.name,
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

@Composable
private fun MiniIcon(app: AppItem, size: Dp) {
    if (app.iconBitmap != null) {
        Image(
            bitmap = app.iconBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(4.dp))
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF475569))
        )
    }
}
