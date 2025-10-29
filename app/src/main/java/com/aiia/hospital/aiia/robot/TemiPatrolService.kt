package com.aiia.salud.robot

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.aiia.salud.R
import com.robotemi.sdk.Robot

class TemiPatrolService : Service() {
    private val robot by lazy { Robot.getInstance() }
    private val navigator by lazy { TemiNavigator(robot) }

    override fun onCreate() {
        super.onCreate()
        startForeground(1, notif("Rondas iniciadas"))
        navigator.patrol(listOf("Habitación 101", "Habitación 102", "Enfermería"))
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun notif(text: String): Notification {
        val chId = "patrol"
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(chId) == null) {
            nm.createNotificationChannel(NotificationChannel(chId, "Rondas Temi", NotificationManager.IMPORTANCE_LOW))
        }
        return NotificationCompat.Builder(this, chId)
            .setContentTitle("Temi Atención Geriátrica")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }
}
