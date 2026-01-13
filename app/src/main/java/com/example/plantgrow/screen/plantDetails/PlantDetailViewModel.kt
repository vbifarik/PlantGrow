package com.example.plantgrow.screen.plantDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantgrow.data.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    private val repository: GardenRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val plantId: Int = savedStateHandle.get<Int>("plantId") ?: 0

    private val _plant = MutableStateFlow<com.example.plantgrow.data.plant.Plant?>(null)
    val plant: StateFlow<com.example.plantgrow.data.plant.Plant?> = _plant

    init {
        loadPlant()
    }

    private fun loadPlant() {
        viewModelScope.launch {
            val plant = repository.getPlantById(plantId)
            _plant.value = plant
        }
    }
}