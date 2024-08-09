package com.example.plantreminder.ui.addplant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plantreminder.data.repository.PlantRepository

class AddPlantViewModelFactory(
    private val repository: PlantRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPlantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddPlantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
