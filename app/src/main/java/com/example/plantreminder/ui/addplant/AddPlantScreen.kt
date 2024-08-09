package com.example.plantreminder.ui.addplant

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantreminder.data.database.PlantDatabase
import com.example.plantreminder.data.model.Plant
import com.example.plantreminder.data.repository.PlantRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AddPlantScreen(
    onPlantAdded: () -> Unit
) {
    val context = LocalContext.current
    val plantDatabase = PlantDatabase.getDatabase(context)
    val plantRepository = PlantRepository(plantDatabase.plantDao())
    val viewModel: AddPlantViewModel = viewModel(factory = AddPlantViewModelFactory(plantRepository))

    var plantName by remember { mutableStateOf("") }
    var lastWateredDate by remember { mutableStateOf(Date()) }
    var wateringFrequency by remember { mutableIntStateOf(0) }
    var lastFertilizedDate by remember { mutableStateOf(Date()) }
    var fertilizingFrequency by remember { mutableIntStateOf(0) }
    var additionalNotes by remember { mutableStateOf("") }

    var showWateredDatePicker by remember { mutableStateOf(false) }
    var showFertilizedDatePicker by remember { mutableStateOf(false) }

    if (showWateredDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                lastWateredDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time
            },
            lastWateredDate.year + 1900, // DatePicker expects year in the format YYYY
            lastWateredDate.month,       // DatePicker expects month in 0-based index
            lastWateredDate.date
        ).show()
        showWateredDatePicker = false
    }

    if (showFertilizedDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                lastFertilizedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time
            },
            lastFertilizedDate.year + 1900, // DatePicker expects year in the format YYYY
            lastFertilizedDate.month,       // DatePicker expects month in 0-based index
            lastFertilizedDate.date
        ).show()
        showFertilizedDatePicker = false
    }

    Column(modifier = Modifier.padding(16.dp)) {

        OutlinedTextField(
            value = plantName,
            onValueChange = { plantName = it },
            label = { Text("Plant Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Last Watered Date: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(lastWateredDate)}")
        OutlinedButton(onClick = { showWateredDatePicker = true }) {
            Text(text = "Select Last Watered Date")
        }

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

        Text(text = "Last Fertilized Date: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(lastFertilizedDate)}")
        OutlinedButton(onClick = { showFertilizedDatePicker = true }) {
            Text(text = "Select Last Fertilized Date")
        }

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

        Button(onClick = {
            if (plantName.isNotEmpty()) {
                val plant = Plant(
                    name = plantName,
                    lastWateredDate = lastWateredDate,
                    wateringFrequency = wateringFrequency,
                    lastFertilizedDate = lastFertilizedDate,
                    fertilizingFrequency = fertilizingFrequency,
                    additionalNotes = additionalNotes
                )
                viewModel.addPlant(plant)
                Toast.makeText(context, "Plant added", Toast.LENGTH_SHORT).show()
                onPlantAdded()
            } else {
                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Save plant")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPlantScreenPreview() {
    AddPlantScreen(onPlantAdded = {})
}
