package com.aiia.salud.robot

import com.robotemi.sdk.Robot

class TemiNavigator(private val robot: Robot) {

    /** Moverse a un “location” guardado en Temi (usar app de Temi para guardar puntos) */
    fun goTo(location: String) {
        robot.goTo(location)
    }

    /** Ronda simple por una lista de ubicaciones */
    fun patrol(locations: List<String>) {
        if (locations.isEmpty()) return
        // sencillo: ve al primero; para rutas más complejas, programar callbacks onGoToLocationStatusChanged
        robot.goTo(locations.first())
    }
}
