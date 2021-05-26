package com.example.instatagg

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }



    @RequiresApi(Build.VERSION_CODES.KITKAT)
    abstract class CameraDevice : AutoCloseable {


    }

}