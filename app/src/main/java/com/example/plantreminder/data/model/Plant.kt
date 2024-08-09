package com.example.plantreminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val lastWateredDate: Date,
    val wateringFrequency: Int,
    val lastFertilizedDate: Date,
    val fertilizingFrequency: Int,
    val additionalNotes: String = "",
    val imagePath: String = ""
)
