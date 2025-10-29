package com.aiia.hospital.aiia.emotion

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import java.util.concurrent.atomic.AtomicBoolean

class FaceAnalyzer(
    private val onEmotionDetected: (FaceEmotion) -> Unit
) : ImageAnalysis.Analyzer {

    private val analyzing = AtomicBoolean(false)

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    )

    override fun analyze(imageProxy: ImageProxy) {
        if (analyzing.get()) {
            imageProxy.close()
            return
        }

        analyzing.set(true)

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    val emotion = if (faces.isNotEmpty()) {
                        val smilingProb = faces[0].smilingProbability ?: 0.0f
                        when {
                            smilingProb > 0.6f -> FaceEmotion.HAPPY
                            smilingProb > 0.3f -> FaceEmotion.NEUTRAL
                            else -> FaceEmotion.SAD
                        }
                    } else {
                        FaceEmotion.NEUTRAL
                    }
                    onEmotionDetected(emotion)
                }
                .addOnFailureListener {
                    onEmotionDetected(FaceEmotion.NEUTRAL)
                }
                .addOnCompleteListener {
                    analyzing.set(false)
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
            analyzing.set(false)
        }
    }
}
