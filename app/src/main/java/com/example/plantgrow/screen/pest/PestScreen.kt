package com.example.plantgrow.screen.pest

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun PestScreen(viewModel: PestViewModel = hiltViewModel(),navController: NavController) {
    Text(text = "Это экран с вредителями")
}