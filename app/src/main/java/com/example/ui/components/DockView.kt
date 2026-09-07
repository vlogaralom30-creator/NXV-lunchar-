package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.AppItem
import com.example.data.model.HomeItem
import com.example.data.model.IconShape

@Composable
fun DockView(
    dockItems: List<HomeItem>,
    appMap: Map<String, AppItem>,
    iconSize: Dp = 52.dp,
    iconShape: IconShape = IconShape.SQUIRCLE,
    showBackground: Boolean = true,
    backgroundColor: Color = Color(0xAA1E293B),
    enableAnimation: Boolean = true,
    onAppClick: (String) -> Unit,
    onAppLongClick: (HomeItem) -> Unit,
    onFolderClick: (HomeItem.Folder) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("launcher_dock")
    ) {
        val dockShape = RoundedCornerShape(28.dp)

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(dockShape)
                .then(
                    if (showBackground) Modifier.background(backgroundColor) else Modifier
                )
                .padding(vertical = 10.dp, horizontal = 12.dp)
        ) {
            dockItems.take(5).forEach { item ->
                when (item) {
                    is HomeItem.App -> {
                        val app = appMap[item.packageName]
                        val label = item.customLabel ?: app?.label ?: item.packageName
                        val icon = app?.iconBitmap

                        AppIconItem(
                            label = label,
                            iconBitmap = icon,
                            iconSize = iconSize,
                            iconShape = iconShape,
                            showLabel = false, // Dock usually shows icons only
                            enableAnimation = enableAnimation,
                            onClick = { onAppClick(item.packageName) },
                            onLongClick = { onAppLongClick(item) }
                        )
                    }
                    is HomeItem.Folder -> {
                        FolderIconPreview(
                            folder = item,
                            appMap = appMap,
                            iconSize = iconSize,
                            iconShape = iconShape,
                            showLabel = false,
                            onClick = { onFolderClick(item) },
                            onLongClick = { onAppLongClick(item) }
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}
