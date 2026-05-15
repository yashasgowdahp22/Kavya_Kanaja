package com.example.kavyakanaja.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kavyakanaja.data.models.Poet
import com.example.kavyakanaja.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoetDetailScreen(
    poet: Poet?,
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    val favoritePoets by userViewModel.favoritePoets.collectAsState()
    val isFavorite = poet != null && favoritePoets.contains(poet.id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(poet?.name ?: "Poet Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (poet != null) {
                        IconButton(onClick = { userViewModel.toggleFavoritePoet(poet.id) }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite poet",
                                tint = if (isFavorite) Color.Red else LocalContentColor.current
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (poet == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text("Poet not found")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = poet.name, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Biography", style = MaterialTheme.typography.titleMedium)
            Text(text = poet.bio, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Famous Works", style = MaterialTheme.typography.titleMedium)
            poet.famousWorks.forEach { work ->
                Text(text = "• $work", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
