package com.jnsoft.instatagg.di

import com.jnsoft.instatagg.data.database.MainDatabase
import com.jnsoft.instatagg.domain.repository.MainRepository
import com.jnsoft.instatagg.presentation.viewmodel.FullscreanPhotoViewModel
import com.jnsoft.instatagg.presentation.viewmodel.MainViewModel
import com.jnsoft.instatagg.presentation.viewmodel.PhotosViewModel
import com.jnsoft.instatagg.presentation.viewmodel.TaggsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Modules {
    val ui = module {
        viewModel {
            MainViewModel(
                MainRepository(
                    MainDatabase.getDatabase(androidApplication()).photosDao(),
                    MainDatabase.getDatabase(androidApplication()).taggsDao()
                )
            )
        }
        viewModel {
            TaggsViewModel(
                MainRepository(
                    MainDatabase.getDatabase(androidApplication()).photosDao(),
                    MainDatabase.getDatabase(androidApplication()).taggsDao()
                )
            )
        }
        viewModel {
            PhotosViewModel(
                MainRepository(
                    MainDatabase.getDatabase(androidApplication()).photosDao(),
                    MainDatabase.getDatabase(androidApplication()).taggsDao()
                )
            )
        }
        viewModel {
            FullscreanPhotoViewModel(
                MainRepository(
                    MainDatabase.getDatabase(androidApplication()).photosDao(),
                    MainDatabase.getDatabase(androidApplication()).taggsDao()
                )
            )
        }
    }
}