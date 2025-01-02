package com.example.plantreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plantreminder.ui.addplant.AddPlantScreen
import com.example.plantreminder.ui.mainscreen.MainScreen
import com.example.plantreminder.ui.plantlist.PlantListScreen
import com.example.plantreminder.ui.theme.PlantReminderTheme

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object AddPlantScreen : Screen("add_plant_screen")
    object PlantsListScreen : Screen("plants_list_screen")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantReminderTheme {
                PlantReminderApp()
            }
        }
    }
}

@Composable
fun PlantReminderApp() {
    val context = LocalContext.current // Uzyskaj kontekst
    PlantReminderTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
            composable(Screen.MainScreen.route) { MainScreen(navController) }
            composable(Screen.AddPlantScreen.route) {
                AddPlantScreen(
                    navController = navController,onPlantAdded = {
                    navController.popBackStack()
                })
            }
            composable(Screen.PlantsListScreen.route) {
                PlantListScreen(navController = navController,context = context, onAddPlantClicked = {
                    navController.navigate(Screen.AddPlantScreen.route)
                })
            }
        }
    }
}


