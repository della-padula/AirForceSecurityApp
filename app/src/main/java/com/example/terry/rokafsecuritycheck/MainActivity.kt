package com.example.terry.rokafsecuritycheck

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.media.MediaRecorder
import java.io.File
import android.widget.Toast
import android.Manifest.permission
import android.os.Build


class MainActivity : AppCompatActivity() {
    val PermissionsRequestCode = 123
    lateinit var managePermissions: ManagePermissions
    lateinit var pm : PackageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize a list of required permissions to request runtime
        val list = listOf<String>(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(this,list,PermissionsRequestCode)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            managePermissions.checkPermissions()
        } else {
            pm = this.packageManager

            checkExternalCard()
            checkCamera()
            checkMicAvailability()
        }
    }

    // Receive the permissions request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PermissionsRequestCode ->{
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode,permissions,grantResults)

                if(isPermissionsGranted){
                    // Do the task now
                    toast("Permissions granted.")
                }else{
                    toast("Permissions denied.")
                }
                return
            }
        }
    }

    fun checkExternalCard() : Boolean {
        // Check whether ExternalCard is in the device
        var state : String = Environment.getExternalStorageState()
        var mExternalStorageAvailable = false
        var mExternalStorageWriteable = false

        Log.d("MASTER_LOG", Environment.getExternalStorageState())

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = true
            mExternalStorageWriteable = true
            Log.d("MASTER_LOG : ", "AvailableTrue, WritableTrue")
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true
            mExternalStorageWriteable = false
            Log.d("MASTER_LOG : ", "AvailableTrue, WritableFalse")
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = false
            mExternalStorageWriteable = false
            Log.d("MASTER_LOG : ", "AvailableFalse, WritableFalse")
        }

        return (mExternalStorageAvailable && mExternalStorageWriteable)
    }

    fun checkCamera() : Boolean {
        // Check whether Camera is in the device
        var resultCamera: Boolean
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

        }
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d("MASTER_LOG : ", "Camera TRUE")
            resultCamera = true
        } else {
            Log.d("MASTER_LOG : ", "Camera FALSE")
            resultCamera = false
        }

        // Temporary Code
        var numCameras : Int = Camera.getNumberOfCameras()
        if(numCameras > 0) {
            resultCamera = true;
        }
        Log.d("MASTER_LOG : ", "Number of Cameras : $numCameras")

        return resultCamera
    }

    fun checkMicAvailability() : Boolean {
        var isMicAvailable = getMicrophoneAvailable(this)

        if(isMicAvailable) {
            Log.d("MASTER_LOG : ", "Mic TRUE")
        } else {
            Log.d("MASTER_LOG : ", "Mic FALSE")
        }
        return isMicAvailable
    }

    fun getMicrophoneAvailable(context: Context): Boolean {
        val recorder = MediaRecorder()
        var available = true
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            recorder.setOutputFile(File(context.getCacheDir(), "MediaUtil#micAvailTestFile").getAbsolutePath())

            recorder.prepare()
            recorder.start()

        } catch (exception: Exception) {
            available = false
        }

        recorder.release()
        return available
    }

    // Extension function to show toast message
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
