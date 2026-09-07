package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppItem
import com.example.data.model.HomeItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContextMenu(
    item: HomeItem,
    appItem: AppItem?,
    onDismiss: () -> Unit,
    onAppInfo: (String) -> Unit,
    onRemoveFromHome: (String) -> Unit,
    onUninstall: (String) -> Unit,
    onPinFavorite: (String) -> Unit
) {
    val packageName = when (item) {
        is HomeItem.App -> item.packageName
        else -> ""
    }
    val label = when (item) {
        is HomeItem.App -> item.customLabel ?: appItem?.label ?: item.packageName
        is HomeItem.Folder -> item.name
        is HomeItem.Widget -> "Widget"
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("app_context_menu")
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1E293B),
            tonalElevation = 8.dp,
            modifier = Modifier.width(280.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
                )

                if (packageName.isNotEmpty()) {
                    ContextMenuItem(
                        icon = Icons.Default.Pin,
                        label = "Pin to Favorites",
                        tint = Color(0xFF38BDF8),
                        onClick = {
                            onPinFavorite(packageName)
                            onDismiss()
                        }
                    )
                    ContextMenuItem(
                        icon = Icons.Default.Info,
                        label = "App Info",
                        tint = Color.White,
                        onClick = {
                            onAppInfo(packageName)
                            onDismiss()
                        }
                    )
                }

                ContextMenuItem(
                    icon = Icons.Default.RemoveCircle,
                    label = "Remove from Home",
                    tint = Color(0xFFF1F5F9),
                    onClick = {
                        onRemoveFromHome(item.id)
                        onDismiss()
                    }
                )

                if (packageName.isNotEmpty()) {
                    ContextMenuItem(
                        icon = Icons.Default.Delete,
                        label = "Uninstall",
                        tint = Color(0xFFEF4444),
                        onClick = {
                            onUninstall(packageName)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContextMenuItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
