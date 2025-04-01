package com.example.task7.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.task7.data.AdvertRepository

class AdvertViewModelFactory(private val repository: AdvertRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdvertViewModel(repository) as T
    }
}