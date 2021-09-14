package com.example.instatagg.di

import android.app.Application
import com.example.instatagg.di.Modules.ui
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class InstaTagg : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@InstaTagg)
            modules(ui)
        }
    }

}