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

class FullscreanPhotoViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun delPhoto(photo: Photo, size: Int){
        viewModelScope.launch { mainRepository.delPhoto(photo, size) }
    }
    fun movePhoto(newTaggName: String, newTaggColor: Int, newTaggId: Long, id: Long){
        viewModelScope.launch { mainRepository.movePhoto(newTaggName, newTaggColor, newTaggId, id) }
    }
    fun getTaggs(): LiveData<List<Tagg>> = liveData(Dispatchers.IO) {
        emit(mainRepository.getTaggs())
    }
    fun insertPhoto(photo: Photo, size: Int){
        viewModelScope.launch { mainRepository.insertPhoto(photo, size) }
    }
    fun getPhotos(id: Long) = liveData(Dispatchers.IO) {
        emit(mainRepository.getPhotos(id))
    }
}