package com.aiia.salud.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aiia.salud.databinding.ActivityMainBinding
import com.aiia.salud.robot.TemiPatrolService
import com.robotemi.sdk.Robot
import com.robotemi.sdk.listeners.OnRobotReadyListener
import com.robotemi.sdk.TtsRequest

class MainActivity : AppCompatActivity(), OnRobotReadyListener {

    private lateinit var b: ActivityMainBinding
    private val robot by lazy { Robot.getInstance() }

    private val reqPerms = registerForActivityResult(RequestMultiplePermissions()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        robot.addOnRobotReadyListener(this)

        b.btnStartCamera.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(b.container.id, CameraFragment.new("PACIENTE_001", "Habitación 101"))
                .commit()
        }

        b.btnStartPatrol.setOnClickListener {
            startForegroundService(Intent(this, TemiPatrolService::class.java))
        }

        askPerms()
    }

    private fun askPerms() {
        val need = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            .any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (need) reqPerms.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
    }

    override fun onRobotReady(isReady: Boolean) {
        if (isReady) robot.speak(TtsRequest.create("Listo para iniciar monitoreo.", false))
    }

    override fun onDestroy() {
        robot.removeOnRobotReadyListener(this)
        super.onDestroy()
    }
}
