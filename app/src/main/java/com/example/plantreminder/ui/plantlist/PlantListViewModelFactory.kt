package com.example.plantreminder.ui.plantlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plantreminder.data.repository.PlantRepository

class PlantListViewModelFactory(private val repository: PlantRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
