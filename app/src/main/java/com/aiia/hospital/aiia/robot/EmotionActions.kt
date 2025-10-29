package com.aiia.hospital.aiia.robot

import android.content.Context
import com.aiia.hospital.aiia.data.AppDb
import com.aiia.hospital.aiia.data.Observation
import com.aiia.hospital.aiia.net.AlertReq
import com.aiia.hospital.aiia.net.NetModule
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmotionActions(
    private val ctx: Context,
    private val robot: Robot,
    private val playerView: PlayerView? = null
) {
    private val io = CoroutineScope(Dispatchers.IO)
    private var player: ExoPlayer? = null

    fun onEmotion(patientId: String, room: String, emotion: String) {
        when (emotion) {
            "happy" -> {
                speak("Qué alegría verte con buen ánimo. Sigamos con la rutina.")
                playVideo("https://storage.googleapis.com/temi-videos/motivacional.mp4")
            }
            "sad" -> {
                speak("Te noto bajito de ánimo. Vamos a relajarnos un momento.")
                playVideo("https://storage.googleapis.com/temi-videos/relajacion.mp4")
                sendAlert(patientId, room, emotion, "Detección de tristeza. Revisar paciente.")
            }
            else -> { /* neutral: no video */ }
        }
        saveLocal(patientId, emotion)
    }

    private fun speak(text: String) {
        robot.speak(TtsRequest.create(text, false))
    }

    private fun playVideo(url: String) {
        playerView ?: return
        if (player == null) player = ExoPlayer.Builder(ctx).build().also { playerView.player = it }
        player?.apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true
        }
    }

    private fun sendAlert(patientId: String, room: String, emotion: String, note: String) {
        io.launch {
            runCatching {
                NetModule.api.send(AlertReq(patientId, emotion, room, note))
            }
        }
    }

    private fun saveLocal(patientId: String, emotion: String) {
        io.launch {
            AppDb.getInstance(ctx).observation().add(
                Observation(patientId = patientId, timestamp = System.currentTimeMillis(), emotion = emotion, note = null)
            )
        }
    }

    fun release() { player?.release(); player = null }
}
