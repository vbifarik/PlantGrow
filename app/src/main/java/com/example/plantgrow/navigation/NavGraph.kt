package com.example.plantgrow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.plantgrow.screen.bed.BedScreen
import com.example.plantgrow.screen.pest.PestScreen
import com.example.plantgrow.screen.plant.PlantScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.Bed.route
    ) {
        composable(route = Screens.Bed.route) {
            BedScreen(navController = navController)
        }
        composable(route = Screens.Pest.route) {
            PestScreen(navController = navController)
        }
        composable(route = Screens.Plant.route) {
            PlantScreen(navController = navController)
        }
    }
}