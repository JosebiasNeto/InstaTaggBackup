package com.jnsoft.instatagg.presentation.taggs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.domain.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaggsViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun getTaggs(): LiveData<List<Tagg>> = liveData(Dispatchers.IO) {
        emit(mainRepository.getTaggs())
    }
    fun insertTagg(tagg: Tagg){
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.insertTagg(tagg)
        }
    }

}