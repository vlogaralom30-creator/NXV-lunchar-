package com.example.ui.anime

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnimeCharacter
import com.example.data.model.AnimeThemeState
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AnimeHeroHeader(
    animeThemeState: AnimeThemeState,
    onOpenCharacterSheet: () -> Unit,
    onQuickNavClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val character = animeThemeState.selectedCharacter
    val accentColor = Color(android.graphics.Color.parseColor(character.accentColorHex))

    // Real-time clock & date
    var hourString by remember { mutableStateOf("18") }
    var minString by remember { mutableStateOf("29") }
    var dayOfWeekString by remember { mutableStateOf("MONDAY") }
    var dayNumString by remember { mutableStateOf("31") }
    var monthString by remember { mutableStateOf("AUGUST") }
    var yearString by remember { mutableStateOf("2026") }
    var greetingText by remember { mutableStateOf("Good EVENING") }

    LaunchedEffect(Unit) {
        while (true) {
            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            hourString = String.format(Locale.getDefault(), "%02d", hour)
            minString = String.format(Locale.getDefault(), "%02d", cal.get(Calendar.MINUTE))
            dayOfWeekString = SimpleDateFormat("EEEE", Locale.US).format(cal.time).uppercase()
            dayNumString = String.format(Locale.getDefault(), "%d", cal.get(Calendar.DAY_OF_MONTH))
            monthString = SimpleDateFormat("MMMM", Locale.US).format(cal.time).uppercase()
            yearString = String.format(Locale.getDefault(), "%d", cal.get(Calendar.YEAR))

            greetingText = when (hour) {
                in 5..11 -> "Good MORNING"
                in 12..16 -> "Good AFTERNOON"
                else -> "Good EVENING"
            }
            delay(1000L)
        }
    }

    // 3D Breathing Float Animation for character image
    val infiniteTransition = rememberInfiniteTransition(label = "anime_float")
    val floatOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Character Switcher Floating Pill Top Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Greeting & Status Pill
            Column {
                Text(
                    text = greetingText,
                    color = accentColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Cursive
                )
                Text(
                    text = "$dayOfWeekString",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$dayNumString DAY",
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "  |  ", color = Color.Gray, fontSize = 11.sp)
                    Text(
                        text = "$monthString MONTH",
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "  |  ", color = Color.Gray, fontSize = 11.sp)
                    Text(
                        text = "$yearString YEAR",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Cambiar Character Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF1E293B),
                                accentColor.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .border(1.dp, accentColor, RoundedCornerShape(24.dp))
                    .clickable { onOpenCharacterSheet() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Switch Character",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = character.characterName,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hero Anime Banner Canvas Box with Giant Name, Clock & 3D Character Overlap
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0F172A))
                .border(1.5.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
        ) {
            // Background Accent Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(accentColor)
                    .align(Alignment.TopCenter)
            )

            // Giant Character Stencil Title e.g. "LUFFY" / "NARUTO" / "TANJIRO"
            Text(
                text = character.heroTitle,
                color = Color.White.copy(alpha = 0.95f),
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                modifier = Modifier
                    .padding(start = 20.dp, top = 16.dp)
                    .align(Alignment.TopStart)
            )

            // Huge Digital Clock (Vertical Stack: Hour & Min)
            Column(
                modifier = Modifier
                    .padding(start = 24.dp, top = 65.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = hourString,
                    color = Color.White,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 60.sp
                )
                Text(
                    text = minString,
                    color = accentColor,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 60.sp
                )
            }

            // 3D Floating Anime Character Cutout Overlap
            Image(
                painter = painterResource(id = character.imageResId),
                contentDescription = character.characterName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(210.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = floatOffsetY.dp)
                    .shadow(16.dp, CircleShape, spotColor = accentColor)
            )

            // Red Stripe Accent Bar at bottom of card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(accentColor, Color.Black)
                        )
                    )
                    .align(Alignment.BottomCenter)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Nav Pills: CALENDAR, MUSIC, WEATHER, SETTINGS
        val quickItems = listOf(
            "CALENDAR" to "com.google.android.calendar",
            "MUSIC" to "com.google.android.apps.youtube.music",
            "WEATHER" to "com.google.android.apps.weather",
            "SETTINGS" to "com.android.settings"
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            quickItems.forEach { (label, pkg) ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(24.dp))
                        .clickable { onQuickNavClick(pkg) }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = label,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
