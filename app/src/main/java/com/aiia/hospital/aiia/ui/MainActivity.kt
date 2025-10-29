package com.aiia.hospital.aiia.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.aiia.hospital.aiia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Habilita el modo pantalla completa sin bordes

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Acceso a vistas mediante viewBinding
        val previewView = binding.previewView       // Preview de cámara
        val emotionText = binding.tvEmotion         // Texto para emoción detectada
        val infoImage = binding.imageInfo           // Icono de información
        val closeButton = binding.close             // Botón cerrar

        // Aquí iría la lógica para activar cámara, mostrar emoción, cerrar, etc.
    }

    // Extensión para aplicar diseño edge-to-edge
    private fun AppCompatActivity.enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(
                android.view.WindowInsets.Type.statusBars() or
                android.view.WindowInsets.Type.navigationBars()
            )
        }
    }
}
