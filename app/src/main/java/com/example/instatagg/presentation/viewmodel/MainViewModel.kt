package com.example.instatagg.presentation.viewmodel

import androidx.lifecycle.*
import com.example.instatagg.domain.model.Photo
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.domain.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun insertPhoto(photo: Photo){
        viewModelScope.launch { mainRepository.insertPhoto(photo) }
    }
    fun getTaggs(): LiveData<List<Tagg>> = liveData(Dispatchers.IO) {
        emit(mainRepository.getTaggs())
    }

    var currentTagg = MutableLiveData<Tagg>()

    fun setTagg(tagg: Tagg){
        currentTagg.value = tagg
    }
    fun getTagg(): Tagg? {
        return currentTagg.value
    }

}