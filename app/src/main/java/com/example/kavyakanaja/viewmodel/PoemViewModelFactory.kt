package com.example.kavyakanaja.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kavyakanaja.repository.PoemRepository

import com.example.kavyakanaja.repository.PoetRepository

import com.example.kavyakanaja.utils.AudioPlayer

import com.example.kavyakanaja.repository.UserRepository

class PoemViewModelFactory(
    private val poemRepository: PoemRepository,
    private val poetRepository: PoetRepository,
    private val audioPlayer: AudioPlayer,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PoemViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                PoemViewModel(poemRepository) as T
            }
            modelClass.isAssignableFrom(PoetViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                PoetViewModel(poetRepository) as T
            }
            modelClass.isAssignableFrom(AudioViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                AudioViewModel(audioPlayer) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                UserViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
