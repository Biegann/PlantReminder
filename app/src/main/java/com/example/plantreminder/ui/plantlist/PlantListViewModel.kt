package com.example.plantreminder.ui.plantlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantreminder.data.model.Plant
import com.example.plantreminder.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class PlantListViewModel(private val repository: PlantRepository) : ViewModel() {

    // StateFlow to hold the list of plants
    private val _plants = MutableStateFlow<List<Plant>>(emptyList())
    val plants: StateFlow<List<Plant>> = _plants

    init {
        // Launch a coroutine in viewModelScope to collect the Flow from the repository
        viewModelScope.launch {
            repository.plants
                .collect { plantList ->
                    _plants.value = plantList
                }
        }
    }
    fun clearAllPlants() {
        viewModelScope.launch {
            repository.deleteAllPlants()
        }
    }

    fun deletePlant(plant: Plant) {
        viewModelScope.launch {
            repository.deletePlant(plant)
        }
    }

    fun updatePlant(plant: Plant) {
        viewModelScope.launch {
            repository.updatePlant(plant)
        }
    }

    fun updateWateredDate(plant: Plant) {
        viewModelScope.launch {
            repository.updatePlant(plant.copy(lastWateredDate = Date()))
        }
    }

    fun updateFertilizedDate(plant: Plant) {
        viewModelScope.launch {
            repository.updatePlant(plant.copy(lastFertilizedDate = Date()))
        }
    }
}
