package com.example.instatagg.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.domain.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FullscreanPhotoViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun delPhoto(id: Long){
        viewModelScope.launch { mainRepository.delPhoto(id) }
    }
    fun movePhoto(newTaggName: String, newTaggColor: Int, newTaggId: Long, id: Long){
        viewModelScope.launch { mainRepository.movePhoto(newTaggName, newTaggColor, newTaggId, id) }
    }
    fun getTaggs(): LiveData<List<Tagg>> = liveData(Dispatchers.IO) {
        emit(mainRepository.getTaggs())
    }
}