package com.aiia.hospital.aiia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.robotemi.sdk.Robot
import com.aiia.hospital.aiia.databinding.ActivityCameraBinding
import com.aiia.hospital.aiia.emotion.FaceEmotion
import com.aiia.hospital.aiia.emotion.FaceAnalyzer
import com.robotemi.sdk.TtsRequest

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var robot: Robot  // Instancia del robot Temi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        robot = Robot.getInstance()  // Inicializar Temi

        // Configurar CameraX (Preview + Analysis)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Obtenemos el proveedor de cámara
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Configurar preview para el PreviewView en el layout
            val previewUseCase = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            // Selector de cámara (frontal o trasera, según necesidad)
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            // Configurar análisis de imagen con nuestro FaceAnalyzer
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this),
                FaceAnalyzer { emotion: FaceEmotion ->
                    // Recibimos el resultado de emoción en el hilo de análisis (background)
                    // Actualizamos la UI en el hilo principal:
                    this@CameraActivity.runOnUiThread {
                        binding.tvEmotion.text = emotion.name  // Mostrar la emoción detectada
                    }
                    // (Opcional) Podemos hacer que Temi reaccione según la emoción:
                    if (emotion == FaceEmotion.HAPPY) {
                        robot.speak(TtsRequest.create("Veo que estás feliz!", false))
                    }
                }
            )

            try {
                // Unbind de casos de uso antes de volver a enlazar
                cameraProvider.unbindAll()
                // Vincular preview y análisis al ciclo de vida de esta actividad
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, previewUseCase, imageAnalysis
                )
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }
}
