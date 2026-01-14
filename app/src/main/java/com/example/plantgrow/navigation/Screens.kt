package com.example.plantgrow.navigation

sealed class Screens(val route: String) {
    object Bed : Screens("bed")
    object BedDetail : Screens("bed_detail/{bedId}") {
        fun createRoute(bedId: Int) = "bed_detail/$bedId"
    }
    object Pest : Screens("pest")
    object PestDetail : Screens("pest_detail/{pestId}") {
        fun createRoute(pestId: Int) = "pest_detail/$pestId"
    }
    object PlantCategory : Screens("plant_category/{bedId}") {
        fun createRoute(bedId: Int) = "plant_category/$bedId"
    }
    object PlantByCategory : Screens("plant_by_category/{bedId}/{genus}") {
        fun createRoute(bedId: Int, genus: String) = "plant_by_category/$bedId/$genus"
    }
    object PlantDetail : Screens("plant_detail/{plantId}") {
        fun createRoute(plantId: Int) = "plant_detail/$plantId"
    }
}