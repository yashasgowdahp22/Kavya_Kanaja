package com.example.kavyakanaja.data.models

enum class ContentType {
    POEM, STORY, LITERATURE, WRITING
}

data class Poem(
    val id: String,
    val title: String,
    val poetName: String,
    val poemText: String,
    val category: String,
    val bhavartha: String,
    val type: ContentType = ContentType.POEM,
    val audioUrl: String? = null,
    val aiNarration: Boolean = false,
    val narrationSpeed: String = "normal",
    val wordMeanings: Map<String, String> = emptyMap(),
    val keywords: List<String> = emptyList()
)
