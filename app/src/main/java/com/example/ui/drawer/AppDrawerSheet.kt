package com.example.ui.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnimationConfig
import com.example.data.model.AnimeThemeState
import com.example.data.model.AppItem
import com.example.data.model.AppSortOrder
import com.example.data.model.IconShape
import com.example.ui.components.AppIconItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerSheet(
    installedApps: List<AppItem>,
    recentApps: List<AppItem>,
    iconShape: IconShape,
    sortOrder: AppSortOrder,
    sheetState: SheetState,
    animationConfig: AnimationConfig = AnimationConfig(),
    animeThemeState: AnimeThemeState? = null,
    onDismiss: () -> Unit,
    onAppClick: (String) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    onToggleSortOrder: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(searchQuery, installedApps) {
        if (searchQuery.isBlank()) {
            installedApps
        } else {
            installedApps.filter {
                it.label.contains(searchQuery, ignoreCase = true) ||
                        it.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFB0F172A),
        scrimColor = Color.Black.copy(alpha = 0.6f),
        modifier = Modifier.testTag("app_drawer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header Search Bar in Drawer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search installed apps...", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF38BDF8)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .clickable { onToggleSortOrder() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort Order",
                        tint = Color(0xFF38BDF8)
                    )
                }
            }

            // Recent Apps row if search is empty and recent apps available
            if (searchQuery.isBlank() && recentApps.isNotEmpty()) {
                Text(
                    text = "RECENTLY USED",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    recentApps.take(5).forEach { app ->
                        AppIconItem(
                            label = app.label,
                            iconBitmap = app.iconBitmap,
                            packageName = app.packageName,
                            animeCharacter = animeThemeState?.selectedCharacter,
                            isAnimeIconPackEnabled = animeThemeState?.isAnimeIconPackEnabled ?: false,
                            iconSize = 44.dp,
                            iconShape = iconShape,
                            showLabel = true,
                            labelSizeSp = 10,
                            labelColor = Color.LightGray,
                            enableAnimation = animationConfig.animationsEnabled,
                            onClick = {
                                onAppClick(app.packageName)
                                onDismiss()
                            },
                            onLongClick = { onAppLongClick(app) }
                        )
                    }
                }
            }

            // All Apps Count Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
            ) {
                Text(
                    text = if (searchQuery.isBlank()) "ALL APPLICATIONS (${filteredApps.size})" else "SEARCH RESULTS (${filteredApps.size})",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = sortOrder.displayName,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }

            // Apps Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize().padding(top = 8.dp)
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    AppIconItem(
                        label = app.label,
                        iconBitmap = app.iconBitmap,
                        packageName = app.packageName,
                        animeCharacter = animeThemeState?.selectedCharacter,
                        isAnimeIconPackEnabled = animeThemeState?.isAnimeIconPackEnabled ?: false,
                        iconSize = 52.dp,
                        iconShape = iconShape,
                        showLabel = true,
                        labelColor = Color.White,
                        enableAnimation = animationConfig.animationsEnabled,
                        onClick = {
                            onAppClick(app.packageName)
                            onDismiss()
                        },
                        onLongClick = { onAppLongClick(app) }
                    )
                }
            }
        }
    }
}
