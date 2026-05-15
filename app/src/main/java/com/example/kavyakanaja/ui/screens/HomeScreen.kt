package com.example.kavyakanaja.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kavyakanaja.R
import com.example.kavyakanaja.data.models.ContentType
import com.example.kavyakanaja.data.models.Poem
import com.example.kavyakanaja.ui.components.SectionHeader
import com.example.kavyakanaja.viewmodel.PoemViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(viewModel: PoemViewModel, onPoemClick: (String) -> Unit) {
    val poems by viewModel.poems.collectAsState()
    val poemOfTheDayInfo by viewModel.poemOfTheDayInfo.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ContentType.POEM) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Patriotic", "Nature", "Devotional", "Social", "Love", "Folk", "Epic", "Modern", "Classical", "Vachana", "Dasa Sahitya")

    val filteredPoems = remember(poems, selectedType, searchQuery, selectedCategory) {
        val byTypeAndSearch = poems.filter {
            val matchesType = it.type == selectedType
            val matchesSearch = it.title.contains(searchQuery, ignoreCase = true) ||
                it.poetName.contains(searchQuery, ignoreCase = true) ||
                it.keywords.any { keyword -> keyword.contains(searchQuery, ignoreCase = true) }

            if (searchQuery.isNotEmpty()) matchesSearch else matchesType && matchesSearch
        }
        if (selectedCategory != null) {
            byTypeAndSearch.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        } else {
            byTypeAndSearch
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                HomeHeader()
                SearchBar(searchQuery) { searchQuery = it }
                if (searchQuery.isEmpty()) {
                    ContentTypeTabs(selectedType) { 
                        selectedType = it
                        selectedCategory = null 
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (searchQuery.isEmpty()) {
                stickyHeader {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        shadowElevation = 4.dp
                    ) {
                        CategoryRow(
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onCategoryToggle = { name ->
                                selectedCategory = if (selectedCategory == name) null else name
                            }
                        )
                    }
                }

                if (selectedType == ContentType.POEM && selectedCategory == null) {
                    poemOfTheDayInfo?.let { info ->
                        item {
                            PoemOfTheDaySection(
                                poem = info.poem,
                                onClick = { onPoemClick(info.poem.id) }
                            )
                        }
                    }
                }
            }

            item {
                SectionHeader(
                    title = if (searchQuery.isEmpty()) {
                        "Explore ${selectedType.name.lowercase().replaceFirstChar { it.uppercase() }}s"
                    } else {
                        "Search Results"
                    }
                )
            }

            items(filteredPoems) { poem ->
                PoemCard(poem = poem, onClick = { onPoemClick(poem.id) })
            }
            
            if (filteredPoems.isEmpty()) {
                item {
                    EmptyStatePlaceholder(searchQuery, selectedCategory)
                }
            }
        }
    }
}

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Kavya Kanaja",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Welcome to the world of Kannada",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Image(
                painter = painterResource(id = R.drawable.kuvempu),
                contentDescription = "App Logo",
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun ContentTypeTabs(selected: ContentType, onSelect: (ContentType) -> Unit) {
    ScrollableTabRow(
        selectedTabIndex = selected.ordinal,
        edgePadding = 16.dp,
        containerColor = Color.Transparent,
        divider = {}
    ) {
        ContentType.values().forEach { type ->
            Tab(
                selected = selected == type,
                onClick = { onSelect(type) },
                text = { 
                    Text(
                        text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelLarge
                    ) 
                }
            )
        }
    }
}

@Composable
fun CategoryRow(
    categories: List<String>,
    selectedCategory: String?,
    onCategoryToggle: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { name ->
            val isSelected = selectedCategory == name
            val backgroundColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, label = ""
            )
            val textColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, label = ""
            )

            Surface(
                modifier = Modifier
                    .clickable { onCategoryToggle(name) }
                    .shadow(if (isSelected) 4.dp else 0.dp, RoundedCornerShape(12.dp)),
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Text(
                    text = name,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun PoemCard(poem: Poem, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = poem.title.first().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = poem.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = poem.poetName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun PoemOfTheDaySection(poem: Poem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
        )) {
            Column(modifier = Modifier.padding(20.dp).align(Alignment.CenterStart)) {
                Text("POEM OF THE DAY", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                Spacer(Modifier.height(4.dp))
                Text(poem.title, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Text("by ${poem.poetName}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
            }
        }
    }
}

@Composable
fun EmptyStatePlaceholder(query: String, category: String?) {
    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
        Text(
            text = if (query.isNotEmpty()) "No results found for \"$query\"" 
                   else if (category != null) "No content yet for \"$category\""
                   else "Loading...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Search poems or poets...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )
}
