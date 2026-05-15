package com.example.kavyakanaja.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kavyakanaja.viewmodel.PoemViewModel
import com.example.kavyakanaja.viewmodel.PoetViewModel
import com.example.kavyakanaja.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    poemViewModel: PoemViewModel,
    poetViewModel: PoetViewModel,
    onFavoritePoemClick: (String) -> Unit,
    onFavoritePoetClick: (String) -> Unit
) {
    val user by userViewModel.user.collectAsState()
    val favoritePoemIds by userViewModel.favoritePoems.collectAsState()
    val favoritePoetIds by userViewModel.favoritePoets.collectAsState()
    val poems by poemViewModel.poems.collectAsState()
    val poets by poetViewModel.poets.collectAsState()

    val favoritePoemItems = favoritePoemIds.mapNotNull { id -> poems.find { it.id == id } }
    val favoritePoetItems = favoritePoetIds.mapNotNull { id -> poets.find { it.id == id } }

    var isEditing by remember { mutableStateOf(false) }
    var name by remember(user) { mutableStateOf(user?.name ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditing) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    userViewModel.updateProfile(name)
                    isEditing = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
            TextButton(onClick = { isEditing = false }) {
                Text("Cancel")
            }
        } else {
            Text(text = user?.name ?: "Guest", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { isEditing = true }) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Update Profile")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Favorite poems",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (favoritePoemItems.isEmpty()) {
            Text(
                text = "Tap the heart on a poem to save it here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            favoritePoemItems.forEach { poem ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onFavoritePoemClick(poem.id) },
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(text = poem.title, style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = poem.poetName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Favorite poets",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (favoritePoetItems.isEmpty()) {
            Text(
                text = "Tap the heart on a poet's page to save it here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            favoritePoetItems.forEach { poet ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onFavoritePoetClick(poet.id) },
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = poet.name,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { userViewModel.logout() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
