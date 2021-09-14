package com.example.instatagg.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.instatagg.domain.model.PhotoEntity
import kotlinx.coroutines.Dispatchers

class MainViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun insert(photoEntity: PhotoEntity) = liveData(Dispatchers.IO) {
        emit(mainRepository.insert(photoEntity = photoEntity))
    }
}