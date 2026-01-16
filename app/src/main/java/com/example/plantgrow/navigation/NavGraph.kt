package com.example.plantgrow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.plantgrow.screen.bed.BedScreen
import com.example.plantgrow.screen.bedDetail.BedDetailScreen
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
        // Главный экран грядок
        composable(route = Screens.Bed.route) {
            BedScreen(navController = navController)
        }

        // Детальный экран грядки
        composable(
            route = Screens.BedDetail.route,
            arguments = listOf(
                navArgument("bedId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val bedId = backStackEntry.arguments?.getInt("bedId") ?: 0
            BedDetailScreen(navController = navController)
        }

        // Экран вредителей
        composable(route = Screens.Pest.route) {
            PestScreen(navController = navController)
        }

        // Детальный экран вредителя
        composable(
            route = Screens.PestDetail.route,
            arguments = listOf(
                navArgument("pestId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val pestId = backStackEntry.arguments?.getInt("pestId") ?: 0
            PestDetailScreen(navController = navController)
        }

        // Экран категорий растений (без bedId - с главного меню)
        composable(route = Screens.PlantCategory.route) {
            PlantCategoryScreen(navController = navController, bedId = null)
        }

        // Экран категорий растений (с bedId - из детального экрана грядки)
        composable(
            route = Screens.PlantCategoryWithBed.route,
            arguments = listOf(
                navArgument("bedId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val bedId = backStackEntry.arguments?.getInt("bedId") ?: 0
            PlantCategoryScreen(
                navController = navController,
                bedId = bedId
            )
        }

        // Экран растений по категории (без bedId)
        composable(
            route = Screens.PlantByCategory.route,
            arguments = listOf(
                navArgument("genus") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val genus = backStackEntry.arguments?.getString("genus") ?: ""
            PlantByCategoryScreen(
                navController = navController,
                bedId = null // Без bedId
            )
        }

        // Экран растений по категории (с bedId)
        composable(
            route = Screens.PlantByCategoryWithBed.route,
            arguments = listOf(
                navArgument("bedId") {
                    type = NavType.IntType
                },
                navArgument("genus") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val bedId = backStackEntry.arguments?.getInt("bedId") ?: 0
            val genus = backStackEntry.arguments?.getString("genus") ?: ""
            PlantByCategoryScreen(
                navController = navController,
                bedId = bedId
            )
        }

        // Детальный экран растения
        composable(
            route = Screens.PlantDetail.route,
            arguments = listOf(
                navArgument("plantId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getInt("plantId") ?: 0
            PlantDetailScreen(navController = navController)
        }
    }
}