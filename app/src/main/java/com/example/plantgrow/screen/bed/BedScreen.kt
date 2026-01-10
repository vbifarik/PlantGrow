package com.example.plantgrow.screen.bed

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun BedScreen(viewModel: BedViewModel = hiltViewModel(), navController: NavController) {
    Text(text = "Это главный экран с грядками")
}