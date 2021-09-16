package com.example.instatagg.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.instatagg.domain.model.PhotoEntity
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.domain.repository.MainRepository

class MainViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun insertPhoto(photoEntity: PhotoEntity) = liveData{
        emit(mainRepository.insertPhoto(photoEntity = photoEntity))
    }
    fun getPhotos(name: String): LiveData<List<PhotoEntity>> = liveData {
        emit(mainRepository.getPhotos(name))
    }
    fun changeTagg(name: String, newTagg:String) = liveData{
        emit(mainRepository.changeTagg(name, newTagg))
    }
    fun delPhoto(id: Int) = liveData {
        emit(mainRepository.delPhoto(id))
    }
    fun clearTagg(name: String) = liveData {
        emit(mainRepository.clearTagg(name))
    }

    fun movePhoto(name: String, id: Int) = liveData {
        emit(mainRepository.movePhoto(name, id))
    }

    fun importPhoto(path: String, name: String, color: String) = liveData {
        emit(mainRepository.importPhoto(path, name, color))
    }
    fun insertTagg(tagg: Tagg) = liveData{
        emit(mainRepository.insertTagg(tagg))
    }
    fun getTaggs(): LiveData<List<Tagg>> = liveData {
        emit(mainRepository.getTaggs())
    }
    fun changeTaggName(name: String, newTagg: String) = liveData{
        emit(mainRepository.changeTaggName(name, newTagg))
    }
    fun delTagg(id: Int) = liveData{
        emit(mainRepository.delTagg(id))
    }
    fun clearTaggs() = liveData{
        emit(mainRepository.clearTaggs())
    }
}