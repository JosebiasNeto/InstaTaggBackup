package com.example.instatagg.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.instatagg.domain.model.PhotoEntity
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.domain.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun insertPhoto(photoEntity: PhotoEntity) = liveData(Dispatchers.IO){
        emit(mainRepository.insertPhoto(photoEntity = photoEntity))
    }
    fun getPhotos(name: String): LiveData<List<PhotoEntity>> = liveData(Dispatchers.IO) {
        emit(mainRepository.getPhotos(name))
    }
    fun changeTagg(name: String, newTagg:String) = liveData(Dispatchers.IO){
        emit(mainRepository.changeTagg(name, newTagg))
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
    fun insertTagg(tagg: Tagg){
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.insertTagg(tagg)
        }
    }
    fun getTaggs(): LiveData<List<Tagg>> = liveData(Dispatchers.IO) {
        emit(mainRepository.getTaggs())
    }
    fun changeTaggName(name: String, newTagg: String) = liveData(Dispatchers.IO){
        emit(mainRepository.changeTaggName(name, newTagg))
    }
    fun delTagg(id: Int) = liveData(Dispatchers.IO){
        emit(mainRepository.delTagg(id))
    }
    fun clearTaggs() = liveData(Dispatchers.IO){
        emit(mainRepository.clearTaggs())
    }
}