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

class PhotosViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun getPhotos(id: Long) = liveData(Dispatchers.IO) {
        emit(mainRepository.getPhotos(id))
    }

    fun changeTaggName(id: Long, newTagg: String){
        viewModelScope.launch {
            mainRepository.changeTaggName(id, newTagg)
        }
    }
    fun delTagg(id: Long){
        viewModelScope.launch(Dispatchers.IO) { mainRepository.delTagg(id) }
    }

    fun changeTaggColor(id: Long, newColor: Int){
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.changeTaggColor(id, newColor) }
    }

    fun movePhoto(newTaggName: String, newTaggColor: Int, newTaggId: Long, id: Long){
        viewModelScope.launch { mainRepository.movePhoto(newTaggName, newTaggColor, newTaggId, id) }
    }

    fun getTagg(id: Long): LiveData<Tagg> = liveData(Dispatchers.IO) {
        emit(mainRepository.getTagg(id))
    }

    fun delPhoto(id: Long){
        viewModelScope.launch { mainRepository.delPhoto(id) }
    }

    fun clearTagg(id: Long) {
        viewModelScope.launch { mainRepository.clearTagg(id) }
    }

    fun importPhoto(path: String, name: String, color: String) = liveData(Dispatchers.IO) {
        emit(mainRepository.importPhoto(path, name, color))
    }

    fun getTaggs(): LiveData<List<Tagg>> = liveData(Dispatchers.IO) {
        emit(mainRepository.getTaggs())
    }
    fun insertPhoto(photo: Photo){
        viewModelScope.launch { mainRepository.insertPhoto(photo) }
    }

}