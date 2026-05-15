package com.example.kavyakanaja.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import java.util.Locale

class AudioPlayer(private val context: Context) : TextToSpeech.OnInitListener {
    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var onStatusChanged: ((Boolean) -> Unit)? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        tts = TextToSpeech(context, this)
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                notifyStatus(true)
            }
            override fun onDone(utteranceId: String?) {
                notifyStatus(false)
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                notifyStatus(false)
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.forLanguageTag("kn-IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("AudioPlayer", "Kannada language is not supported on this device")
            } else {
                // Set speech rate to normal (1.0f) as requested
                tts?.setSpeechRate(1.0f) 
                tts?.setPitch(1.0f)
                isTtsReady = true
            }
        } else {
            Log.e("AudioPlayer", "TTS Initialization failed")
        }
    }

    fun setStatusListener(listener: (Boolean) -> Unit) {
        onStatusChanged = listener
    }

    fun play(url: String?) {
        if (url.isNullOrBlank()) return

        try {
            stop()
            val player = MediaPlayer()
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            
            if (url.startsWith("asset:///")) {
                val assetPath = url.substring(9)
                val afd = context.assets.openFd(assetPath)
                player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
            } else {
                player.setDataSource(url)
            }
            
            player.setOnPreparedListener { mp ->
                mp.start()
                notifyStatus(true)
            }
            player.setOnCompletionListener { notifyStatus(false) }
            player.prepareAsync()
            mediaPlayer = player
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Playback failed", e)
            notifyStatus(false)
        }
    }

    fun speak(text: String) {
        if (!isTtsReady) {
            showToast("Narration engine not ready")
            return
        }
        stop()

        // Remove emojis and symbols before narrating
        val emojiRegex = Regex("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]|\\p{So}+")
        val cleanText = text.replace(emojiRegex, "")

        val params = android.os.Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "PoemNarration")
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "PoemNarration")
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
        tts?.stop()
        notifyStatus(false)
    }

    fun pause() {
        mediaPlayer?.let { if (it.isPlaying) { it.pause(); notifyStatus(false) } }
        if (tts?.isSpeaking == true) { tts?.stop(); notifyStatus(false) }
    }

    fun resume() {
        mediaPlayer?.let { it.start(); notifyStatus(true) }
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
    }

    private fun notifyStatus(isPlaying: Boolean) {
        mainHandler.post { onStatusChanged?.invoke(isPlaying) }
    }

    private fun showToast(message: String) {
        mainHandler.post { Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }
    }
}
