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

    // Поток для всех растений на грядке
    val bedPlants: Flow<List<BedPlantWithPlant>> = repository.getBedPlantsWithPlants(bedId)

    // Поток для непосаженных растений (без posX) с их количеством
    val unplantedPlantsWithQuantity: Flow<List<Pair<Plant, Int>>> = bedPlants.map { bedPlantList ->
        bedPlantList
            .filter { it.bedPlant.posX == null && it.bedPlant.quantity > 0 }
            .groupBy { it.plant.id }
            .map { (_, plantsWithSameId) ->
                val plant = plantsWithSameId.first().plant
                val totalQuantity = plantsWithSameId.sumOf { it.bedPlant.quantity }
                plant to totalQuantity
            }
    }

    // Выделенное растение для посадки с количеством
    private val _selectedPlantWithQuantity = MutableStateFlow<Pair<Plant, Int>?>(null)
    val selectedPlantWithQuantity: StateFlow<Pair<Plant, Int>?> = _selectedPlantWithQuantity

    // Выделенные клетки на сетке
    private val _selectedTiles = MutableStateFlow<Set<Pair<Int, Int>>>(emptySet())
    val selectedTiles: StateFlow<Set<Pair<Int, Int>>> = _selectedTiles

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

    // Выбрать растение для посадки с его количеством
    fun selectPlant(plantWithQuantity: Pair<Plant, Int>?) {
        _selectedPlantWithQuantity.value = plantWithQuantity
        _selectedTiles.value = emptySet()
    }

    // Выбрать клетку на сетке с проверкой количества
    fun selectTile(x: Int, y: Int) {
        val currentTiles = _selectedTiles.value
        val selectedPlant = _selectedPlantWithQuantity.value

        if (selectedPlant == null) {
            // Нельзя выбрать клетку без выбранного растения
            return
        }

        val availableQuantity = selectedPlant.second

        if (currentTiles.contains(Pair(x, y))) {
            // Убираем клетку из выбранных
            val newTiles = currentTiles.toMutableSet()
            newTiles.remove(Pair(x, y))
            _selectedTiles.value = newTiles
        } else {
            // Добавляем клетку, если не превышен лимит
            if (currentTiles.size < availableQuantity) {
                val newTiles = currentTiles.toMutableSet()
                newTiles.add(Pair(x, y))
                _selectedTiles.value = newTiles
            }
            // Если превышен лимит, ничего не делаем
        }
    }

    // Очистить все выбранные клетки
    fun clearSelectedTiles() {
        _selectedTiles.value = emptySet()
    }

    // Посадить растение на выбранные клетки
    fun plantSelectedPlant() {
        viewModelScope.launch {
            val selectedPlant = _selectedPlantWithQuantity.value
            val tiles = _selectedTiles.value

            if (selectedPlant != null && tiles.isNotEmpty()) {
                val plant = selectedPlant.first
                val availableQuantity = selectedPlant.second

                // Проверяем, что выбранное количество не превышает доступное
                if (tiles.size > availableQuantity) {
                    return@launch
                }

                // Получаем ВСЕ записи этого растения без позиции
                val unplantedRecords = repository.getAllBedPlantsForPlant(bedId, plant.id)
                    .filter { it.posX == null && it.quantity > 0 }

                if (unplantedRecords.isNotEmpty()) {
                    // У нас есть растения без позиции для посадки
                    var remainingToPlant = tiles.size

                    // Используем растения из записей без позиции
                    for (record in unplantedRecords) {
                        if (remainingToPlant <= 0) break

                        val canUseFromThisRecord = minOf(record.quantity, remainingToPlant)

                        if (canUseFromThisRecord == record.quantity) {
                            // Используем всю запись, удаляем ее
                            repository.removePlantFromBed(record.id)
                        } else {
                            // Используем часть, уменьшаем количество
                            repository.updateBedPlant(
                                record.copy(quantity = record.quantity - canUseFromThisRecord)
                            )
                        }

                        remainingToPlant -= canUseFromThisRecord
                    }

                    // Создаем новые записи для каждой выбранной клетки
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
                }

                // Сбрасываем выбор
                _selectedPlantWithQuantity.value = null
                _selectedTiles.value = emptySet()
            }
        }
    }

    // Добавить новое растение на грядку (без позиции)
    fun addPlantToBed(plantId: Int, quantity: Int = 1, notes: String = "") {
        viewModelScope.launch {
            // Проверяем, есть ли уже такое растение без позиции
            val existingPlants = repository.getAllBedPlantsForPlant(bedId, plantId)
                .filter { it.posX == null }

            if (existingPlants.isNotEmpty()) {
                // Берем первую запись без позиции и увеличиваем количество
                val firstRecord = existingPlants.first()
                repository.updateBedPlant(
                    firstRecord.copy(quantity = firstRecord.quantity + quantity)
                )
            } else {
                // Создаем новую запись
                repository.addPlantToBed(
                    BedPlant(
                        bedId = bedId,
                        plantId = plantId,
                        quantity = quantity,
                        notes = notes,
                        plantingDate = getCurrentDate(),
                        posX = null,
                        posY = null
                    )
                )
            }
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

    // Вспомогательные методы для UI
    fun getSelectedPlant(): Plant? = _selectedPlantWithQuantity.value?.first
    fun getAvailableQuantity(): Int = _selectedPlantWithQuantity.value?.second ?: 0
}