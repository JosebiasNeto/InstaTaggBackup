package com.jnsoft.instatagg.di

import android.app.Application
import com.jnsoft.instatagg.di.Modules.data
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class InstaTagg : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@InstaTagg)
            modules(data)
        }
    }

}