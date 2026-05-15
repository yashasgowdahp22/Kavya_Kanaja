package com.example.kavyakanaja.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kavyakanaja.data.models.Poet
import com.example.kavyakanaja.repository.PoetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PoetViewModel(private val repository: PoetRepository) : ViewModel() {

    private val _poets = MutableStateFlow<List<Poet>>(emptyList())
    val poets: StateFlow<List<Poet>> = _poets

    init {
        loadPoets()
    }

    private fun loadPoets() {
        viewModelScope.launch {
            _poets.value = repository.getAllPoets()
        }
    }
}
