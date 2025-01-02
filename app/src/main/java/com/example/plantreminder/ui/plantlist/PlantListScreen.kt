package com.example.plantreminder.ui.plantlist

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
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
import androidx.navigation.NavController
import com.example.plantreminder.data.model.Plant
import com.example.plantreminder.data.repository.PlantRepository
import com.example.plantreminder.data.database.PlantDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun PlantListScreen(
    navController: NavController,
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
    var showDeleteAllConfirmation by remember { mutableStateOf(false) }

    // Handle plant edit
    val handleEdit = { plant: Plant ->
        plantToEdit = plant
        showEditDialog = true
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(plants.value) { plant ->
                PlantItem(
                    plant = plant,
                    onEdit = handleEdit,
                    onDelete = { viewModel.deletePlant(it) }
                )
            }
        }
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = onAddPlantClicked,
                modifier = Modifier.padding(12.dp)
            ) {
                Text(text = "Add Plant")
            }
            Button(
                onClick = { showDeleteAllConfirmation = true },
                modifier = Modifier.padding(12.dp)
            ) {
                Text(text = "Delete all")
            }
            Button(
                onClick = {  navController.popBackStack() },
                modifier = Modifier.padding(12.dp)
            ) {
                Text(text = "Return")
            }
        }
    }

    if (showDeleteAllConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteAllConfirmation = false },
            title = { Text(text = "Confirm Deletion") },
            text = { Text("Are you sure you want to delete all plants?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllPlants()
                        showDeleteAllConfirmation = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteAllConfirmation = false }) {
                    Text("No")
                }
            }
        )
    }

    if (showEditDialog && plantToEdit != null) {
        EditPlantDialog(
            plant = plantToEdit!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedPlant ->
                viewModel.updatePlant(updatedPlant)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun EditPlantDialog(
    plant: Plant,
    onDismiss: () -> Unit,
    onSave: (Plant) -> Unit
) {
    var name by remember { mutableStateOf(plant.name) }
    var lastWateredDate by remember { mutableStateOf(plant.lastWateredDate) }
    var wateringFrequency by remember { mutableStateOf(plant.wateringFrequency) }
    var lastFertilizedDate by remember { mutableStateOf(plant.lastFertilizedDate) }
    var fertilizingFrequency by remember { mutableStateOf(plant.fertilizingFrequency) }
    var additionalNotes by remember { mutableStateOf(plant.additionalNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Plant") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Watering frequency: ${wateringFrequency.toInt()} days")
                Slider(
                    value = wateringFrequency.toFloat(),
                    onValueChange = { wateringFrequency = it.toInt() },
                    valueRange = 0f..30f,
                    steps = 29,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Fertilizing frequency: ${fertilizingFrequency.toInt()} days")
                Slider(
                    value = fertilizingFrequency.toFloat(),
                    onValueChange = { fertilizingFrequency = it.toInt() },
                    valueRange = 0f..90f,
                    steps = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = additionalNotes,
                    onValueChange = { additionalNotes = it },
                    label = { Text("Additional Notes") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedPlant = plant.copy(
                        name = name,
                        lastWateredDate = lastWateredDate,
                        wateringFrequency = wateringFrequency,
                        lastFertilizedDate = lastFertilizedDate,
                        fertilizingFrequency = fertilizingFrequency,
                        additionalNotes = additionalNotes
                    )
                    onSave(updatedPlant)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PlantItem(
    plant: Plant,
    onEdit: (Plant) -> Unit,
    onDelete: (Plant) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val nextWateringDate = Calendar.getInstance().apply {
        time = plant.lastWateredDate
        add(Calendar.DAY_OF_YEAR, plant.wateringFrequency)
    }.time

    val nextFertilizingDate = Calendar.getInstance().apply {
        time = plant.lastFertilizedDate
        add(Calendar.DAY_OF_YEAR, plant.fertilizingFrequency)
    }.time


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(
                text = "Options",
                modifier = Modifier.fillMaxWidth(), // Wypełnia szerokość, co pozwala na wyśrodkowanie
                textAlign = androidx.compose.ui.text.style.TextAlign.Center // Wyśrodkowuje tekst
            ) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally // Wyśrodkowanie wszystkich elementów w kolumnie
                ) {
                    Button(
                        onClick = {
                            onEdit(plant)
                            showDialog = false
                        },
                        modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally) // Wyśrodkowanie przycisku
                    ) {
                        Text("Edit")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onDelete(plant)
                            showDialog = false
                        },
                        modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally) // Wyśrodkowanie przycisku
                    ) {
                        Text("Delete")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showDialog = false },
                        modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally) // Wyśrodkowanie przycisku
                    ) {
                        Text("Cancel")
                    }
                }
            },
            confirmButton = {}
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
            Text(text = "Next Watering: ${dateFormat.format(nextWateringDate)}")
            Text(text = "Last Fertilized: ${dateFormat.format(plant.lastFertilizedDate)}")
            Text(text = "Fertilizing Frequency: ${plant.fertilizingFrequency} days")
            Text(text = "Next Fertilizing: ${dateFormat.format(nextFertilizingDate)}")
            Text(text = "Additional Notes: ${plant.additionalNotes}")
        }
    }
}
