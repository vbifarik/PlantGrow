package com.example.plantgrow.screen.plant

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun PlantScreen(viewModel: PlantViewModel = hiltViewModel(),navController: NavController) {
    Text(text = "Это экран с растениями")
}