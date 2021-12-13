package com.example.instatagg.presentation.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaActionSound
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instatagg.R
import com.example.instatagg.databinding.ActivityMainBinding
import com.example.instatagg.domain.model.Photo
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.presentation.adapter.MainAdapter
import com.example.instatagg.presentation.viewmodel.MainViewModel
import com.example.instatagg.utils.Constants.FILENAME_FORMAT
import com.example.instatagg.utils.Constants.REQUEST_CODE_PERMISSIONS
import com.example.instatagg.utils.Constants.REQUIRED_PERMISSIONS
import com.example.instatagg.utils.Constants.TAG
import com.example.instatagg.utils.OnItemClickListener
import com.example.instatagg.utils.addOnItemClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors





class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var adapter: MainAdapter
    private var camera: Camera? = null
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (allPermissionsGranted()) {
            startCamera(getCurrentCamera())
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        adapter = MainAdapter(arrayListOf())
        val llm = LinearLayoutManager(this)
        llm.reverseLayout = true

        binding.rvChangeTagg.isVisible = false
        binding.rvChangeTagg.adapter = adapter
        binding.rvChangeTagg.layoutManager = llm
        binding.rvChangeTagg.addOnItemClickListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                choseTagg(position)
            }
        })

        binding.choseTaggColor.setOnClickListener {
            binding.rvChangeTagg.isVisible = !binding.rvChangeTagg.isVisible
        }
        binding.openSettingsButton.setOnClickListener {
            binding.btnFlipCamera.isVisible = !binding.btnFlipCamera.isVisible
            when(getCurrentFlash()){
                ImageCapture.FLASH_MODE_ON -> binding.btnFlashOn.isVisible = !binding.btnFlashOn.isVisible
                ImageCapture.FLASH_MODE_AUTO -> binding.btnFlashAuto.isVisible = !binding.btnFlashAuto.isVisible
                ImageCapture.FLASH_MODE_OFF -> binding.btnFlashOff.isVisible = !binding.btnFlashOff.isVisible
            }
        }
        binding.btnFlashOn.setOnClickListener { setCurrentFlash(ImageCapture.FLASH_MODE_AUTO)
                                                binding.btnFlashOn.visibility = View.GONE
                                                binding.btnFlashAuto.visibility = View.VISIBLE}
        binding.btnFlashAuto.setOnClickListener { setCurrentFlash(ImageCapture.FLASH_MODE_OFF)
                                                binding.btnFlashAuto.visibility = View.GONE
                                                binding.btnFlashOff.visibility = View.VISIBLE}
        binding.btnFlashOff.setOnClickListener { setCurrentFlash(ImageCapture.FLASH_MODE_ON)
                                                binding.btnFlashOff.visibility = View.GONE
                                                binding.btnFlashOn.visibility = View.VISIBLE}

        binding.btnFlipCamera.setOnClickListener {
            if(getCurrentCamera() == CameraSelector.DEFAULT_BACK_CAMERA){
                saveCurrentCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
                startCamera(getCurrentCamera())
            } else {
                saveCurrentCamera(CameraSelector.DEFAULT_BACK_CAMERA)
                startCamera(getCurrentCamera())
            }
            removeSettingsVisibility()
        }

        viewModel.getTaggs().observe(this, {
            refreshAdapter(it)
        })

        binding.cameraCaptureButton.setOnClickListener {
            if(adapter.itemCount == 0){
                Toast.makeText(this, "Add a Tagg first!", Toast.LENGTH_SHORT).show()
            } else takePhoto(getCurrentFlash())
        }

        binding.openGaleryButton.setOnClickListener {
            startActivity(Intent(this, TaggsActivity::class.java))
            overridePendingTransition(0,0)
        }
        val tagg : Tagg? = intent.getParcelableExtra<Tagg>("tagg")
        if (tagg != null) {
            saveCurrentTagg(tagg)
        }
        setCurrentTagg(getCurrentTagg())
    }

    private fun getCurrentFlash(): Int {
        val currentFlash = this.getSharedPreferences("currentFlash", Context.MODE_PRIVATE)
        return when (currentFlash.getInt("currentFlash", ImageCapture.FLASH_MODE_OFF)) {
            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_ON
            ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_AUTO
            else -> {ImageCapture.FLASH_MODE_OFF}
        }
    }
    private fun setCurrentFlash(flashMode: Int){
        val currentFlash = this.getSharedPreferences("currentFlash", Context.MODE_PRIVATE)
        val save = currentFlash.edit()
        when(flashMode){
            ImageCapture.FLASH_MODE_ON -> save.putInt("currentFlash", ImageCapture.FLASH_MODE_ON)
            ImageCapture.FLASH_MODE_AUTO -> save.putInt("currentFlash", ImageCapture.FLASH_MODE_AUTO)
            ImageCapture.FLASH_MODE_OFF -> save.putInt("currentFlash", ImageCapture.FLASH_MODE_OFF)
        }
        save.apply()
    }

    private fun removeSettingsVisibility() {
        binding.btnFlipCamera.isVisible = false
        binding.btnFlashAuto.isVisible = false
        binding.btnFlashOff.isVisible = false
        binding.btnFlashOn.isVisible = false
    }

    private fun insertPhoto(photoFile: String, tagg: Tagg) {
    val photo = Photo(photoFile, tagg,null)
        viewModel.insertPhoto(photo)
    }
    private fun refreshAdapter(taggs: List<Tagg>){
        adapter.apply {
            addTaggs(taggs)
            notifyDataSetChanged()
        }
    }

    private fun setCurrentTagg(tagg: Tagg){
        binding.choseTaggText.text = tagg.name
        binding.choseTaggColor.setBackgroundColor(tagg.color)
    }

    private fun choseTagg(position: Int) {
        val tagg = adapter.getTagg(position)
        setCurrentTagg(tagg)
        binding.rvChangeTagg.isVisible = false
        saveCurrentTagg(tagg)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun startCamera(cameraSelector: CameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(this))
        val scaleGestureDetector = ScaleGestureDetector(this, listener)
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

    private fun takePhoto(flashMode: Int) {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.flashMode = flashMode
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Log.d(TAG, msg)
                    if(getCurrentTagg().name.isEmpty()){
                        Toast.makeText(applicationContext,"Photo capture failed!",Toast.LENGTH_SHORT).show()
                    } else {
                        savedUri.toString().let { insertPhoto(it, getCurrentTagg()) }
                        MediaActionSound().play(MediaActionSound.SHUTTER_CLICK)
                        flashEffect()
                    }
                }
            })
    }
    private fun flashEffect(){
        binding.flashScreen.visibility = View.VISIBLE
        Handler().postDelayed({
            binding.flashScreen.visibility = View.GONE
        }, 100)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED }

    private fun getOutputDirectory(): File {
       return filesDir
    }

    fun saveCurrentTagg(tagg: Tagg){
        val currentTagg = this.getSharedPreferences("currentTagg", Context.MODE_PRIVATE)
        val save = currentTagg.edit()
        tagg.id?.let { save.putLong("currentTaggId", it) }
        tagg.name?.let { save.putString("currentTaggName", it) }
        tagg.color?.let { save.putInt("currentTaggColor", it) }
        save.apply()
    }

    private fun getCurrentTagg(): Tagg{
        val currentTagg = this.getSharedPreferences("currentTagg", Context.MODE_PRIVATE)
        val tagg: Tagg = Tagg(0,"", R.color.white)
        tagg.id = currentTagg.getLong("currentTaggId", 0)
        tagg.name = currentTagg.getString("currentTaggName", "")!!
        tagg.color = currentTagg.getInt("currentTaggColor", R.color.white)
        return tagg
    }
    private fun saveCurrentCamera(cameraSelector: CameraSelector){
        val currentCamera = this.getSharedPreferences("currentCamera", Context.MODE_PRIVATE)
        val save = currentCamera.edit()
        val defaultCamera : Boolean = cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
        save.putBoolean("currentCamera", defaultCamera)
        save.apply()
    }

    private fun getCurrentCamera(): CameraSelector{
        val currentCamera = this.getSharedPreferences("currentCamera", Context.MODE_PRIVATE)
        val defaultCamera = currentCamera.getBoolean("currentCamera", true)
        return if (defaultCamera){
            CameraSelector.DEFAULT_BACK_CAMERA
        } else CameraSelector.DEFAULT_FRONT_CAMERA
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera(getCurrentCamera())
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onBackPressed() {
        this.finishAffinity()
    }
}