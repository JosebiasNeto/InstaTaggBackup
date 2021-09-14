package com.example.instatagg.di

import com.example.instatagg.data.db.PhotosDatabase
import com.example.instatagg.domain.repository.PhotosDBDataSource
import com.example.instatagg.presentation.viewmodel.MainRepository
import com.example.instatagg.presentation.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Modules {
    val ui = module {
        viewModel {
            MainViewModel(
                MainRepository(
                PhotosDBDataSource(PhotosDatabase.getDatabase(androidApplication()).photosDao())))
        }
    }
}