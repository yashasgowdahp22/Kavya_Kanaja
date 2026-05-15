package com.example.kavyakanaja.data.models

data class PoemOfTheDayInfo(
    val poem: Poem,
    /** e.g. "Wednesday, May 13, 2026" in the device locale */
    val dateLabel: String
)
