package com.example.plantgrow.navigation

sealed class Screens(val route: String) {
    object Bed: Screens("bed_screen")
    object Pest: Screens("pest_screen")
    object PlantCategory: Screens("plantCategory_screen")
    object PlantByCategory: Screens("plant_by_category/{genus}") {
        fun createRoute(genus: String) = "plant_by_category/$genus"
    }
    object PlantDetail: Screens("plant_detail/{plantId}") {
        fun createRoute(plantId: Int) = "plant_detail/$plantId"
    }
    object PestDetail: Screens("pest_detail/{pestId}") {
        fun createRoute(pestId: Int) = "pest_detail/$pestId"
    }
}