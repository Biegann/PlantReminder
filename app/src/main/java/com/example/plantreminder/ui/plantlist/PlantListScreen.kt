package com.example.plantreminder.ui.plantlist

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantreminder.data.model.Plant
import com.example.plantreminder.data.repository.PlantRepository
import com.example.plantreminder.data.database.PlantDatabase
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PlantListScreen(
    context: Context,
    viewModel: PlantListViewModel = viewModel(
        factory = PlantListViewModelFactory(PlantRepository(
            PlantDatabase.getDatabase(context).plantDao()
        ))
    ),
    onAddPlantClicked: () -> Unit
) {
    val plants = viewModel.plants.collectAsState(initial = emptyList())

    // Handle state for dialog
    var plantToEdit by remember { mutableStateOf<Plant?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Handle plant deletion
    val handleDelete = { plant: Plant ->
        viewModel.deletePlant(plant)
    }

    // Handle plant edit
    val handleEdit = { plant: Plant ->
        plantToEdit = plant
        showEditDialog = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(plants.value) { plant ->
                PlantItem(
                    plant = plant,
                    onEdit = handleEdit,
                    onDelete = handleDelete
                )
            }
        }
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onAddPlantClicked,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Add Plant")
            }
            Button(
                onClick = { viewModel.clearAllPlants() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Delete all")
            }
        }
    }

    if (showEditDialog && plantToEdit != null) {
        // Show Edit Dialog or Screen here
        // e.g., a new Composable for editing or a navigation to another screen
        // Placeholder for Edit Dialog
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(text = "Edit Plant") },
            text = {
                // Add your Edit Plant UI here
                Text("Edit ${plantToEdit?.name}")
            },
            confirmButton = {
                Button(onClick = { showEditDialog = false }) {
                    Text("Confirm")
                }
            }
        )
    }
}

@Composable
fun PlantItem(
    plant: Plant,
    onEdit: (Plant) -> Unit,
    onDelete: (Plant) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Options") },
            text = {
                Column {
                    Button(onClick = {
                        onEdit(plant)
                        showDialog = false
                    }) {
                        Text("Edit")
                    }
                    Button(onClick = {
                        onDelete(plant)
                        showDialog = false
                    }) {
                        Text("Delete")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { showDialog = true }
                )
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = plant.name)
            Text(text = "Last Watered: ${dateFormat.format(plant.lastWateredDate)}")
            Text(text = "Watering Frequency: ${plant.wateringFrequency} days")
            Text(text = "Last Fertilized: ${dateFormat.format(plant.lastFertilizedDate)}")
            Text(text = "Fertilizing Frequency: ${plant.fertilizingFrequency} days")
            Text(text = "Additional Notes: ${plant.additionalNotes}")
        }
    }
}
