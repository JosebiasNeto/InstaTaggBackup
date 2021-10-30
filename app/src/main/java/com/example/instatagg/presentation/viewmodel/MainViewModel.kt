package com.example.instatagg.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.instatagg.domain.model.PhotoEntity
import com.example.instatagg.domain.repository.MainRepository
import kotlinx.coroutines.Dispatchers

class MainViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun insertPhoto(photoEntity: PhotoEntity) = liveData(Dispatchers.IO){
        emit(mainRepository.insertPhoto(photoEntity = photoEntity))
    }
}