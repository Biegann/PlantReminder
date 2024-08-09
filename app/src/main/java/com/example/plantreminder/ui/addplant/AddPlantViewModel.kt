package com.example.plantreminder.ui.addplant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantreminder.data.model.Plant
import com.example.plantreminder.data.repository.PlantRepository
import kotlinx.coroutines.launch

class AddPlantViewModel(private val repository: PlantRepository) : ViewModel() {

    fun addPlant(plant: Plant) {
        viewModelScope.launch {
            repository.addPlant(plant)
        }
    }
}