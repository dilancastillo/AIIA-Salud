package com.aiia.salud

import android.app.Application
import com.robotemi.sdk.Robot

class TemiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicialización temprana del Robot SDK
        Robot.getInstance()
    }
}
