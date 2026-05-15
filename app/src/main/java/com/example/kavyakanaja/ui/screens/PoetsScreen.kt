package com.example.kavyakanaja.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kavyakanaja.viewmodel.PoetViewModel
import com.example.kavyakanaja.data.models.Poet

@Composable
fun PoetsScreen(viewModel: PoetViewModel, onPoetClick: (String) -> Unit) {
    val poets by viewModel.poets.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredPoets = poets.filter {
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.famousWorks.any { work -> work.contains(searchQuery, ignoreCase = true) } ||
        it.keywords.any { keyword -> keyword.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                placeholder = { Text("Search poets (English or Kannada)...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search poets")
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.PersonSearch,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = if (searchQuery.isEmpty()) "Poets Corner" else "Search Results",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(filteredPoets) { poet ->
                PoetListItem(poet = poet, onClick = { onPoetClick(poet.id) })
            }
            
            if (filteredPoets.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp)) {
                        Text(
                            text = "No poets found matching \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PoetListItem(poet: Poet, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = poet.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Famous works: ${poet.famousWorks.joinToString(", ")}", 
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}
