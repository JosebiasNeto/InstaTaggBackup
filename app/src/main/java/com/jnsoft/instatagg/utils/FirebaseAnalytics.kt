package com.jnsoft.instatagg.utils

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object FirebaseAnalytics {
    val firebaseAnalytics = Firebase.analytics
    fun eventTakePhotoBack(){
        val params = Bundle()
        params.putString("camera", "back_camera")
        firebaseAnalytics.logEvent("take_photo", params)
    }
    fun eventTakePhotoSelfie(){
        val params = Bundle()
        params.putString("camera", "selfie_camera")
        firebaseAnalytics.logEvent("take_photo", params)
    }
    fun eventDeleteTagg(photosCount: Int){
        val params = Bundle()
        params.putInt("tagg_size", photosCount)
        firebaseAnalytics.logEvent("delete_tagg", params)
    }
    fun eventDeletePhoto(){
        val params = Bundle()
        params.putString("photo", "delete_photo")
        firebaseAnalytics.logEvent("delete_photo", params)
    }
    fun eventSharePhoto(){
        val params = Bundle()
        params.putString("photo", "share_photo")
        firebaseAnalytics.logEvent("share_photo", params)
    }
    fun eventImportPhoto(){
        val params = Bundle()
        params.putString("photo", "import_photo")
        firebaseAnalytics.logEvent("import_photo", params)
    }
    fun eventCreateTagg() {
        val params = Bundle()
        params.putString("tagg", "create_tagg")
        firebaseAnalytics.logEvent("create_tagg", params)
    }
}