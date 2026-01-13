package com.example.plantgrow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.plantgrow.screen.bed.BedScreen
import com.example.plantgrow.screen.pest.PestScreen
import com.example.plantgrow.screen.pestdetail.PestDetailScreen
import com.example.plantgrow.screen.plant.PlantByCategoryScreen
import com.example.plantgrow.screen.plantCategory.PlantCategoryScreen
import com.example.plantgrow.screen.plantDetails.PlantDetailScreen

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
        composable(route = Screens.PlantCategory.route) {
            PlantCategoryScreen(navController = navController)
        }
        composable(
            route = Screens.PlantByCategory.route,
            arguments = listOf(
                navArgument("genus") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val genus = backStackEntry.arguments?.getString("genus") ?: ""
            PlantByCategoryScreen(navController = navController)
        }
        composable(
            route = Screens.PlantDetail.route,
            arguments = listOf(
                navArgument("plantId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            PlantDetailScreen(navController = navController)
        }
        composable(
            route = Screens.PestDetail.route,
            arguments = listOf(
                navArgument("pestId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            PestDetailScreen(navController = navController)
        }
    }
}