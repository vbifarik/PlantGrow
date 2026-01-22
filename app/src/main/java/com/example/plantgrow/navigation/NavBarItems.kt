package com.example.plantgrow.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PestControl
import androidx.compose.material.icons.outlined.Nature

object NavBarItems {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Грядки",
            icon = Icons.Filled.Home,
            route = Screens.Bed.route
        ),
        BottomNavItem(
            label = "Вредители",
            icon = Icons.Filled.PestControl,
            route = Screens.Pest.route
        ),
        BottomNavItem(
            label = "Растения",
            icon = Icons.Outlined.Nature,
            route = Screens.PlantCategory.route
        )
    )
}