package com.aiia.salud.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Size
import android.view.*
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.aiia.salud.databinding.FragmentCameraBinding
import com.aiia.salud.robot.EmotionActions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import com.robotemi.sdk.Robot
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private var _b: FragmentCameraBinding? = null
    private val b get() = _b!!
    private val robot by lazy { Robot.getInstance() }

    private lateinit var patientId: String
    private lateinit var room: String

    private val exec = Executors.newSingleThreadExecutor()
    private var actions: EmotionActions? = null
    private var throttleJob: Job? = null

    companion object {
        fun new(patientId: String, room: String) = CameraFragment().apply {
            arguments = Bundle().apply { putString("pid", patientId); putString("room", room) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientId = requireArguments().getString("pid")!!
        room = requireArguments().getString("room")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentCameraBinding.inflate(inflater, container, false).also { _b = it }.root

    @SuppressLint("UnsafeOptInUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        actions = EmotionActions(requireContext(), robot, b.playerView)
        val provider = ProcessCameraProvider.getInstance(requireContext())
        provider.addListener({
            val cameraProvider = provider.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(b.preview.surfaceProvider) }

            val analyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(exec, ::analyze)
                }

            val selector = CameraSelector.DEFAULT_FRONT_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(viewLifecycleOwner, selector, preview, analyzer)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun analyze(img: ImageProxy) {
        val media = img.image ?: return img.close()
        val image = InputImage.fromMediaImage(media, img.imageInfo.rotationDegrees)
        val opts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        FaceDetection.getClient(opts).process(image)
            .addOnSuccessListener { faces -> handleFaces(faces) }
            .addOnCompleteListener { img.close() }
    }

    private fun handleFaces(faces: List<Face>) {
        if (faces.isEmpty()) return
        val f = faces.first()
        val smile = f.smilingProbability ?: 0f

        val emotion = when {
            smile >= 0.6f -> "happy"
            smile <= 0.3f -> "sad"
            else -> "neutral"
        }

        // Anti-rebote: máximo 1 acción cada 4 s
        if (throttleJob?.isActive == true) return
        throttleJob = viewLifecycleOwner.lifecycleScope.launch {
            actions?.onEmotion(patientId, room, emotion)
            delay(4000)
        }
    }

    override fun onDestroyView() {
        actions?.release()
        _b = null
        super.onDestroyView()
    }
}
