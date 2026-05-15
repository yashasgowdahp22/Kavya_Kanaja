package com.example.kavyakanaja.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kavyakanaja.utils.AudioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AudioViewModel(private val audioPlayer: AudioPlayer) : ViewModel() {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentUrl = MutableStateFlow<String?>(null)
    val currentUrl: StateFlow<String?> = _currentUrl

    init {
        audioPlayer.setStatusListener { playing ->
            _isPlaying.value = playing
        }
    }

    fun togglePlay(url: String) {
        if (_currentUrl.value == url) {
            if (_isPlaying.value) {
                audioPlayer.pause()
            } else {
                audioPlayer.resume()
            }
        } else {
            audioPlayer.play(url)
            _currentUrl.value = url
        }
    }

    fun narrate(text: String) {
        _currentUrl.value = "tts"
        audioPlayer.speak(text)
    }

    fun stop() {
        audioPlayer.stop()
        _isPlaying.value = false
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
