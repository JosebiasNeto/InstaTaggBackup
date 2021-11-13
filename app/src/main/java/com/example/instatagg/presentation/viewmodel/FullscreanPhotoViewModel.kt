package com.example.instatagg.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instatagg.domain.repository.MainRepository
import kotlinx.coroutines.launch

class FullscreanPhotoViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun delPhoto(id: Long){
        viewModelScope.launch { mainRepository.delPhoto(id) }
    }

}