package com.example.plantgrow.screen.plant

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantgrow.data.bedplant.BedPlant
import com.example.plantgrow.data.pest.Pest
import com.example.plantgrow.data.plant.Plant
import com.example.plantgrow.data.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class PlantByCategoryViewModel @Inject constructor(
    private val repository: GardenRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val genus: String = savedStateHandle.get<String>("genus") ?: ""
    private val _searchQuery = MutableStateFlow("")
    val categoryName: String = genus
    val plantsList: Flow<List<Plant>> = repository.getAllPlants()
        .map { allPlants ->
            allPlants.filter { it.mainGenus == genus }
        }
    val plants: Flow<List<Plant>> = combine(
        plantsList,
        _searchQuery
    ) { plant, query ->
        if (query.isBlank()) {
            plant
        } else {
            plant.filter { plant ->
                plant.name.contains(query, ignoreCase = true)
            }
        }
    }.flowOn(Dispatchers.Default)

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            _searchQuery.emit(query)
        }
    }


    fun addPlantToBed(bedId: Int, plantId: Int) {
        viewModelScope.launch {
            repository.addPlantToBed(
                BedPlant(
                    bedId = bedId,
                    plantId = plantId,
                    quantity = 1
                )
            )
        }
    }
}