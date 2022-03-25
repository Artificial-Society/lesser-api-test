package sc.artificial.lesserapitest.gaze


import android.graphics.Bitmap
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer
import sc.artificial.lesserapi.camera.GazeAnalysisDelegate
import sc.artificial.lesserapi.gaze.GazeAnalysisType
import sc.artificial.lesserapi.gaze.GazeInferenceManager


class GazeImageAnalyzer: ImageAnalysis.Analyzer {

    val TAG = "GAZE-IMAGE-ANALYZER"

    val context: Context
    lateinit var rgbaByteArray: ByteArray

    private lateinit var inferenceManager: GazeInferenceManager

    private lateinit var delegate: GazeAnalysisDelegate
    private lateinit var types: Array<GazeAnalysisType>


    constructor(context: Context, gazeAnalysisDelegate: GazeAnalysisDelegate, resultTypes: Array<GazeAnalysisType>) {
        this.context = context

        delegate = gazeAnalysisDelegate
        types = resultTypes

        inferenceManager = GazeInferenceManager(context, gazeAnalysisDelegate, resultTypes)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {

        val currentTime = System.currentTimeMillis();

        val rotation = image.imageInfo.rotationDegrees
        val buffer = image.planes[0].buffer
        rgbaByteArray = buffer.toByteArray()

        val rawBitmap = getOriginalBitmapFromByteArray(rgbaByteArray, image.width, image.height)
        val fixedBitmap = rawBitmap.rotate(rotation.toFloat()).flipHorizontal(true).flipHorizontal(true)

        inferenceManager.processFrame(fixedBitmap, currentTime)

        image.close()
    }


    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    private fun getOriginalBitmapFromByteArray(byteArray: ByteArray, width: Int, height: Int): Bitmap {

        val pixelArray = IntArray(byteArray.size / 4)
        var j = 0
        for (i in 0 until pixelArray.size) {
            val R = byteArray[j++].toInt() and 0xff
            val G = byteArray[j++].toInt() and 0xff
            val B: Int = byteArray[j++].toInt() and 0xff
            val A: Int = byteArray[j++].toInt() and 0xff
            val pixel = A shl 24 or (R shl 16) or (G shl 8) or B
            pixelArray[i] = pixel
        }

        // RAW BITMAP
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixelArray, 0, width, 0, 0, width, height)

        // POST-PROCESS BITMAP
        val rotateLeftAndFlipHorizontally = Matrix()
        rotateLeftAndFlipHorizontally.setScale(-1f, 1f)
        val resBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotateLeftAndFlipHorizontally, false)

        return bitmap
    }



    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun Bitmap.flipHorizontal(isFlipped : Boolean): Bitmap {
        if(isFlipped) {
            val matrix = Matrix().apply { postScale(-1f, 1f) }
            return Bitmap.createBitmap(this, 0, 0, width, height, matrix,false)
        } else {
            return this
        }
    }

}

