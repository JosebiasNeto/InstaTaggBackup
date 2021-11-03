package com.example.instatagg.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.instatagg.domain.model.Photo
import com.example.instatagg.domain.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotosViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun getPhotos(id: Long): LiveData<List<Photo>> = liveData(Dispatchers.IO) {
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
    fun clearTaggs() = liveData(Dispatchers.IO){
        emit(mainRepository.clearTaggs())
    }
    fun changeTaggColor(id: Long, newColor: Int){
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.changeTaggColor(id, newColor) }
    }
    fun changeTagg(newTaggName: String, newTaggColor: Int, newTaggId: Long, currentTaggId: Long)
            = liveData(Dispatchers.IO){ emit(mainRepository.changeTagg(
        newTaggName, newTaggColor, newTaggId, currentTaggId))
    }
    fun delPhoto(id: Int) = liveData(Dispatchers.IO) {
        emit(mainRepository.delPhoto(id))
    }
    fun clearTagg(name: String) = liveData(Dispatchers.IO) {
        emit(mainRepository.clearTagg(name))
    }

    fun movePhoto(name: String, id: Int) = liveData(Dispatchers.IO) {
        emit(mainRepository.movePhoto(name, id))
    }

    fun importPhoto(path: String, name: String, color: String) = liveData(Dispatchers.IO) {
        emit(mainRepository.importPhoto(path, name, color))
    }

}