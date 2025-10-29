package com.aiia.hospital.aiia

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intentx
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
            Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
        }

        binding.btnSelectPatient.setOnClickListener {
            startActivity(Intent(this, SelectPatientActivity::class.java))
        }
    }
    override fun onTtsStatusChanged(ttsRequest: TtsRequest) {

    }
}