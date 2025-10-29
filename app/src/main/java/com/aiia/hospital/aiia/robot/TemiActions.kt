package com.aiia.hospital.aiia

import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest

object TemiActions {
    // Ejemplo de función de acción Temi ajustada:
    fun sayWelcome(userName: String) {
        val robot = Robot.getInstance()
        // Construir la petición TTS correctamente antes de hablar
        val ttsRequest = TtsRequest.create("Hola $userName, bienvenido.", false)
        robot.speak(ttsRequest)  // Ahora se pasa un TtsRequest, no un String
    }

    fun askQuestion(question: String) {
        val robot = Robot.getInstance()
        val ttsRequest = TtsRequest.create(question, true)
        robot.speak(ttsRequest)
    }
    // ... otras acciones Temi usando TtsRequest de forma similar ...
}
