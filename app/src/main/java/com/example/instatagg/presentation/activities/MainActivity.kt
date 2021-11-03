package com.example.instatagg.presentation.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
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

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
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

        viewModel.getTaggs().observe(this, {
            refreshAdapter(it)
        })

        binding.cameraCaptureButton.setOnClickListener {
            if(adapter.itemCount == 0){
                Toast.makeText(this, "Add a Tagg first!", Toast.LENGTH_SHORT).show()
            } else takePhoto()
        }

        binding.openGaleryButton.setOnClickListener {
            startActivity(Intent(this, TaggsActivity::class.java))
        }

        setCurrentTagg(getCurrentTagg())

    }

    private fun insertPhoto(photoFile: File, tagg: Tagg) {
    val photo = Photo(photoFile.path, tagg,null)
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

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
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
                        insertPhoto(photoFile, getCurrentTagg())
                        Toast.makeText(applicationContext, "Photo capture success!", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun saveCurrentTagg(tagg: Tagg){
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


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
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