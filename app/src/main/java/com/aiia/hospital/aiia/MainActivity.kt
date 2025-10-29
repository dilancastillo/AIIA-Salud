package com.aiia.hospital.aiia

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.aiia.hospital.aiia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), Robot.TtsListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartTour.setOnClickListener {
            val message = """
                Iniciando recorrido de hospitalización.
                Me acercaré cama por cama para verificar a cada paciente.
                Por favor, mantengan los pasillos despejados.
            """.trimIndent()

            val req = TtsRequest.create(message, true)
            Robot.getInstance().speak(req)
        }

        binding.btnSelectPatient.setOnClickListener {
            startActivity(Intent(this, SelectPatientActivity::class.java))
        }
    }
    override fun onTtsStatusChanged(ttsRequest: TtsRequest) {

    }
}