package com.jnsoft.instatagg.di

import com.jnsoft.instatagg.data.database.MainDatabase
import com.jnsoft.instatagg.domain.repository.MainRepository
import com.jnsoft.instatagg.presentation.camera.CameraViewModel
import com.jnsoft.instatagg.presentation.photos.PhotosViewModel
import com.jnsoft.instatagg.presentation.taggs.TaggsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Modules {
    val data = module {
        factory {
            MainRepository(MainDatabase.getDatabase(androidApplication()).photosDao(),
                MainDatabase.getDatabase(androidApplication()).taggsDao())
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