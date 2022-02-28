package com.jnsoft.instatagg.presentation.camera

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.FragmentCameraBinding
import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.utils.Constants.REQUEST_CODE_PERMISSIONS
import com.jnsoft.instatagg.utils.Constants.REQUIRED_PERMISSIONS
import com.jnsoft.instatagg.utils.OnItemClickListener
import com.jnsoft.instatagg.utils.addOnItemClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var adapter: MiniTaggsAdapter
    private val viewModel: CameraViewModel by viewModel()
    private lateinit var cameraService: CameraService
    private val args: CameraFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false)

        cameraService = CameraService(context!!, this, binding)
        cameraExecutor = Executors.newSingleThreadExecutor()

        firstOpen()
        checkPermissions()
        setupMiniRecyclerView()
        setupSettings()
        setupTaggs()

        binding.cameraCaptureButton.setOnClickListener {
            if(adapter.itemCount == 0){
                Toast.makeText(context, getString(com.jnsoft.instatagg.R.string.without_tagg),
                    Toast.LENGTH_SHORT).show()
            } else cameraService.takePhoto(getCurrentFlash())
        }

        binding.openGaleryButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_cameraFragement_to_taggsFragment)
        }

        return binding.root
    }

    private fun firstOpen() {
        val firstOpen = context!!.getSharedPreferences("firstOpen", Context.MODE_PRIVATE)
        val isFirstOpen = firstOpen.getBoolean("firstOpen", true)
        if(isFirstOpen){
            viewModel.insertTagg(Tagg(null,resources.getString(com.jnsoft.instatagg.R.string.job),
                resources.getColor(com.jnsoft.instatagg.R.color.blue), 0))
            viewModel.insertTagg(Tagg(null,resources.getString(com.jnsoft.instatagg.R.string.vacation),
                resources.getColor(com.jnsoft.instatagg.R.color.green), 0))
            viewModel.insertTagg(Tagg(null,resources.getString(com.jnsoft.instatagg.R.string.family),
                resources.getColor(com.jnsoft.instatagg.R.color.red), 0))
        }
        val save = firstOpen.edit()
        save.putBoolean("firstOpen", false)
        save.apply()
    }

    private fun checkPermissions() {
        if (allPermissionsGranted()) {
            cameraService.startCamera(cameraService.getCurrentCamera())
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraService.startCamera(cameraService.getCurrentCamera())
            } else {
                Toast.makeText(context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        context?.let { it1 ->
            ContextCompat.checkSelfPermission(it1, it)
        } == PackageManager.PERMISSION_GRANTED }

    private fun setupMiniRecyclerView() {
        adapter = MiniTaggsAdapter(arrayListOf())
        val orientation = this.resources.configuration.orientation
        val llm = LinearLayoutManager(context)
        llm.reverseLayout = true
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            llm.orientation = RecyclerView.VERTICAL
        } else {
            llm.orientation = RecyclerView.HORIZONTAL
        }
        binding.rvChangeTagg.layoutManager = llm
        binding.rvChangeTagg.isVisible = false
        binding.rvChangeTagg.adapter = adapter
        binding.rvChangeTagg.addOnItemClickListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                choseTagg(position)
            }
        })
    }

    private fun setupSettings() {
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
            if(cameraService.getCurrentCamera() == CameraSelector.DEFAULT_BACK_CAMERA){
                cameraService.saveCurrentCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
                cameraService.startCamera(cameraService.getCurrentCamera())
            } else {
                cameraService.saveCurrentCamera(CameraSelector.DEFAULT_BACK_CAMERA)
                cameraService.startCamera(cameraService.getCurrentCamera())
            }
            removeSettingsVisibility()
        }
    }
    private fun getCurrentFlash(): Int {
        val currentFlash = context!!.getSharedPreferences("currentFlash", Context.MODE_PRIVATE)
        return when (currentFlash.getInt("currentFlash", ImageCapture.FLASH_MODE_OFF)) {
            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_ON
            ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_AUTO
            else -> {ImageCapture.FLASH_MODE_OFF}
        }
    }

    private fun setCurrentFlash(flashMode: Int){
        val currentFlash = context!!.getSharedPreferences("currentFlash", Context.MODE_PRIVATE)
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

    private fun setupTaggs() {
        binding.cvChoseTagg!!.setOnClickListener {
            binding.rvChangeTagg.isVisible = !binding.rvChangeTagg.isVisible
        }
        viewModel.taggs.observe(this, {
            refreshAdapter(it)
            if(it.isEmpty()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    saveCurrentTagg(Tagg(0,getString(com.jnsoft.instatagg.R.string.no_taggs), resources.getColor(com.jnsoft.instatagg.R.color.accent),0))
                }
            } else {
                val listOfIds = arrayListOf<Long>()
                for(i in it.indices){
                    if(getCurrentTagg().id == it[i].id){
                        saveCurrentTagg(it[i])
                        it[i].id?.let { it1 -> listOfIds.add(it1) }
                    }
                }
                if(listOfIds.isEmpty()) saveCurrentTagg(it[0])
            }
        })
        if(args.taggid != 0L){
            viewModel.taggs.value!!.map { if(it.id == args.taggid) saveCurrentTagg(it) }
        }
        setCurrentTagg(getCurrentTagg())
    }

    private fun refreshAdapter(taggs: List<Tagg>){
        adapter.apply {
            addTaggs(taggs)
            notifyDataSetChanged()
        }
    }

    private fun setCurrentTagg(tagg: Tagg){
        binding.choseTaggText.text = tagg.name
        binding.cvChoseTagg!!.setCardBackgroundColor(tagg.color)
    }

    private fun choseTagg(position: Int) {
        val tagg = adapter.getTagg(position)
        setCurrentTagg(tagg)
        binding.rvChangeTagg.isVisible = false
        saveCurrentTagg(tagg)
    }

    fun saveCurrentTagg(tagg: Tagg){
        val currentTagg = context!!.getSharedPreferences("currentTagg", Context.MODE_PRIVATE)
        val save = currentTagg.edit()
        tagg.id?.let { save.putLong("currentTaggId", it) }
        tagg.name?.let { save.putString("currentTaggName", it) }
        tagg.color?.let { save.putInt("currentTaggColor", it) }
        save.apply()
        setCurrentTagg(getCurrentTagg())
    }

    fun getCurrentTagg(): Tagg{
        val currentTagg = context!!.getSharedPreferences("currentTagg", Context.MODE_PRIVATE)
        val tagg: Tagg = Tagg(0,"", resources.getColor(android.R.color.white), 0)
        tagg.id = currentTagg.getLong("currentTaggId", 0)
        tagg.name = currentTagg.getString("currentTaggName", "")!!
        tagg.color = currentTagg.getInt("currentTaggColor", resources.getColor(android.R.color.white))
        return tagg
    }

    fun insertPhoto(photoFile: String, tagg: Tagg, size: Long) {
        val photo = Photo(photoFile, tagg,null)
        viewModel.insertPhoto(photo)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}