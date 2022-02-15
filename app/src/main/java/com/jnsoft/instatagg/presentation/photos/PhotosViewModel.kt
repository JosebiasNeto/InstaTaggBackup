package com.jnsoft.instatagg.presentation.photos

import androidx.lifecycle.*
import com.jnsoft.instatagg.domain.model.Photo
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.domain.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotosViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _tagg = MutableLiveData<Tagg>()
    val tagg: LiveData<Tagg> = _tagg

    private val _photos = MutableLiveData<List<Photo>>()
    val photos: LiveData<List<Photo>> = _photos

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

    fun movePhoto(newTaggName: String, newTaggColor: Int, newTaggId: Long, id: Long,
                  size: Long, oldTaggId: Long){
        viewModelScope.launch { mainRepository.movePhoto(newTaggName, newTaggColor, newTaggId, id,
                                                         size, oldTaggId) }
    }

    fun getTagg(id: Long){
        viewModelScope.launch { _tagg.value = mainRepository.getTagg(id) }
    }

    fun delPhoto(photo: Photo, size: Long){
        viewModelScope.launch { mainRepository.delPhoto(photo, size) }
    }

    fun clearTagg(id: Long) {
        viewModelScope.launch { mainRepository.clearTagg(id) }
    }

    fun getTaggs(): LiveData<List<Tagg>> = liveData(Dispatchers.IO) {
        emit(mainRepository.getTaggs())
    }
    fun insertPhoto(photo: Photo, size: Long){
        viewModelScope.launch { mainRepository.insertPhoto(photo, size) }
    }

}