package com.jnsoft.instatagg.di

import com.jnsoft.instatagg.data.local.LocalDataSource
import com.jnsoft.instatagg.data.local.LocalDatabase
import com.jnsoft.instatagg.data.local.MainDatabase
import com.jnsoft.instatagg.data.remote.RemoteDataSource
import com.jnsoft.instatagg.data.remote.RemoteDatabase
import com.jnsoft.instatagg.domain.repository.MainRepository
import com.jnsoft.instatagg.domain.repository.Repository
import com.jnsoft.instatagg.presentation.camera.CameraViewModel
import com.jnsoft.instatagg.presentation.photos.PhotosViewModel
import com.jnsoft.instatagg.presentation.taggs.TaggsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Modules {
    val data = module {
        factory<RemoteDataSource> { RemoteDatabase() }
        factory<LocalDataSource> {
            LocalDatabase(MainDatabase.getDatabase(androidApplication()).photosDao(),
                MainDatabase.getDatabase(androidApplication()).taggsDao())
        }
        factory<MainRepository> {
            Repository(get(), get())
        }
        viewModel {
            CameraViewModel(get())
        }
        viewModel {
            TaggsViewModel(get())
        }
        viewModel {
            PhotosViewModel(get())
        }
    }
}