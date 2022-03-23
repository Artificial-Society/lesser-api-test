package sc.artificial.lesserapitest

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import sc.artificial.lesserapi.camera.GazeAnalysisDelegate
import sc.artificial.lesserapi.camera.GazeBitmapAnalysisManager
import sc.artificial.lesserapi.camera.GazeBitmapAnalysisType
import sc.artificial.lesserapi.camera.GazeBitmapAnalyzer
import sc.artificial.lesserapi.permissions.PermissionControl
import sc.artificial.lesserapitest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), GazeAnalysisDelegate {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // CameraX를 이용하는 경우
        if (PermissionControl.checkAndGetPermissions(this)) {
            GazeBitmapAnalysisManager.initAnalyzer(this, arrayOf(GazeBitmapAnalysisType.concentraiton, GazeBitmapAnalysisType.gaze))
        }
        GazeBitmapAnalysisManager.bindCamera(this)

        // CameraX가 아닌 Camera2등을 사용하는 경우 Bitmap을 얻어와 다음을 참고해 사용 - Bitmap Analyze
        // Local Image Test Code
        /*
        val assetManager: AssetManager = this.assets
        try {
            val inputStream = assetManager.open("test.png")
            val rawBitmap = BitmapFactory.decodeStream(inputStream, null, null)!!

            GazeBitmapAnalyzer(this, this, arrayOf(GazeBitmapAnalysisType.concentraiton, GazeBitmapAnalysisType.gaze))
                .bitmapAnalyze(rawBitmap) // Local Image가 아닌 카메라를 통해 얻은 Bitmap인 경우, rawBitmap대신 현재 카메라를 통해 얻은 Bitmap 삽입
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        */
    }
}