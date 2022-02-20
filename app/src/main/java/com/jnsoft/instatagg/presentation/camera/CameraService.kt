package com.jnsoft.instatagg.presentation.camera

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaActionSound
import android.net.Uri
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.jnsoft.instatagg.databinding.FragmentCameraBinding
import com.jnsoft.instatagg.utils.Constants
import com.jnsoft.instatagg.utils.FirebaseAnalytics
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraService (val context: Context, val fragment: CameraFragment, val binding: FragmentCameraBinding) {

    private var camera: Camera? = null
    private lateinit var outputDirectory: File
    private var imageCapture: ImageCapture? = null

    @SuppressLint("ClickableViewAccessibility", "RestrictedApi")
    fun startCamera(cameraSelector: CameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(Runnable {
            val outMetrics = DisplayMetrics()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                val display = context.display
                display?.getRealMetrics(outMetrics)
            } else {
                @Suppress("DEPRECATION")
                val display = fragment.activity!!.windowManager.defaultDisplay
                @Suppress("DEPRECATION")
                display.getMetrics(outMetrics)
            }

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetResolution(Size(outMetrics.widthPixels, outMetrics.heightPixels))
                .build()
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                fragment, cameraSelector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(context))
        val scaleGestureDetector = ScaleGestureDetector(context, listener)
        binding.viewFinder.setOnTouchListener { view, motionEvent ->
            scaleGestureDetector.onTouchEvent(motionEvent)
            return@setOnTouchListener true
        }
    }
    val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scale = detector.scaleFactor * camera!!.cameraInfo.zoomState.value!!.zoomRatio
            camera!!.cameraControl.setZoomRatio(scale)
            return true
        }
    }

    fun takePhoto(flashMode: Int) {
        outputDirectory = getOutputDirectory()
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                Constants.FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.flashMode = flashMode
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(Constants.TAG, "Photo capture failed: ${exc.message}", exc)
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Log.d(Constants.TAG, msg)
                    if(fragment.getCurrentTagg().name.isEmpty()){
                        Toast.makeText(context.applicationContext,"Photo capture failed!", Toast.LENGTH_SHORT).show()
                    } else {
                        savedUri.toString().let {fragment.insertPhoto(it, fragment.getCurrentTagg(), (photoFile.length())) }
                        MediaActionSound().play(MediaActionSound.SHUTTER_CLICK)
                        flashEffect()
                        if(getCurrentCamera() == CameraSelector.DEFAULT_BACK_CAMERA) FirebaseAnalytics.eventTakePhotoBack()
                        if(getCurrentCamera() == CameraSelector.DEFAULT_FRONT_CAMERA) FirebaseAnalytics.eventTakePhotoSelfie()
                    }
                }
            })
    }
    private fun getOutputDirectory(): File {
        return context.filesDir
    }
    private fun flashEffect(){
        binding.flashScreen.visibility = View.VISIBLE
        Handler().postDelayed({
            binding.flashScreen.visibility = View.GONE
        }, 100)
    }
    fun saveCurrentCamera(cameraSelector: CameraSelector){
        val currentCamera = context.getSharedPreferences("currentCamera", Context.MODE_PRIVATE)
        val save = currentCamera.edit()
        val defaultCamera : Boolean = cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
        save.putBoolean("currentCamera", defaultCamera)
        save.apply()
    }

    fun getCurrentCamera(): CameraSelector{
        val currentCamera = context.getSharedPreferences("currentCamera", Context.MODE_PRIVATE)
        val defaultCamera = currentCamera.getBoolean("currentCamera", true)
        return if (defaultCamera){
            CameraSelector.DEFAULT_BACK_CAMERA
        } else CameraSelector.DEFAULT_FRONT_CAMERA
    }
}