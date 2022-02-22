package com.jnsoft.instatagg.presentation.photos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _taggs = MutableLiveData<List<Tagg>>()
    val taggs: LiveData<List<Tagg>> = _taggs

    private val _photosSelected = MutableLiveData<ArrayList<Photo>>(arrayListOf())
    val photosSelected: LiveData<ArrayList<Photo>> = _photosSelected

    private val _photoFullscreen = MutableLiveData<Int>()
    val photoFullscreen: LiveData<Int> = _photoFullscreen

    fun getTagg(id: Long){
        viewModelScope.launch { _tagg.value = mainRepository.getTagg(id) }
    }

    fun getPhotos(id: Long) {
        viewModelScope.launch {
            _photos.value = mainRepository.getPhotos(id)
        }
    }

    fun getTaggs(){
        viewModelScope.launch { _taggs.value = mainRepository.getTaggs() }
    }

    fun setFullscreenPhoto(position: Int){
        _photoFullscreen.value = position
    }

    fun setCheckboxPhotosVisible(){
        _photos.value!!.map { it.checkboxVisibility = true }
    }
    fun setCheckboxPhotosInvisible(){
        _photos.value!!.map { it.checkboxVisibility = false }
    }

    fun addOrRemovePhotoSelected(photoId: Long){
        val photo = _photos.value!!.find { it.id == photoId }
        if(_photosSelected.value!!.contains(photo)) _photosSelected.value!!.remove(photo)
        else _photosSelected.value!!.add(photo!!)
    }

    fun changeTaggName(id: Long, newTagg: String){
        viewModelScope.launch {
            mainRepository.changeTaggName(id, newTagg)
        }
    }
    fun changeTaggColor(id: Long, newColor: Int){
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.changeTaggColor(id, newColor) }
    }

    fun delTagg(id: Long){
        viewModelScope.launch(Dispatchers.IO) { mainRepository.delTagg(id) }
    }

    fun movePhoto(newTaggName: String, newTaggColor: Int, newTaggId: Long, id: Long,
                  size: Long, oldTaggId: Long){
        viewModelScope.launch { mainRepository.movePhoto(newTaggName, newTaggColor, newTaggId, id,
                                                         size, oldTaggId) }
    }

    fun delPhoto(photo: Photo, size: Long){
        viewModelScope.launch { mainRepository.delPhoto(photo, size) }
    }

    fun clearTagg(id: Long) {
        viewModelScope.launch { mainRepository.clearTagg(id) }
    }

    fun insertPhoto(photo: Photo, size: Long){
        viewModelScope.launch { mainRepository.insertPhoto(photo, size) }
    }

}