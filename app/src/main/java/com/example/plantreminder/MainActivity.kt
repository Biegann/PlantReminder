package com.example.plantreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plantreminder.ui.addplant.AddPlantScreen
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
                AddPlantScreen(onPlantAdded = {
                    navController.popBackStack()
                })
            }
            composable(Screen.PlantsListScreen.route) {
                PlantListScreen(context = context, onAddPlantClicked = {
                    navController.navigate(Screen.AddPlantScreen.route)
                })
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .wrapContentHeight()
                .padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(onClick = {
                navController.navigate(Screen.AddPlantScreen.route)
            }) {
                Text(text = "Add plant")
            }

            Button(onClick = {
                navController.navigate(Screen.PlantsListScreen.route)
            }) {
                Text(text = "Plants list")
            }
        }
    }
}
