package com.example.plantreminder.ui.mainscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.plantreminder.Screen
import com.example.plantreminder.data.database.PlantDatabase
import com.example.plantreminder.data.model.Plant
import com.example.plantreminder.data.repository.PlantRepository
import com.example.plantreminder.ui.plantlist.PlantListViewModel
import com.example.plantreminder.ui.plantlist.PlantListViewModelFactory
import java.util.Calendar
import kotlin.math.ceil
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.plantreminder.R
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController,
               viewModel: PlantListViewModel = viewModel(
                   factory = PlantListViewModelFactory(
                       PlantRepository(
                           PlantDatabase.getDatabase(LocalContext.current).plantDao()
                       )
                   )
               )
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Collect the list of plants from the ViewModel
    val plants by viewModel.plants.collectAsState(initial = emptyList())

    // Get the current date
    val currentDate = Calendar.getInstance().time

    // Filter plants that need watering or fertilizing within 2 days
    val duePlants = plants.filter { plant ->
        val nextWateringDate = Calendar.getInstance().apply {
            time = plant.lastWateredDate
            add(Calendar.DAY_OF_YEAR, plant.wateringFrequency)
        }.time

        val nextFertilizingDate = Calendar.getInstance().apply {
            time = plant.lastFertilizedDate
            add(Calendar.DAY_OF_YEAR, plant.fertilizingFrequency)
        }.time

        // Calculate days until next watering or fertilizing
        val daysUntilWatering = ceil((nextWateringDate.time - currentDate.time) / (1000.0 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
        val daysUntilFertilizing = ceil((nextFertilizingDate.time - currentDate.time) / (1000.0 * 60 * 60 * 24)).toInt().coerceAtLeast(0)

        // Check if either is within 2 days
        daysUntilWatering in 0..2 || daysUntilFertilizing in 0..2
    }

    // State to control the visibility of the dialog and selected plant
    val (selectedPlant, setSelectedPlant) = remember { mutableStateOf<Plant?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {
            CenterAlignedTopAppBar(
                title = { Text("Plants Needing Attention") },
                navigationIcon = {
                    IconButton(onClick = { /* handle menu click if needed */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                if (duePlants.isEmpty()) {
                    item {
                        Text(
                            text = "Currently, none of the plants need attention",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    items(duePlants) { plant ->
                        DuePlantItem(
                            plant = plant,
                            onWatered = { viewModel.updateWateredDate(it) },
                            onFertilized = { viewModel.updateFertilizedDate(it) },
                            onLongPress = { setSelectedPlant(plant) }
                        )
                    }
                }
            }
        }

        // Show the dialog if a plant is selected
        selectedPlant?.let { plant ->
            AlertDialog(
                onDismissRequest = { setSelectedPlant(null) },
                title = { Text(text = "Details of plant") },
                text = {
                    Column {
                        Text(text = "Name: ${plant.name}")
                        Text(text = "Watering frequency : ${plant.wateringFrequency} days")
                        Text(text = "Fertilization frequency: ${plant.fertilizingFrequency} days")
                        Text(text = "Last watered: ${dateFormat.format(plant.lastWateredDate)}")
                        Text(text = "Last fertilized: ${dateFormat.format(plant.lastFertilizedDate)}")
                        // Optional: display more detailed information if desired
                    }
                },
                confirmButton = {
                    OutlinedButton(onClick = { setSelectedPlant(null) }) {
                        Text("Zamknij")
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .wrapContentHeight()
                .padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(onClick = {
                navController.navigate(Screen.AddPlantScreen.route)
            },
                modifier = Modifier.padding(12.dp)) {
                Text(text = "Add plant")
            }

            Button(onClick = {
                navController.navigate(Screen.PlantsListScreen.route)
            },
                modifier = Modifier.padding(12.dp)) {
                Text(text = "Plants list")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DuePlantItem(
    plant: Plant,
    onWatered: (Plant) -> Unit,
    onFertilized: (Plant) -> Unit,
    onLongPress: () -> Unit
    ) {
    val currentDate = Calendar.getInstance().time

    val nextWateringDate = Calendar.getInstance().apply {
        time = plant.lastWateredDate
        add(Calendar.DAY_OF_YEAR, plant.wateringFrequency)
    }.time

    val nextFertilizingDate = Calendar.getInstance().apply {
        time = plant.lastFertilizedDate
        add(Calendar.DAY_OF_YEAR, plant.fertilizingFrequency)
    }.time

    val daysUntilWatering = ceil((nextWateringDate.time - currentDate.time) / (1000.0 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    val daysUntilFertilizing = ceil((nextFertilizingDate.time - currentDate.time) / (1000.0 * 60 * 60 * 24)).toInt().coerceAtLeast(0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = { /* regular click action */ },
                onLongClick = onLongPress
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = plant.name)
            if (daysUntilWatering in 0..2) {
                Text(text = "Water in: $daysUntilWatering days")
            }
            if (daysUntilFertilizing in 0..2) {
                Text(text = "Fertilize in: $daysUntilFertilizing days")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onWatered(plant) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_watered),
                        contentDescription = "Watered"
                    )
                }

                IconButton(
                    onClick = { onFertilized(plant) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_fertilizer),
                        contentDescription = "Fertilized"
                    )
                }
            }
        }
    }
}