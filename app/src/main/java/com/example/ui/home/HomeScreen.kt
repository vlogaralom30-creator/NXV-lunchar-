package com.example.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.AppItem
import com.example.data.model.HomeItem
import com.example.data.model.ThemeConfig
import com.example.ui.animation.LauncherAnimationUtils
import com.example.ui.animation.LauncherAnimationUtils.homePageTransition
import com.example.ui.components.AppContextMenu
import com.example.ui.components.AppIconItem
import com.example.ui.components.ClockWidget
import com.example.ui.components.DockView
import com.example.ui.components.FolderDialog
import com.example.ui.components.FolderIconPreview
import com.example.ui.components.PageIndicator
import com.example.ui.components.SearchBarWidget
import com.example.ui.drawer.AppDrawerSheet
import com.example.ui.search.UniversalSearchSheet
import com.example.ui.theme.LauncherThemeEngine
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenSettings: () -> Unit
) {
    val themeConfig by viewModel.themeConfig.collectAsStateWithLifecycle()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    val recentApps by viewModel.recentApps.collectAsStateWithLifecycle()
    val homeGridItems by viewModel.homeGridItems.collectAsStateWithLifecycle()
    val dockItems by viewModel.dockItems.collectAsStateWithLifecycle()
    val appMap by viewModel.appMap.collectAsStateWithLifecycle()

    val animConfig = themeConfig.animationConfig
    val themeData = remember(themeConfig) { LauncherThemeEngine.getThemeData(themeConfig) }

    var showDrawer by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var selectedContextMenuFolder by remember { mutableStateOf<HomeItem.Folder?>(null) }
    var selectedContextMenuItem by remember { mutableStateOf<HomeItem?>(null) }

    val drawerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val pagerState = rememberPagerState(pageCount = { themeConfig.homePageCount })
    val coroutineScope = rememberCoroutineScope()

    // Vertical drag gesture state for smooth finger-following transitions
    val dragOffsetY = remember { Animatable(0f) }

    val handleLaunchApp = { packageName: String ->
        viewModel.launchApp(packageName)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeData.background)
            .pointerInput(animConfig.animationsEnabled) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            dragOffsetY.snapTo(dragOffsetY.value + dragAmount)
                        }
                    },
                    onDragEnd = {
                        coroutineScope.launch {
                            if (dragOffsetY.value < -120f) { // Swipe up -> Open Drawer
                                showDrawer = true
                            } else if (dragOffsetY.value > 120f) { // Swipe down -> Open Search
                                showSearch = true
                            }
                            dragOffsetY.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            dragOffsetY.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onOpenSettings()
                    },
                    onDoubleTap = {
                        showDrawer = true
                    }
                )
            }
            .testTag("home_screen_container")
    ) {
        // Wallpaper background with dynamic gesture scale / offset
        Image(
            painter = painterResource(id = R.drawable.img_nxv_wallpaper_1788744490502),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    if (animConfig.animationsEnabled) {
                        val dragScale = 1f + (abs(dragOffsetY.value) / 3000f).coerceIn(0f, 0.05f)
                        scaleX = dragScale
                        scaleY = dragScale
                    }
                }
        )

        // Dark overlay on top of wallpaper according to theme config
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = themeConfig.wallpaperOverlayDarkness))
        )

        // Home Screen Content Layout with smooth finger-following Y translation
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .graphicsLayer {
                    if (animConfig.animationsEnabled) {
                        translationY = dragOffsetY.value * 0.35f
                        alpha = 1f - (abs(dragOffsetY.value) / 1200f).coerceIn(0f, 0.3f)
                    }
                }
        ) {
            // Clock & Header near the top
            ClockWidget(
                style = themeConfig.clockStyle,
                showDate = themeConfig.clockShowDate,
                is24Hour = themeConfig.is24HourClock,
                textColor = Color.White,
                onClick = { onOpenSettings() }
            )

            // Search Bar Widget
            SearchBarWidget(
                backgroundColor = themeData.searchBarBackground,
                onClick = { showSearch = true },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            // Multi-Page Grid Horizontal Pager with customizable Page Transitions
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                val pageItems = homeGridItems.filter { it.position.pageIndex == pageIndex }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(themeConfig.gridConfig.cols),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .homePageTransition(pageIndex, pagerState, animConfig)
                ) {
                    items(pageItems, key = { it.id }) { item ->
                        when (item) {
                            is HomeItem.App -> {
                                val app = appMap[item.packageName]
                                val label = item.customLabel ?: app?.label ?: item.packageName
                                val icon = app?.iconBitmap

                                AppIconItem(
                                    label = label,
                                    iconBitmap = icon,
                                    iconSize = themeConfig.iconSizeDp.dp,
                                    iconShape = themeConfig.iconShape,
                                    showLabel = themeConfig.showAppLabels,
                                    labelSizeSp = themeConfig.appLabelSizeSp,
                                    labelColor = Color(android.graphics.Color.parseColor(themeConfig.appLabelColorHex)),
                                    enableAnimation = animConfig.animationsEnabled,
                                    onClick = { handleLaunchApp(item.packageName) },
                                    onLongClick = { selectedContextMenuItem = item }
                                )
                            }
                            is HomeItem.Folder -> {
                                FolderIconPreview(
                                    folder = item,
                                    appMap = appMap,
                                    iconSize = themeConfig.iconSizeDp.dp,
                                    iconShape = themeConfig.iconShape,
                                    showLabel = themeConfig.showAppLabels,
                                    labelSizeSp = themeConfig.appLabelSizeSp,
                                    labelColor = Color(android.graphics.Color.parseColor(themeConfig.appLabelColorHex)),
                                    onClick = { selectedContextMenuFolder = item },
                                    onLongClick = { selectedContextMenuItem = item }
                                )
                            }
                            else -> {}
                        }
                    }
                }
            }

            // Page Dots Indicator
            PageIndicator(
                pageCount = themeConfig.homePageCount,
                currentPage = pagerState.currentPage,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Persistent Bottom Dock
            DockView(
                dockItems = dockItems,
                appMap = appMap,
                iconSize = themeConfig.dockIconSizeDp.dp,
                iconShape = themeConfig.iconShape,
                showBackground = themeConfig.showDockBackground,
                backgroundColor = themeData.dockBackground,
                enableAnimation = animConfig.animationsEnabled,
                onAppClick = { packageName -> handleLaunchApp(packageName) },
                onAppLongClick = { item -> selectedContextMenuItem = item },
                onFolderClick = { folder -> selectedContextMenuFolder = folder }
            )
        }

        // App Drawer Sheet
        if (showDrawer) {
            AppDrawerSheet(
                installedApps = installedApps,
                recentApps = recentApps,
                iconShape = themeConfig.iconShape,
                sortOrder = themeConfig.appDrawerSortOrder,
                sheetState = drawerSheetState,
                animationConfig = animConfig,
                onDismiss = { showDrawer = false },
                onAppClick = { packageName ->
                    handleLaunchApp(packageName)
                    showDrawer = false
                },
                onAppLongClick = { app ->
                    viewModel.addAppToHome(app, pagerState.currentPage, 0, 0)
                    showDrawer = false
                },
                onToggleSortOrder = { viewModel.toggleSortOrder() }
            )
        }

        // Universal Search Sheet
        if (showSearch) {
            UniversalSearchSheet(
                installedApps = installedApps,
                recentApps = recentApps,
                iconShape = themeConfig.iconShape,
                sheetState = searchSheetState,
                animationConfig = animConfig,
                onDismiss = { showSearch = false },
                onAppClick = { packageName ->
                    handleLaunchApp(packageName)
                    showSearch = false
                }
            )
        }

        // Folder Dialog
        selectedContextMenuFolder?.let { folder ->
            FolderDialog(
                folder = folder,
                appMap = appMap,
                iconShape = themeConfig.iconShape,
                animationConfig = animConfig,
                onDismiss = { selectedContextMenuFolder = null },
                onAppClick = { packageName -> handleLaunchApp(packageName) },
                onRenameFolder = { newName -> viewModel.renameFolder(folder, newName) },
                onRemoveFromFolder = { packageName -> viewModel.removeAppFromFolder(folder, packageName) }
            )
        }

        // Context Menu Popup
        selectedContextMenuItem?.let { item ->
            val appItem = (item as? HomeItem.App)?.let { appMap[it.packageName] }
            AppContextMenu(
                item = item,
                appItem = appItem,
                onDismiss = { selectedContextMenuItem = null },
                onAppInfo = { packageName -> viewModel.openAppDetails(packageName) },
                onRemoveFromHome = { itemId -> viewModel.removeItemFromHome(itemId) },
                onUninstall = { packageName -> viewModel.requestUninstallApp(packageName) },
                onPinFavorite = { packageName -> viewModel.pinFavorite(packageName) }
            )
        }
    }
}
