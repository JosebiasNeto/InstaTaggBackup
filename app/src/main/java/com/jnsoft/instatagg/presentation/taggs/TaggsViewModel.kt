package com.jnsoft.instatagg.presentation.taggs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.domain.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaggsViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _taggs = MutableLiveData<List<Tagg>>()
    val taggs: LiveData<List<Tagg>> = _taggs

    fun getTaggs(){
        viewModelScope.launch {
            _taggs.value = mainRepository.getTaggs()
        }
    }
    fun insertTagg(tagg: Tagg){
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.insertTagg(tagg)
        }
    }

}