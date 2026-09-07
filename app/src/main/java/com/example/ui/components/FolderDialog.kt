package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnimationConfig
import com.example.data.model.AppItem
import com.example.data.model.HomeItem
import com.example.data.model.IconShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDialog(
    folder: HomeItem.Folder,
    appMap: Map<String, AppItem>,
    iconShape: IconShape,
    animationConfig: AnimationConfig = AnimationConfig(),
    onDismiss: () -> Unit,
    onAppClick: (String) -> Unit,
    onRenameFolder: (String) -> Unit,
    onRemoveFromFolder: (String) -> Unit
) {
    var isEditingName by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(folder.name) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("folder_dialog")
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow / animationConfig.speed.multiplier
                ),
                initialScale = 0.7f
            ) + fadeIn(),
            exit = scaleOut(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessHigh
                ),
                targetScale = 0.7f
            ) + fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color(0xEE1E293B),
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isEditingName) {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF38BDF8),
                                    unfocusedBorderColor = Color.Gray
                                ),
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            )
                            TextButton(onClick = {
                                if (nameInput.isNotBlank()) {
                                    onRenameFolder(nameInput.trim())
                                }
                                isEditingName = false
                            }) {
                                Text("Done", color = Color(0xFF38BDF8))
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isEditingName = true }
                            ) {
                                Text(
                                    text = folder.name,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Rename",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val folderApps = folder.appPackages.mapNotNull { appMap[it] }

                    if (folderApps.isEmpty()) {
                        Text(
                            text = "Folder is empty",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 24.dp).align(Alignment.CenterHorizontally)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().height(260.dp)
                        ) {
                            items(folderApps, key = { it.packageName }) { app ->
                                AppIconItem(
                                    label = app.label,
                                    iconBitmap = app.iconBitmap,
                                    iconSize = 48.dp,
                                    iconShape = iconShape,
                                    showLabel = true,
                                    labelColor = Color.White,
                                    enableAnimation = animationConfig.animationsEnabled,
                                    onClick = {
                                        onAppClick(app.packageName)
                                        onDismiss()
                                    },
                                    onLongClick = {
                                        onRemoveFromFolder(app.packageName)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
