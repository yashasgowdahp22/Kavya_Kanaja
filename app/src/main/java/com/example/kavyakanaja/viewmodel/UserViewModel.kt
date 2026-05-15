package com.example.kavyakanaja.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kavyakanaja.data.models.User
import com.example.kavyakanaja.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _language = MutableStateFlow("kn")
    val language: StateFlow<String> = _language

    private val _themeMode = MutableStateFlow(UserRepository.THEME_LIGHT)
    val themeMode: StateFlow<String> = _themeMode

    private val _favoritePoems = MutableStateFlow<Set<String>>(emptySet())
    val favoritePoems: StateFlow<Set<String>> = _favoritePoems

    private val _favoritePoets = MutableStateFlow<Set<String>>(emptySet())
    val favoritePoets: StateFlow<Set<String>> = _favoritePoets

    init {
        _isLoggedIn.value = repository.isLoggedIn()
        _themeMode.value = repository.getThemeMode()
        if (_isLoggedIn.value) {
            _user.value = repository.getUser()
            _favoritePoems.value = repository.getFavoritePoems()
            _favoritePoets.value = repository.getFavoritePoets()
            _language.value = repository.getLanguage()
        }
    }

    fun updateProfile(name: String) {
        repository.updateProfile(name)
        _user.value = repository.getUser()
    }

    fun setLanguage(languageCode: String) {
        repository.setLanguage(languageCode)
        _language.value = languageCode
    }

    fun setThemeMode(mode: String) {
        repository.setThemeMode(mode)
        _themeMode.value = mode
    }

    fun toggleFavoritePoem(poemId: String) {
        repository.toggleFavoritePoem(poemId)
        _favoritePoems.value = repository.getFavoritePoems()
    }

    fun toggleFavoritePoet(poetId: String) {
        repository.toggleFavoritePoet(poetId)
        _favoritePoets.value = repository.getFavoritePoets()
    }

    fun login(name: String) {
        repository.saveUser(name)
        _user.value = repository.getUser()
        _isLoggedIn.value = true
        _favoritePoems.value = repository.getFavoritePoems()
        _favoritePoets.value = repository.getFavoritePoets()
        _language.value = repository.getLanguage()
    }

    fun logout() {
        repository.logout()
        _user.value = null
        _isLoggedIn.value = false
    }
}
