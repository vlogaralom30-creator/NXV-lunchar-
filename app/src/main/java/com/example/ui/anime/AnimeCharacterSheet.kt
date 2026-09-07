package com.example.ui.anime

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnimeCharacter
import com.example.data.model.AnimeThemeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeCharacterSheet(
    animeThemeState: AnimeThemeState,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSelectCharacter: (AnimeCharacter) -> Unit,
    onToggleNavbar: (Boolean) -> Unit,
    onSelectLanguage: (String) -> Unit
) {
    val activeCharacter = animeThemeState.selectedCharacter
    val accentColor = Color(android.graphics.Color.parseColor(activeCharacter.accentColorHex))

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F172A),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Header
            Text(
                text = "Cambiar El Personaje",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Character Avatars Row (Luffy, Naruto, Tanjiro)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnimeCharacter.entries.forEach { charItem ->
                    val isSelected = activeCharacter == charItem
                    val itemAccent = Color(android.graphics.Color.parseColor(charItem.accentColorHex))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onSelectCharacter(charItem) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) itemAccent else Color(0xFF334155),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = charItem.imageResId),
                                contentDescription = charItem.characterName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(itemAccent)
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = charItem.characterName,
                            color = if (isSelected) itemAccent else Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navbar E Idioma Section
            Text(
                text = "Navbar E Idioma :",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Navbar Toggle Button
            Button(
                onClick = { onToggleNavbar(!animeThemeState.isNavbarEnabled) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (animeThemeState.isNavbarEnabled) Color(0xFF059669) else Color(0xFFDC2626)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(44.dp)
            ) {
                Text(
                    text = if (animeThemeState.isNavbarEnabled) "NAVBAR ON" else "NAVBAR OFF",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Language Selector Flags Row
            val languages = listOf("EN" to "🇬🇧", "ID" to "🇮🇩", "ES" to "🇪🇸", "DE" to "🇩🇪", "PT" to "🇵🇹")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                languages.forEach { (lang, flag) ->
                    val isLangSelected = animeThemeState.selectedLanguage == lang
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isLangSelected) accentColor else Color(0xFF1E293B))
                            .clickable { onSelectLanguage(lang) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$flag $lang",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Close / Apply Button
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(
                    text = "APPLY ANIME THEME",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
