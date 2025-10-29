package com.aiia.hospital.aiia.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import com.google.android.exoplayer2.ui.PlayerView
import androidx.camera.view.PreviewView
import com.robotemi.sdk.Robot
import com.aiia.hospital.aiia.robot.EmotionActions
import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    // UI creada por código (sin XML)
    private lateinit var previewView: PreviewView
    private lateinit var playerView: PlayerView

    // CameraX
    private lateinit var cameraExecutor: ExecutorService

    // ML Kit
    private val faceDetector by lazy {
        val opts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL) // smilingProbability
            .build()
        FaceDetection.getClient(opts)
    }

    // Temi + acciones
    private val robot by lazy { Robot.getInstance() }
    private var actions: EmotionActions? = null

    // Anti-rebote de acciones
    private var throttleJob: Job? = null

    // Datos del paciente / ubicación
    private var patientId: String = "PACIENTE_001"
    private var room: String = "Habitación 101"

    // Permisos
    private val requestPerms = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { startCameraIfGranted() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperar extras (si vienen)
        intent.getStringExtra("patientId")?.let { patientId = it }
        intent.getStringExtra("room")?.let { room = it }

        // Construir UI básica (Preview arriba, Player abajo)
        val root = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        previewView = PreviewView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        playerView = PlayerView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, dp(200)
            ).apply {
                // anclar al fondo
                gravity = android.view.Gravity.BOTTOM
            }
        }
        root.addView(previewView)
        root.addView(playerView)
        setContentView(root)

        // Inicializar helpers
        cameraExecutor = Executors.newSingleThreadExecutor()
        actions = EmotionActions(this, robot, playerView)

        // Permisos
        ensurePermissions()
    }

    private fun ensurePermissions() {
        val need = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            .any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }

        if (need) {
            requestPerms.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        } else {
            startCamera()
        }
    }

    private fun startCameraIfGranted() {
        val granted = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            .all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
        if (granted) startCamera()
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val cameraProvider = providerFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(cameraExecutor, ::analyzeFrame)
                }

            val selector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, selector, preview, analyzer)
            } catch (_: Exception) {
                // puedes hacer log si quieres
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun analyzeFrame(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return imageProxy.close()
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        faceDetector.process(image)
            .addOnSuccessListener { faces -> handleFaces(faces) }
            .addOnCompleteListener { imageProxy.close() }
    }

    private fun handleFaces(faces: List<Face>) {
        if (faces.isEmpty()) return
        val face = faces.first()
        val smile = face.smilingProbability ?: 0f

        val emotion = when {
            smile >= 0.6f -> "happy"
            smile <= 0.3f -> "sad"
            else -> "neutral"
        }

        // Evitar spamear acciones (máx 1 cada 4s)
        if (throttleJob?.isActive == true) return
        throttleJob = lifecycleScope.launch {
            actions?.onEmotion(patientId, room, emotion)
            delay(4000)
        }
    }

    private fun dp(px: Int): Int {
        val scale = resources.displayMetrics.density
        return (px * scale).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        throttleJob?.cancel()
        actions?.release()
        cameraExecutor.shutdown()
        faceDetector.close()
    }
}
