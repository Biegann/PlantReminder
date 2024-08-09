package com.example.plantreminder.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.plantreminder.data.model.Plant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Insert
    suspend fun insert(plant: Plant)

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getPlantById(id: Int): Plant?

    @Query("SELECT * FROM plants")
    fun getAllPlants(): Flow<List<Plant>>

    @Query("DELETE FROM plants WHERE id = :id")
    suspend fun deletePlantById(id: Int)

    @Query("DELETE FROM plants")
    suspend fun deleteAllPlants()

    @Delete
    suspend fun deletePlant(plant: Plant)
}
