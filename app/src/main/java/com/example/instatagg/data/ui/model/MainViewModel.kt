package com.example.instatagg.data.ui.model

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.instatagg.R
import com.example.instatagg.data.ui.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainViewModel: ViewModel() {

    fun checkPermission(){
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                MainActivity, MainViewModel.REQUIRED_PERMISSIONS, MainViewModel.REQUEST_CODE_PERMISSIONS
            )
        }
    }



}