package com.aiia.hospital.aiia

import com.robotemi.sdk.Robot

class TemiNavigator private constructor(private val robot: Robot) {

    companion object {
        fun build(): TemiNavigator {
            // Inicia el "builder" con la instancia del robot Temi
            return TemiNavigator(Robot.getInstance())
        }
    }

    /**
     * Ordena al robot Temi patrullar todas las ubicaciones guardadas en su mapa actual.
     * Recorre secuencialmente cada ubicación conocida y la visita.
     */
    fun patrol() {
        // Obtener las ubicaciones guardadas (nombres de lugares) del Temi
        val locations: List<String> = robot.locations  // Método del SDK que devuelve lista de nombres
        if (locations.isEmpty()) return

        // Enviar al robot a cada ubicación secuencialmente
        for (location in locations) {
            robot.goTo(location)  // Ordena al robot ir a la ubicación especificada
            // Nota: en un escenario real, se debería esperar a que alcance la ubicación o usar callbacks del listener.
        }
    }
}
