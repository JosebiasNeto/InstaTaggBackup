package com.jnsoft.instatagg.utils

import android.Manifest

object Constants {
    const val TAG = "CameraXBasic"
    const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    const val REQUEST_CODE_PERMISSIONS = 123
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
}