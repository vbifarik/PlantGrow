package com.example.plantgrow.screen.plant

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.plantgrow.data.plant.Plant
import com.example.plantgrow.data.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@HiltViewModel
class PlantByCategoryViewModel @Inject constructor(
    private val repository: GardenRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val genus: String = savedStateHandle.get<String>("genus") ?: ""

    val plants: Flow<List<Plant>> = repository.getAllPlants()
        .map { allPlants ->
            allPlants.filter { it.mainGenus == genus }
        }

    val categoryName: String = genus
}