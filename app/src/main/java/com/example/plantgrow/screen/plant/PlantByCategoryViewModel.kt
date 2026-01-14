package com.example.plantgrow.screen.plant

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantgrow.data.bedplant.BedPlant
import com.example.plantgrow.data.plant.Plant
import com.example.plantgrow.data.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
    fun addPlantToBed(bedId: Int, plantId: Int) {
        viewModelScope.launch {
            // Сначала проверим, есть ли уже такое растение на грядке
            val existingBedPlant = repository.getBedPlant(bedId, plantId)

            if (existingBedPlant == null) {
                // Если нет, создаем новую запись
                repository.addPlantToBed(
                    BedPlant(
                        bedId = bedId,
                        plantId = plantId,
                        quantity = 1
                    )
                )
            } else {
                // Если есть, увеличиваем количество
                repository.updateBedPlant(
                    existingBedPlant.copy(
                        quantity = existingBedPlant.quantity + 1
                    )
                )
            }
        }
    }
}