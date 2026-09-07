package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnimationConfig
import com.example.data.model.AnimationPreset
import com.example.data.model.AnimationSpeed
import com.example.data.model.AppCloseStyle
import com.example.data.model.AppOpenStyle
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.Apps
import com.example.data.repository.AppIconPackManager
import com.example.data.model.ClockStyle
import com.example.data.model.DrawerAnimStyle
import com.example.data.model.FolderAnimStyle
import com.example.data.model.GridConfig
import com.example.data.model.IconShape
import com.example.data.model.PageTransitionStyle
import com.example.data.model.SearchAnimStyle
import com.example.data.model.ThemeConfig
import com.example.data.model.ThemePreset

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    themeConfig: ThemeConfig,
    onUpdateThemeConfig: ((ThemeConfig) -> ThemeConfig) -> Unit,
    onBack: () -> Unit
) {
    val anim = themeConfig.animationConfig

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NXV Launcher Settings",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        },
        containerColor = Color(0xFF0F172A),
        modifier = Modifier.testTag("settings_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preset Themes Section
            item {
                SettingsSectionHeader(title = "THEMES & PRESETS", icon = Icons.Default.Palette)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    ThemePreset.entries.forEach { preset ->
                        val isSelected = themeConfig.themeId == preset.themeId
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onUpdateThemeConfig { old ->
                                        old.copy(themeId = preset.themeId, themeName = preset.themeName)
                                    }
                                }
                        ) {
                            Text(
                                text = preset.themeName,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            // ANIMATION SYSTEM SECTION
            item {
                SettingsSectionHeader(title = "PREMIUM ANIMATIONS", icon = Icons.Default.AutoAwesome)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Master Animation Toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Master Animations", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Toggle all transitions and motion effects", color = Color.Gray, fontSize = 12.sp)
                            }
                            Switch(
                                checked = anim.animationsEnabled,
                                onCheckedChange = { enabled ->
                                    onUpdateThemeConfig { old ->
                                        old.copy(animationConfig = old.animationConfig.copy(animationsEnabled = enabled))
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                            )
                        }

                        if (anim.animationsEnabled) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Animation Presets
                            Text("Animation Preset", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                            ) {
                                AnimationPreset.entries.forEach { preset ->
                                    val isSelected = anim.preset == preset
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                                            .clickable {
                                                val newAnimConfig = preset.applyPreset(anim)
                                                onUpdateThemeConfig { old -> old.copy(animationConfig = newAnimConfig) }
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = preset.displayName,
                                            color = if (isSelected) Color.Black else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Speed Multiplier Selector
                            Text("Animation Speed", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                            ) {
                                AnimationSpeed.entries.forEach { speed ->
                                    val isSelected = anim.speed == speed
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                                            .clickable {
                                                onUpdateThemeConfig { old ->
                                                    old.copy(animationConfig = old.animationConfig.copy(speed = speed, preset = AnimationPreset.CUSTOM))
                                                }
                                            }
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = speed.displayName,
                                            color = if (isSelected) Color.Black else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // App Open Style
                            Text("App Open Transition", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                            ) {
                                AppOpenStyle.entries.forEach { style ->
                                    val isSelected = anim.appOpenStyle == style
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                                            .clickable {
                                                onUpdateThemeConfig { old ->
                                                    old.copy(animationConfig = old.animationConfig.copy(appOpenStyle = style, preset = AnimationPreset.CUSTOM))
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = style.displayName,
                                            color = if (isSelected) Color.Black else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // App Close Style
                            Text("App Return / Close Transition", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                            ) {
                                AppCloseStyle.entries.forEach { style ->
                                    val isSelected = anim.appCloseStyle == style
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                                            .clickable {
                                                onUpdateThemeConfig { old ->
                                                    old.copy(animationConfig = old.animationConfig.copy(appCloseStyle = style, preset = AnimationPreset.CUSTOM))
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = style.displayName,
                                            color = if (isSelected) Color.Black else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Page Transition Style
                            Text("Page Side-Scroll Style", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                            ) {
                                PageTransitionStyle.entries.forEach { style ->
                                    val isSelected = anim.pageTransitionStyle == style
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                                            .clickable {
                                                onUpdateThemeConfig { old ->
                                                    old.copy(animationConfig = old.animationConfig.copy(pageTransitionStyle = style, preset = AnimationPreset.CUSTOM))
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = style.displayName,
                                            color = if (isSelected) Color.Black else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Drawer Style
                            Text("App Drawer Animation", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                            ) {
                                DrawerAnimStyle.entries.forEach { style ->
                                    val isSelected = anim.drawerAnimStyle == style
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                                            .clickable {
                                                onUpdateThemeConfig { old ->
                                                    old.copy(animationConfig = old.animationConfig.copy(drawerAnimStyle = style, preset = AnimationPreset.CUSTOM))
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = style.displayName,
                                            color = if (isSelected) Color.Black else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Individual Motion Toggles
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Icon Touch Feedback", color = Color.White, fontSize = 13.sp)
                                Switch(
                                    checked = anim.enableIconPressFeedback,
                                    onCheckedChange = { check ->
                                        onUpdateThemeConfig { old ->
                                            old.copy(animationConfig = old.animationConfig.copy(enableIconPressFeedback = check))
                                        }
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Drag & Drop Spring Physics", color = Color.White, fontSize = 13.sp)
                                Switch(
                                    checked = anim.dragAnimEnabled,
                                    onCheckedChange = { check ->
                                        onUpdateThemeConfig { old ->
                                            old.copy(animationConfig = old.animationConfig.copy(dragAnimEnabled = check))
                                        }
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Theme Crossfade Animation", color = Color.White, fontSize = 13.sp)
                                Switch(
                                    checked = anim.themeTransitionEnabled,
                                    onCheckedChange = { check ->
                                        onUpdateThemeConfig { old ->
                                            old.copy(animationConfig = old.animationConfig.copy(themeTransitionEnabled = check))
                                        }
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                                )
                            }
                        }
                    }
                }
            }

            // Grid & Layout Section
            item {
                SettingsSectionHeader(title = "HOME GRID & LAYOUT", icon = Icons.Default.GridOn)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Grid Size", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            GridConfig.entries.forEach { grid ->
                                val isSelected = themeConfig.gridConfig == grid
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                                        .clickable {
                                            onUpdateThemeConfig { old -> old.copy(gridConfig = grid) }
                                        }
                                        .padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = grid.displayName,
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Home Pages Count: ${themeConfig.homePageCount}", color = Color.White)
                        Slider(
                            value = themeConfig.homePageCount.toFloat(),
                            onValueChange = { count ->
                                onUpdateThemeConfig { old -> old.copy(homePageCount = count.toInt()) }
                            },
                            valueRange = 1f..5f,
                            steps = 3,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF38BDF8),
                                activeTrackColor = Color(0xFF38BDF8)
                            )
                        )
                    }
                }
            }

            // Icons Customization
            item {
                SettingsSectionHeader(title = "ICON APPEARANCE", icon = Icons.Default.Brush)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Icon Shape", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            IconShape.entries.forEach { shape ->
                                val isSelected = themeConfig.iconShape == shape
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                                        .clickable {
                                            onUpdateThemeConfig { old -> old.copy(iconShape = shape) }
                                        }
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = shape.displayName.take(7),
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Icon Size: ${themeConfig.iconSizeDp} dp", color = Color.White)
                        Slider(
                            value = themeConfig.iconSizeDp.toFloat(),
                            onValueChange = { size ->
                                onUpdateThemeConfig { old -> old.copy(iconSizeDp = size.toInt()) }
                            },
                            valueRange = 40f..68f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF38BDF8),
                                activeTrackColor = Color(0xFF38BDF8)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Show App Labels", color = Color.White)
                            Switch(
                                checked = themeConfig.showAppLabels,
                                onCheckedChange = { show ->
                                    onUpdateThemeConfig { old -> old.copy(showAppLabels = show) }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                            )
                        }
                    }
                }
            }

            // Custom Icon Pack Assets (37 Icons)
            item {
                SettingsSectionHeader(title = "NXV ICON PACK ASSETS (37 ICONS)", icon = Icons.Default.Apps)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text("Loaded Custom App Icons", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("37 Custom asset icons enabled for system, social & media apps", color = Color.Gray, fontSize = 11.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF059669))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("37 ACTIVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val categories = AppIconPackManager.getAllAssetCategories()
                        categories.forEach { category ->
                            Text(
                                text = category.title,
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                category.icons.forEach { (name, resId) ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF334155))
                                            .padding(6.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = resId),
                                            contentDescription = name,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = name,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Dock Customization
            item {
                SettingsSectionHeader(title = "DOCK & BOTTOM BAR", icon = Icons.Default.Smartphone)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Show Dock Background", color = Color.White)
                            Switch(
                                checked = themeConfig.showDockBackground,
                                onCheckedChange = { show ->
                                    onUpdateThemeConfig { old -> old.copy(showDockBackground = show) }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Dock Icon Size: ${themeConfig.dockIconSizeDp} dp", color = Color.White)
                        Slider(
                            value = themeConfig.dockIconSizeDp.toFloat(),
                            onValueChange = { size ->
                                onUpdateThemeConfig { old -> old.copy(dockIconSizeDp = size.toInt()) }
                            },
                            valueRange = 40f..64f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF38BDF8),
                                activeTrackColor = Color(0xFF38BDF8)
                            )
                        )
                    }
                }
            }

            // Clock & Header Customization
            item {
                SettingsSectionHeader(title = "CLOCK & HEADER", icon = Icons.Default.Schedule)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Clock Style", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            ClockStyle.entries.forEach { style ->
                                val isSelected = themeConfig.clockStyle == style
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155))
                                        .clickable {
                                            onUpdateThemeConfig { old -> old.copy(clockStyle = style) }
                                        }
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = style.displayName.split(" ")[0],
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("24-Hour Time Format", color = Color.White)
                            Switch(
                                checked = themeConfig.is24HourClock,
                                onCheckedChange = { is24 ->
                                    onUpdateThemeConfig { old -> old.copy(is24HourClock = is24) }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                            )
                        }
                    }
                }
            }

            // Wallpaper & Background Overlay
            item {
                SettingsSectionHeader(title = "WALLPAPER & BLUR", icon = Icons.Default.Wallpaper)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Wallpaper Dark Overlay: ${(themeConfig.wallpaperOverlayDarkness * 100).toInt()}%", color = Color.White)
                        Slider(
                            value = themeConfig.wallpaperOverlayDarkness,
                            onValueChange = { dark ->
                                onUpdateThemeConfig { old -> old.copy(wallpaperOverlayDarkness = dark) }
                            },
                            valueRange = 0f..0.85f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF38BDF8),
                                activeTrackColor = Color(0xFF38BDF8)
                            )
                        )
                    }
                }
            }

            // About & Brand Section
            item {
                SettingsSectionHeader(title = "ABOUT NXV LAUNCHER", icon = Icons.Default.Info)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "NXV Launcher",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Version 1.0.0 • Production Build",
                            color = Color(0xFF38BDF8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Developed by Naxxivo. A lightweight, fast, offline-first Android HOME application designed for daily speed and clean customization.",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "naxxivo.online", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "naxxivo.xyz", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Privacy",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "100% Offline & Privacy-Focused • No Data Collected",
                                color = Color(0xFF10B981),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF38BDF8),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = Color(0xFF38BDF8),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )
    }
}
