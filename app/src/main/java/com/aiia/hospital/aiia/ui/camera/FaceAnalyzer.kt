package com.aiia.hospital.aiia

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

/**
 * Analizador de cámara que detecta rostros y evalúa una emoción básica.
 * @param onEmotionDetected Callback que recibe la emoción detectada en el rostro (o UNKNOWN si no se detecta rostro).
 */
class FaceAnalyzer(private val onEmotionDetected: (FaceEmotion) -> Unit) : ImageAnalysis.Analyzer {

    // Configurar opciones del detector de rostros de ML Kit:
    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)            // Modo rápido
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)                  // Sin landmarks, no necesario para emoción
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)                    // Sin contornos
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)       // Habilitar clasificación (smile, eyes open)
            .build()
    )

    override fun analyze(imageProxy: ImageProxy) {
        try {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                // Convertir la imagen de CameraX a InputImage de ML Kit
                val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                // Procesar la imagen para detectar rostros
                detector.process(inputImage)
                    .addOnSuccessListener { faces ->
                        // Determinar la emoción basada en la sonrisa (u otras métricas si se desea)
                        val emotion = if (faces.isNotEmpty()) {
                            val face = faces[0]
                            val smileProb = face.smilingProbability ?: -1f
                            if (smileProb > 0.7f) {
                                FaceEmotion.HAPPY    // alta probabilidad de sonrisa -> feliz
                            } else if (smileProb >= 0.0f) {
                                FaceEmotion.NEUTRAL  // rostro detectado pero sin sonrisa notable
                            } else {
                                FaceEmotion.UNKNOWN  // no hay dato de probabilidad (debería no ocurrir si classification está ALL)
                            }
                        } else {
                            FaceEmotion.UNKNOWN      // no se detectó ningún rostro
                        }
                        // Invocar callback con la emoción resultante
                        onEmotionDetected(emotion)
                    }
                    .addOnFailureListener { 
                        // En caso de error en detección, reportar desconocido
                        onEmotionDetected(FaceEmotion.UNKNOWN)
                    }
                    .addOnCompleteListener {
                        // Cerrar la imagen pase lo que pase, para liberar el frame
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        } catch (e: Exception) {
            // Asegurar cierre del proxy en caso de excepción no controlada
            imageProxy.close()
            e.printStackTrace()
        }
    }
}
