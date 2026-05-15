package com.example.kavyakanaja.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kavyakanaja.data.models.Poem
import com.example.kavyakanaja.ui.components.SectionHeader
import com.example.kavyakanaja.ui.theme.NotoSansKannadaFamily
import com.example.kavyakanaja.ui.theme.kannadaContentStyle
import com.example.kavyakanaja.viewmodel.AudioViewModel
import com.example.kavyakanaja.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoemDetailScreen(
    poem: Poem?,
    audioViewModel: AudioViewModel,
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    if (poem == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Poem not found", style = MaterialTheme.typography.titleMedium)
        }
        return
    }

    val context = LocalContext.current
    val isPlaying by audioViewModel.isPlaying.collectAsState()
    val currentUrl by audioViewModel.currentUrl.collectAsState()
    val favoritePoems by userViewModel.favoritePoems.collectAsState()
    val isFavorite = favoritePoems.contains(poem.id)
    var selectedWordMeaning by remember { mutableStateOf<String?>(null) }

    val kannadaStyle = kannadaContentStyle(MaterialTheme.typography.bodyLarge)
    val kannadaColor = MaterialTheme.colorScheme.onSurface

    fun sharePoem() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "${poem.title}\n\n${poem.poemText}\n\n- ${poem.poetName}\n\nShared via Kavya Kanaja")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun downloadPoem() {
        Toast.makeText(context, "Poem downloaded successfully to offline storage", Toast.LENGTH_SHORT).show()
    }

    if (selectedWordMeaning != null) {
        AlertDialog(
            onDismissRequest = { selectedWordMeaning = null },
            confirmButton = {
                TextButton(onClick = { selectedWordMeaning = null }) {
                    Text("OK")
                }
            },
            title = { Text("Word Meaning", style = MaterialTheme.typography.titleLarge) },
            text = {
                Text(
                    selectedWordMeaning!!,
                    style = kannadaContentStyle(MaterialTheme.typography.bodyLarge)
                )
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { downloadPoem() }) {
                        Icon(
                            Icons.Default.CloudDownload,
                            contentDescription = "Download"
                        )
                    }
                    IconButton(onClick = { sharePoem() }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                    IconButton(onClick = { userViewModel.toggleFavoritePoem(poem.id) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove favorite" else "Add favorite",
                            tint = if (isFavorite) Color.Red else LocalContentColor.current
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        ),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = poem.title,
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "by ${poem.poetName}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Headphones,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Listen",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    poem.audioUrl?.let { url ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(12.dp))
                                Text("Original poem audio", style = MaterialTheme.typography.bodyLarge)
                            }
                            FilledTonalIconButton(onClick = { audioViewModel.togglePlay(url) }) {
                                Icon(
                                    imageVector = if (isPlaying && currentUrl == url) {
                                        Icons.Default.PauseCircle
                                    } else {
                                        Icons.Default.PlayCircle
                                    },
                                    contentDescription = "Play or pause recording",
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Kannada narration (AI)", style = MaterialTheme.typography.bodyLarge)
                        }
                        FilledTonalIconButton(onClick = {
                            if (isPlaying && currentUrl == "tts") {
                                audioViewModel.stop()
                            } else {
                                audioViewModel.narrate(poem.poemText)
                            }
                        }) {
                            Icon(
                                imageVector = if (isPlaying && currentUrl == "tts") {
                                    Icons.Default.StopCircle
                                } else {
                                    Icons.Default.PlayCircle
                                },
                                contentDescription = "Play or stop narration",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    val annotatedPoemText = buildAnnotatedString {
                        val words = poem.poemText.split(Regex("(?<=\\s)|(?=\\s)"))
                        words.forEach { word ->
                            val trimmedWord = word.trim()
                            if (poem.wordMeanings.containsKey(trimmedWord)) {
                                pushStringAnnotation(tag = "WORD", annotation = poem.wordMeanings[trimmedWord]!!)
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = NotoSansKannadaFamily,
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                                    )
                                ) {
                                    append(word)
                                }
                                pop()
                            } else {
                                append(word)
                            }
                        }
                    }

                    ClickableText(
                        text = annotatedPoemText,
                        style = kannadaStyle.copy(
                            fontSize = 20.sp,
                            lineHeight = 32.sp,
                            color = kannadaColor
                        ),
                        onClick = { offset ->
                            annotatedPoemText.getStringAnnotations(tag = "WORD", start = offset, end = offset)
                                .firstOrNull()?.let { annotation ->
                                    selectedWordMeaning = annotation.item
                                }
                        }
                    )
                }
            }

            SectionHeader(title = "Bhavartha (Explanation)")
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                )
            ) {
                Text(
                    text = poem.bhavartha,
                    modifier = Modifier.padding(20.dp),
                    style = kannadaContentStyle(MaterialTheme.typography.bodyLarge)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
