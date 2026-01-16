package com.example.plantgrow.screen.bedDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantgrow.data.bed.Bed
import com.example.plantgrow.data.bedplant.BedPlant
import com.example.plantgrow.data.bedplant.BedPlantWithPlant
import com.example.plantgrow.data.plant.Plant
import com.example.plantgrow.data.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BedDetailViewModel @Inject constructor(
    private val repository: GardenRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bedId: Int = savedStateHandle.get<Int>("bedId") ?: 0

    private val _bed = MutableStateFlow<Bed?>(null)
    val bed: StateFlow<Bed?> = _bed

    // Поток для непосаженных растений (без posX)
    val unplantedPlants: Flow<List<Plant>> = repository.getUnplantedPlants(bedId)

    // Выделенное растение для посадки
    private val _selectedPlant = MutableStateFlow<Plant?>(null)
    val selectedPlant: StateFlow<Plant?> = _selectedPlant

    // Выделенные клетки на сетке
    private val _selectedTiles = MutableStateFlow<Set<Pair<Int, Int>>>(emptySet())
    val selectedTiles: StateFlow<Set<Pair<Int, Int>>> = _selectedTiles

    val bedPlants: Flow<List<BedPlantWithPlant>> = repository.getBedPlantsWithPlants(bedId)

    init {
        loadBed()
    }

    private fun loadBed() {
        viewModelScope.launch {
            _bed.value = repository.getBedById(bedId)
        }
    }

    // Метод для получения посаженных растений на сетке
    fun getPlantedPlantsOnGrid(): Flow<Map<Pair<Int, Int>, Plant>> {
        return bedPlants.map { bedPlantList ->
            bedPlantList
                .filter { it.bedPlant.posX != null && it.bedPlant.posY != null }
                .associate {
                    Pair(it.bedPlant.posX!!, it.bedPlant.posY!!) to it.plant
                }
        }
    }

    // Выбрать растение для посадки
    fun selectPlant(plant: Plant?) {
        _selectedPlant.value = plant
    }

    // Выбрать клетку на сетке
    fun selectTile(x: Int, y: Int) {
        val current = _selectedTiles.value.toMutableSet()
        if (current.contains(Pair(x, y))) {
            current.remove(Pair(x, y))
        } else {
            current.add(Pair(x, y))
        }
        _selectedTiles.value = current
    }

    // Очистить все выбранные клетки
    fun clearSelectedTiles() {
        _selectedTiles.value = emptySet()
    }

    // Посадить растение на выбранные клетки
    fun plantSelectedPlant() {
        viewModelScope.launch {
            val plant = _selectedPlant.value
            val tiles = _selectedTiles.value

            if (plant != null && tiles.isNotEmpty()) {
                // Сначала удаляем старую запись без позиции (если есть)
                repository.removeUnplantedBedPlant(bedId, plant.id)

                // Создаем новую запись для каждой выбранной клетки
                tiles.forEach { (x, y) ->
                    repository.addPlantToBed(
                        BedPlant(
                            bedId = bedId,
                            plantId = plant.id,
                            quantity = 1,
                            plantingDate = getCurrentDate(),
                            posX = x,
                            posY = y
                        )
                    )
                }

                // Сбрасываем выбор
                _selectedPlant.value = null
                _selectedTiles.value = emptySet()
            }
        }
    }

    fun addPlantToBed(plantId: Int, quantity: Int = 1, notes: String = "") {
        viewModelScope.launch {
            repository.addPlantToBed(
                BedPlant(
                    bedId = bedId,
                    plantId = plantId,
                    quantity = quantity,
                    notes = notes,
                    plantingDate = getCurrentDate(),
                    posX = null, // Не посажено на сетке
                    posY = null
                )
            )
        }
    }

    fun removePlantFromBed(bedPlantId: Int) {
        viewModelScope.launch {
            repository.removePlantFromBed(bedPlantId)
        }
    }

    fun updatePlantQuantity(bedPlantId: Int, newQuantity: Int) {
        viewModelScope.launch {
            repository.getBedPlantById(bedPlantId)?.let { bedPlant ->
                repository.updateBedPlant(
                    bedPlant.copy(quantity = newQuantity)
                )
            }
        }
    }

    fun updatePlantNotes(bedPlantId: Int, notes: String) {
        viewModelScope.launch {
            repository.getBedPlantById(bedPlantId)?.let { bedPlant ->
                repository.updateBedPlant(
                    bedPlant.copy(notes = notes)
                )
            }
        }
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return formatter.format(Date())
    }

    suspend fun isPlantPlantedOnBed(plantId: Int): Boolean {
        return repository.isPlantPlantedOnBed(bedId, plantId)
    }

    suspend fun getPlantQuantityOnBed(plantId: Int): Int {
        return repository.getPlantQuantityOnBed(bedId, plantId)
    }
}