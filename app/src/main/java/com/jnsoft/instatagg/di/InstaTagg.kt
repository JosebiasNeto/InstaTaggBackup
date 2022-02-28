package com.jnsoft.instatagg.di

import android.app.Application
import android.content.Context
import com.jnsoft.instatagg.di.Modules.data
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class InstaTagg : Application() {

    init {
        instance = this
    }
    companion object{
        private var instance: InstaTagg? = null
        fun getAppContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@InstaTagg)
            modules(data)
        }
    }

}