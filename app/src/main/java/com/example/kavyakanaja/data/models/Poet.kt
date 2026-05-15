package com.example.kavyakanaja.data.models

data class Poet(
    val id: String,
    val name: String,
    val bio: String,
    val famousWorks: List<String>,
    val imageUrl: String? = null,
    val keywords: List<String> = emptyList()
)
