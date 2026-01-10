package com.example.plantgrow.navigation

sealed class Screens(val route: String) {
    object Bed: Screens("bed_screen")
    object Pest: Screens("pest_screen")
    object Plant: Screens("plant_screen")
}