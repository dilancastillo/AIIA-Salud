package com.aiia.hospital.aiia

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.robotemi.sdk.Robot
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import com.robotemi.sdk.TtsRequest 

class TemiPatrolService : Service(), OnGoToLocationStatusChangedListener {

    private lateinit var robot: Robot
    private val locationsQueue: List<String> = listOf("Punto A", "Punto B", "Punto C")
    private var currentIndex: Int = 0

    override fun onCreate() {
        super.onCreate()
        robot = Robot.getInstance()  // Obtener instancia del robot
        robot.addOnGoToLocationStatusChangedListener(this)  // Registrar listener:contentReference[oaicite:11]{index=11}
        // Iniciar patrullaje si se desea automáticamente
        if (locationsQueue.isNotEmpty()) {
            robot.goTo(locationsQueue[currentIndex])  // Ir al primer punto
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        robot.removeOnGoToLocationStatusChangedListener(this)  // Remover listener al terminar:contentReference[oaicite:12]{index=12}
    }

    /** Callback del SDK de Temi cuando cambia el estado de ir a una ubicación. */
    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        if (status == OnGoToLocationStatusChangedListener.COMPLETE) {
            // Llegó a la ubicación 'location'
            if (robot.isReady) {
                val tts =TtsRequest.create("Llegamos a $location", true)
                robot.speak(tts)

                // Pasar a la siguiente ubicación en la patrulla
                currentIndex += 1
                if (currentIndex < locationsQueue.size) {
                    robot.goTo(locationsQueue[currentIndex])
                } else {
                    currentIndex = 0  // Reiniciar o detener patrullaje según la lógica deseada
                }
            }
        } else if (status == OnGoToLocationStatusChangedListener.ABORT) {
            // Manejar caso de aborto (obstáculo, cancelación, etc.)
            // Por ejemplo, intentar nuevamente o notificar.
        }
        // (También se pueden manejar otros estados como "start", "going", etc., si es necesario)
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Este servicio no es de tipo Bound, retornamos null
        return null
    }
}
