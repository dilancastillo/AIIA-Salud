package com.aiia.hospital.aiia

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.aiia.hospital.aiia.databinding.ActivityFaceScanBinding
import com.aiia.hospital.aiia.ui.FaceOverlayView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class FaceScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceScanBinding
    private lateinit var previewView: PreviewView
    private lateinit var overlay: FaceOverlayView

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private val detector by lazy {
        val opts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .enableTracking()
            .build()
        FaceDetection.getClient(opts)
    }

    private var stableFrames = 0
    private var lastTrackingId: Int? = null

    private val askCameraPerm = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera() else showNoCameraDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        previewView = binding.previewView
        overlay = binding.faceOverlay

        setupBottomBar(binding.bottomActions)

        val patientName = intent.getStringExtra("patient_name") ?: "Paciente"
        binding.tvStatus.text = "Apunta la cámara al rostro de: $patientName"

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) startCamera() else askCameraPerm.launch(Manifest.permission.CAMERA)
    }

    private fun showNoCameraDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cámara requerida")
            .setMessage("Se necesita el permiso de cámara para realizar el reconocimiento.")
            .setPositiveButton("OK") { _, _ -> finish() }
            .show()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

            val analyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(720, 1280))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            analyzer.setAnalyzer(cameraExecutor) { proxy ->
                processFrame(proxy)
            }

            val selector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, selector, preview, analyzer
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Error cámara: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processFrame(proxy: ImageProxy) {
        val media = proxy.image ?: return proxy.close()
        val image = InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees)

        detector.process(image)
            .addOnSuccessListener { faces ->

                val boxes = faces.mapNotNull { face ->
                    val rect = face.boundingBox

                    val scaled = scaleRectToView(rect, image.width, image.height, previewView)
                    scaled
                }
                runOnUiThread { overlay.setBoxes(boxes) }


                if (faces.isNotEmpty()) {
                    val currentId = faces[0].trackingId
                    if (currentId != null && currentId == lastTrackingId) {
                        stableFrames++
                    } else {
                        stableFrames = 0
                        lastTrackingId = currentId
                    }

                    if (stableFrames >= 10) {
                        stableFrames = 0
                        onStableFaceDetected()
                    }
                } else {
                    lastTrackingId = null
                    stableFrames = 0
                }
            }
            .addOnFailureListener {

            }
            .addOnCompleteListener { proxy.close() }
    }

    private fun onStableFaceDetected() {

        runOnUiThread {
            binding.tvStatus.text = "Rostro detectado. Verificando identidad..."
        }
        Robot.getInstance().speak(
            TtsRequest.create("Rostro detectado. Iniciando verificación.", true)
        )

        lifecycleScope.launch(Dispatchers.Main) {

            delay(600)
            binding.tvStatus.text = "Reconocimiento pendiente"
        }
    }

    private fun scaleRectToView(
        rect: android.graphics.Rect,
        imgW: Int,
        imgH: Int,
        view: PreviewView
    ): android.graphics.RectF? {
        val vw = view.width.toFloat()
        val vh = view.height.toFloat()
        if (vw <= 0f || vh <= 0f) return null


        val scale = maxOf(vw / imgW, vh / imgH)
        val offsetX = (vw - imgW * scale) / 2f
        val offsetY = (vh - imgH * scale) / 2f

        val left = rect.left * scale + offsetX
        val top = rect.top * scale + offsetY
        val right = rect.right * scale + offsetX
        val bottom = rect.bottom * scale + offsetY

        return android.graphics.RectF(left, top, right, bottom)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        detector.close()
    }
}
















