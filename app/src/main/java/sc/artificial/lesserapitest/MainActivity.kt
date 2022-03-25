package sc.artificial.lesserapitest

import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

import sc.artificial.lessersdk.databinding.ActivityMainBinding
import sc.artificial.lessersdk.gaze.GazeAnalysisManager

// CUSTOM LIBRARY TEST
import sc.artificial.lesserapi.permissions.PermissionControl
import sc.artificial.lesserapi.camera.GazeAnalysisDelegate
import sc.artificial.lesserapi.gaze.GazeAnalysisType
import sc.artificial.lesserapitest.databinding.ActivityMainBinding
import sc.artificial.lesserapitest.gaze.GazeAnalysisManager


class MainActivity : AppCompatActivity(), GazeAnalysisDelegate {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (PermissionControl.checkAndGetPermissions(this)) {
            GazeAnalysisManager.initAnalyzer(this, arrayOf(GazeAnalysisType.concentraiton, GazeAnalysisType.gaze))
        }
        GazeAnalysisManager.bindCamera(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PermissionControl.REQUEST_CODE_PERMISSIONS) {
            GazeAnalysisManager.initAnalyzer(this, arrayOf(GazeAnalysisType.concentraiton, GazeAnalysisType.gaze))
        }
        else {
            Toast.makeText(this, "Permissions not granted by the user", Toast.LENGTH_SHORT).show()
        }
    }

    override fun handleConcentration(isConcentrating: Boolean, timestamp: Long) {
//        super.handleConcentration(isConcentrating, timestamp)
        // Do something
        Log.d("GAZE-RESULT", "isConcentrating: ${isConcentrating}")
    }

    override fun handleGazePoint(gazePoint: PointF, timestamp: Long) {
//        super.handleGazePoint(gazePoint, timestamp)
        // Do something
        Log.d("GAZE-RESULT", "x: ${gazePoint.x}, y: ${gazePoint.y}")
    }



}