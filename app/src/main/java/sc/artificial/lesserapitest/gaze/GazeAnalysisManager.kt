package sc.artificial.lesserapitest.gaze

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.Surface
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import sc.artificial.lesserapi.camera.GazeAnalysisDelegate
import sc.artificial.lesserapi.gaze.GazeAnalysisType
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class GazeAnalysisManager {

    companion object {

        private lateinit var cameraProvider: ProcessCameraProvider
        private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
        private lateinit var cameraSelector : CameraSelector
        private lateinit var cameraExecutor: ExecutorService

        private lateinit var imageAnalysis: ImageAnalysis
        private lateinit var delegate: GazeAnalysisDelegate
        private lateinit var types: Array<GazeAnalysisType>

        const val TAG = "GAZE-ANALYSIS"


        fun initAnalyzer(gazeAnalysisDelegate: GazeAnalysisDelegate, resultTypes: Array<GazeAnalysisType>) {

            delegate = gazeAnalysisDelegate
            types = resultTypes

        }

        fun bindCamera(context: Context) {
            // SET CAMERA & PREVIEW
            cameraExecutor = Executors.newSingleThreadExecutor()
            cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({

                cameraProvider = cameraProviderFuture.get()

                // SELECT CAMERA
                cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build()

                // BUILD IMAGE ANALYSIS
                imageAnalysis = ImageAnalysis.Builder()
                    // enable the following line if RGBA output is needed. DEFAULT: YUV_420_888
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, GazeImageAnalyzer(context, delegate, types))
                    }

                // HANDLE ROTATION
                val orientation = (context as Activity).windowManager.defaultDisplay.orientation
                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageAnalysis.targetRotation = rotation

                val useCaseGroup = UseCaseGroup.Builder()
                    .addUseCase(imageAnalysis)
                    .build()

                // BIND CAMERA PROVIDER WITH CAMERA
                try {
                    cameraProvider.unbindAll() // Unbind use cases before rebinding
                    var camera = cameraProvider.bindToLifecycle(
                        context as LifecycleOwner,
                        cameraSelector, useCaseGroup
                    )
                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context))
        }

        fun unbindCamera() {
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                cameraProvider.unbindAll()
            }
        }

    }
}