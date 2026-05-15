package com.example.kavyakanaja.repository

import android.content.Context
import com.example.kavyakanaja.data.models.Poet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class PoetRepository(private val context: Context) {
    private val gson = Gson()

    fun getAllPoets(): List<Poet> {
        // I should create poets.json too
        return try {
            val inputStream = context.assets.open("poets.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Poet>>() {}.type
            gson.fromJson(reader, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getPoetById(id: String): Poet? {
        return getAllPoets().find { it.id == id }
    }
}
