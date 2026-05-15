package com.example.kavyakanaja.data.models

data class User(
    val id: String,
    val name: String,
    val preferences: UserPreferences = UserPreferences()
)

data class UserPreferences(
    val language: String = "kn", // kn for Kannada, en for English
    val fontSize: Float = 16f,
    val notificationsEnabled: Boolean = true,
    val darkMode: Boolean = false
)
