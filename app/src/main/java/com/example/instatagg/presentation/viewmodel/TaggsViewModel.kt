package com.example.instatagg.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.domain.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaggsViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun getTaggs(): LiveData<List<Tagg>> = liveData(Dispatchers.IO) {
        emit(mainRepository.getTaggs())
    }
    fun insertTagg(tagg: Tagg){
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.insertTagg(tagg)
        }
    }

}