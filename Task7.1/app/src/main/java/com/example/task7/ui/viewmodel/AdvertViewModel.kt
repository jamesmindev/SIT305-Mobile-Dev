package com.example.task7.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task7.data.AdvertRepository
import com.example.task7.model.Advert
import kotlinx.coroutines.launch

class AdvertViewModel(private val repository: AdvertRepository) : ViewModel() {
    val allAdverts = repository.allAdverts

    fun getAdvertById(advertId: Int): LiveData<Advert> {
        return repository.getAdvertById(advertId)
    }

    fun addAdvert(advert: Advert) = viewModelScope.launch {
        repository.insert(advert)
    }

    fun updateAdvert(advert: Advert) = viewModelScope.launch {
        repository.update(advert)
    }

    fun deleteTask(advert: Advert) = viewModelScope.launch {
        repository.delete(advert)
    }
}