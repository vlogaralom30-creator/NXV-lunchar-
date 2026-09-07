package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClockStyle
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockWidget(
    style: ClockStyle = ClockStyle.DIGITAL_CLEAN,
    showDate: Boolean = true,
    is24Hour: Boolean = false,
    textColor: Color = Color.White,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (style == ClockStyle.HIDDEN) return

    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000L)
        }
    }

    val timeFormatPattern = if (is24Hour) "HH:mm" else "h:mm"
    val timeFormat = remember(is24Hour) { SimpleDateFormat(timeFormatPattern, Locale.getDefault()) }
    val amPmFormat = remember { SimpleDateFormat("a", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }

    val formattedTime = timeFormat.format(currentTime)
    val formattedAmPm = if (!is24Hour) amPmFormat.format(currentTime) else ""
    val formattedDate = dateFormat.format(currentTime)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .testTag("clock_widget")
    ) {
        when (style) {
            ClockStyle.MODERN_BOLD -> {
                Text(
                    text = formattedTime,
                    color = textColor,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )
                if (showDate) {
                    Text(
                        text = formattedDate.uppercase(),
                        color = textColor.copy(alpha = 0.75f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            ClockStyle.MINIMAL_SLIM -> {
                Text(
                    text = if (formattedAmPm.isNotEmpty()) "$formattedTime $formattedAmPm" else formattedTime,
                    color = textColor,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.SansSerif
                )
                if (showDate) {
                    Text(
                        text = formattedDate,
                        color = textColor.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            else -> { // DIGITAL_CLEAN
                Text(
                    text = formattedTime,
                    color = textColor,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                if (showDate) {
                    Text(
                        text = formattedDate,
                        color = textColor.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
