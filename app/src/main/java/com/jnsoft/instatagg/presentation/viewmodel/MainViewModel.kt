package com.jnsoft.instatagg.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.domain.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun insertPhoto(photo: Photo){
        viewModelScope.launch { mainRepository.insertPhoto(photo) }
    }
    fun getTaggs(): LiveData<List<Tagg>> = liveData(Dispatchers.IO) {
        emit(mainRepository.getTaggs())
    }
}