package com.example.kavyakanaja.repository

import android.content.Context
import com.example.kavyakanaja.data.models.User

class UserRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_FAVORITE_POEMS = "favorite_poems"
        private const val KEY_FAVORITE_POETS = "favorite_poets"
        private const val LEGACY_FAVORITES = "favorites"
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean("is_logged_in", loggedIn).apply()
    }

    fun getUser(): User? {
        val name = prefs.getString("user_name", null) ?: return null
        return User(id = "1", name = name)
    }

    fun saveUser(name: String) {
        prefs.edit().putString("user_name", name).apply()
        setLoggedIn(true)
    }

    fun updateProfile(name: String) {
        prefs.edit().putString("user_name", name).apply()
    }

    fun setLanguage(languageCode: String) {
        prefs.edit().putString("language", languageCode).apply()
    }

    fun getLanguage(): String {
        return prefs.getString("language", "kn") ?: "kn"
    }

    /** Stored values: [THEME_LIGHT], [THEME_DARK], [THEME_SYSTEM] */
    fun setThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
    }

    fun getThemeMode(): String {
        return prefs.getString(KEY_THEME_MODE, THEME_SYSTEM) ?: THEME_SYSTEM
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun getFavoritePoems(): Set<String> {
        val stored = prefs.getStringSet(KEY_FAVORITE_POEMS, null)
        if (!stored.isNullOrEmpty()) return stored.toSet()
        val legacy = prefs.getStringSet(LEGACY_FAVORITES, null)
        return if (!legacy.isNullOrEmpty()) {
            prefs.edit().putStringSet(KEY_FAVORITE_POEMS, HashSet(legacy)).apply()
            legacy.toSet()
        } else {
            emptySet()
        }
    }

    fun toggleFavoritePoem(poemId: String) {
        val next = getFavoritePoems().toMutableSet()
        if (!next.remove(poemId)) next.add(poemId)
        prefs.edit().putStringSet(KEY_FAVORITE_POEMS, HashSet(next)).apply()
    }

    fun getFavoritePoets(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITE_POETS, emptySet())?.toSet() ?: emptySet()
    }

    fun toggleFavoritePoet(poetId: String) {
        val next = getFavoritePoets().toMutableSet()
        if (!next.remove(poetId)) next.add(poetId)
        prefs.edit().putStringSet(KEY_FAVORITE_POETS, HashSet(next)).apply()
    }
}
