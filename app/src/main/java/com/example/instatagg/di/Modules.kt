package com.example.instatagg.di

import com.example.instatagg.data.database.MainDatabase
import com.example.instatagg.domain.repository.MainRepository
import com.example.instatagg.presentation.viewmodel.FullscreanPhotoViewModel
import com.example.instatagg.presentation.viewmodel.MainViewModel
import com.example.instatagg.presentation.viewmodel.PhotosViewModel
import com.example.instatagg.presentation.viewmodel.TaggsViewModel
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