package com.example.plantreminder.data.repository

import com.example.plantreminder.data.dao.PlantDao
import com.example.plantreminder.data.model.Plant
import kotlinx.coroutines.flow.Flow

class PlantRepository(private val plantDao: PlantDao) {

    // Użyj Flow, aby obserwować zmiany w bazie danych
    val plants: Flow<List<Plant>> = plantDao.getAllPlants()

    suspend fun addPlant(plant: Plant) {
        plantDao.insert(plant)
    }

    suspend fun getPlantById(id: Int): Plant? {
        return plantDao.getPlantById(id)
    }

    suspend fun deletePlantById(id: Int) {
        plantDao.deletePlantById(id)
    }

    suspend fun deletePlant(plant: Plant) {
        plantDao.deletePlant(plant)
    }

    suspend fun deleteAllPlants() {
        plantDao.deleteAllPlants()
    }

    suspend fun updatePlant(plant: Plant) {
        plantDao.updatePlant(plant)
    }
}
