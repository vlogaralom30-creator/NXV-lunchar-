package com.example.ui.anime

import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AnimeCharacter
import com.example.data.model.AnimeThemeState
import com.example.data.repository.AppIconPackManager

/**
 * Common translucent glassmorphism widget container matching Anime design specs.
 */
@Composable
fun AnimeWidgetContainer(
    animeState: AnimeThemeState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val cornerRadiusDp = animeState.widgetCornerRadiusDp.dp
    val opacity = animeState.widgetOpacity
    val accentColor = Color(android.graphics.Color.parseColor(animeState.selectedCharacter.accentColorHex))

    val containerColor = when (animeState.widgetStyle) {
        "GLASS_NEON" -> Color(0xFF1E293B).copy(alpha = opacity)
        "MINIMAL_SOLID" -> Color(0xFF0F172A).copy(alpha = 0.95f)
        "CYBER_TRANSLUCENT" -> Color(0xFF020617).copy(alpha = (opacity * 0.75f).coerceAtLeast(0.4f))
        else -> Color(0xFF1E293B).copy(alpha = opacity)
    }

    val borderColor = when (animeState.widgetStyle) {
        "GLASS_NEON" -> accentColor.copy(alpha = 0.8f)
        "MINIMAL_SOLID" -> Color(0xFF334155)
        "CYBER_TRANSLUCENT" -> accentColor
        else -> accentColor
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(cornerRadiusDp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (animeState.widgetStyle == "GLASS_NEON" || animeState.widgetStyle == "CYBER_TRANSLUCENT") 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadiusDp)
            )
    ) {
        content()
    }
}

/**
 * Device Stats Card Widget
 */
@Composable
fun DeviceStatsCard(
    animeState: AnimeThemeState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accentColor = Color(android.graphics.Color.parseColor(animeState.selectedCharacter.accentColorHex))

    var batteryLevel by remember { mutableStateOf(84) }
    var storageText by remember { mutableStateOf("223 GB") }

    LaunchedEffect(Unit) {
        try {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            batteryLevel = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 84

            val stat = StatFs(Environment.getDataDirectory().path)
            val bytesAvailable = stat.availableBlocksLong * stat.blockSizeLong
            val freeGb = bytesAvailable / (1024 * 1024 * 1024)
            storageText = "$freeGb GB Free"
        } catch (_: Exception) {
            storageText = "223 GB"
        }
    }

    AnimeWidgetContainer(animeState = animeState, modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = "Device",
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DEVICE: ${Build.MODEL.ifEmpty { "VIVO V29" }}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ANDROID ${Build.VERSION.RELEASE}",
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    icon = Icons.Default.BatteryFull,
                    title = "BATTERY",
                    value = "$batteryLevel%",
                    accentColor = accentColor,
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = Icons.Default.SdCard,
                    title = "STORAGE",
                    value = storageText,
                    accentColor = accentColor,
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = Icons.Default.Memory,
                    title = "RAM / CPU",
                    value = "4.95 GB",
                    accentColor = accentColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(16.dp))
        Text(text = title, color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
    }
}

/**
 * User Profile & Weather Card Widget
 */
@Composable
fun ProfileWeatherCard(
    animeState: AnimeThemeState,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(android.graphics.Color.parseColor(animeState.selectedCharacter.accentColorHex))

    AnimeWidgetContainer(animeState = animeState, modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .border(2.dp, accentColor, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = animeState.selectedCharacter.imageResId),
                    contentDescription = "Profile Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animeState.userName,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = animeState.userLocation,
                    color = Color.LightGray,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Today's Weather 86°F Few clouds",
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Anime Character Mantra / Quote Widget
 */
@Composable
fun AnimeMantraQuoteWidget(
    animeState: AnimeThemeState,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(android.graphics.Color.parseColor(animeState.selectedCharacter.accentColorHex))

    val (quote, author) = when (animeState.selectedCharacter) {
        AnimeCharacter.LUFFY -> "If you don't take risks, you can't create a future!" to "Monkey D. Luffy"
        AnimeCharacter.NARUTO -> "I never go back on my word. That's my nindo: my ninja way!" to "Naruto Uzumaki"
        AnimeCharacter.TANJIRO -> "No matter how many people you lose, you have to go on living!" to "Tanjiro Kamado"
    }

    AnimeWidgetContainer(animeState = animeState, modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = "Mantra",
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "\"$quote\"",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "— $author",
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

/**
 * Vinyl Music Player Widget with Spinning Record
 */
@Composable
fun VinylMusicWidget(
    animeState: AnimeThemeState,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(android.graphics.Color.parseColor(animeState.selectedCharacter.accentColorHex))
    var isPlaying by remember { mutableStateOf(true) }

    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinyl_rotation"
    )

    AnimeWidgetContainer(animeState = animeState, modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_vinyl_record_1788768035546),
                    contentDescription = "Vinyl Disc",
                    modifier = Modifier
                        .size(64.dp)
                        .rotate(if (isPlaying) rotationAngle else 0f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Good Luck, Babe!",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Chappell Roan • Anime Edition",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { 0.42f },
                        color = accentColor,
                        trackColor = Color(0xFF334155),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Prev */ }) {
                    Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Prev", tint = Color.White)
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                        .clickable { isPlaying = !isPlaying },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { /* Next */ }) {
                    Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White)
                }
            }
        }
    }
}

/**
 * World Map Card Widget
 */
@Composable
fun WorldMapCard(
    animeState: AnimeThemeState,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(android.graphics.Color.parseColor(animeState.selectedCharacter.accentColorHex))

    AnimeWidgetContainer(animeState = animeState, modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxWidth()) {
                    val w = size.width
                    val h = size.height
                    drawCircle(color = accentColor.copy(alpha = 0.3f), radius = 24f, center = Offset(w * 0.4f, h * 0.5f))
                    drawCircle(color = accentColor, radius = 8f, center = Offset(w * 0.4f, h * 0.5f))
                    drawLine(color = accentColor, start = Offset(0f, h * 0.5f), end = Offset(w, h * 0.5f), strokeWidth = 2f)
                    drawLine(color = Color.Gray.copy(alpha = 0.3f), start = Offset(w * 0.5f, 0f), end = Offset(w * 0.5f, h), strokeWidth = 1.5f)
                }
                Text(
                    text = "Location: ${animeState.userLocation}",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
                )
            }
        }
    }
}

/**
 * Minimalist Icon Grid Container for Anime Theme
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimeIconGrid(
    onLaunchApp: (String) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customIcons = listOf(
        "WhatsApp" to ("com.whatsapp" to R.drawable.ic_app_whatsapp),
        "TikTok" to ("com.zhiliaoapp.musically" to R.drawable.ic_app_tiktok),
        "Instagram" to ("com.instagram.android" to R.drawable.ic_app_instagram),
        "Facebook" to ("com.facebook.katana" to R.drawable.ic_app_facebook),
        "Telegram" to ("org.telegram.messenger" to R.drawable.ic_app_telegram),
        "YouTube" to ("com.google.android.youtube" to R.drawable.ic_app_youtube),
        "Chrome" to ("com.android.chrome" to R.drawable.ic_app_browser),
        "Photos" to ("com.google.android.apps.photos" to R.drawable.ic_app_photos),
        "Camera" to ("com.android.camera" to R.drawable.ic_app_camera)
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            FlowRow(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                customIcons.forEach { (name, pair) ->
                    val (pkg, resId) = pair
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF334155))
                            .clickable {
                                if (!AppIconPackManager.launchBuiltInApp(context, pkg)) {
                                    val intent = context.packageManager.getLaunchIntentForPackage(pkg)
                                    if (intent != null) context.startActivity(intent)
                                    else onLaunchApp(pkg)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = name,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0284C7))
                        .clickable { onOpenDrawer() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = "Apps",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
