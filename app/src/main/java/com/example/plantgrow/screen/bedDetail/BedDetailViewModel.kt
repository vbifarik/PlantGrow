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

    // Поток для непосаженных растений (без posX)
    val unplantedPlants: Flow<List<Plant>> = repository.getUnplantedPlants(bedId)

    // Выделенное растение для посадки
    private val _selectedPlant = MutableStateFlow<Plant?>(null)
    val selectedPlant: StateFlow<Plant?> = _selectedPlant

    // Выделенные клетки на сетке
    private val _selectedTiles = MutableStateFlow<Set<Pair<Int, Int>>>(emptySet())
    val selectedTiles: StateFlow<Set<Pair<Int, Int>>> = _selectedTiles

    // Количество доступных растений для текущего выбранного растения
    private val _availableQuantity = MutableStateFlow(0)
    val availableQuantity: StateFlow<Int> = _availableQuantity

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
        _selectedTiles.value = emptySet()

        if (plant != null) {
            viewModelScope.launch {
                calculateAvailableQuantity(plant.id)
            }
        } else {
            _availableQuantity.value = 0
        }
    }

    // Выбрать клетку на сетке с проверкой количества
    fun selectTile(x: Int, y: Int) {
        val currentTiles = _selectedTiles.value
        val selectedPlantValue = _selectedPlant.value

        if (selectedPlantValue == null) {
            // Нельзя выбрать клетку без выбранного растения
            return
        }

        val available = _availableQuantity.value
        if (available <= 0) {
            // Нет доступных растений для посадки
            return
        }

        if (currentTiles.contains(Pair(x, y))) {
            // Убираем клетку из выбранных
            val newTiles = currentTiles.toMutableSet()
            newTiles.remove(Pair(x, y))
            _selectedTiles.value = newTiles
        } else {
            // Добавляем клетку, если не превышен лимит
            if (currentTiles.size < available) {
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
            val plant = _selectedPlant.value
            val tiles = _selectedTiles.value

            if (plant != null && tiles.isNotEmpty()) {
                // Проверяем, что выбранное количество не превышает доступное
                val available = _availableQuantity.value
                if (tiles.size > available) {
                    // Нельзя посадить больше чем есть доступно
                    return@launch
                }

                // Получаем ВСЕ записи этого растения на грядке
                val allBedPlants = repository.getAllBedPlantsForPlant(bedId, plant.id)

                // Находим запись(и) без позиции
                val unplantedRecords = allBedPlants.filter { it.posX == null }

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

                // Сбрасываем выбор и пересчитываем доступное количество
                _selectedPlant.value = null
                _selectedTiles.value = emptySet()
                _availableQuantity.value = 0
            }
        }
    }

    // Рассчитать доступное количество для растения
    private suspend fun calculateAvailableQuantity(plantId: Int) {
        // Получаем ВСЕ записи этого растения на грядке (и с позицией и без)
        val allBedPlants = repository.getAllBedPlantsForPlant(bedId, plantId)

        // Общее количество всех растений этого типа на грядке
        val totalQuantity = allBedPlants.sumOf { it.quantity }

        // Количество уже посаженных на сетке (с позицией)
        val plantedCount = allBedPlants
            .filter { it.posX != null }
            .sumOf { it.quantity }

        // Доступное количество = общее - уже посаженные
        val available = totalQuantity - plantedCount

        _availableQuantity.value = maxOf(0, available) // Не может быть отрицательным
    }

    // Добавить новое растение на грядку (без позиции)
    fun addPlantToBed(plantId: Int, quantity: Int = 1, notes: String = "") {
        viewModelScope.launch {
            // Проверяем, есть ли уже такое растение без позиции
            val existingPlant = repository.getBedPlant(bedId, plantId)

            if (existingPlant != null && existingPlant.posX == null) {
                // Увеличиваем количество у существующей записи
                repository.updateBedPlant(
                    existingPlant.copy(
                        quantity = existingPlant.quantity + quantity,
                        notes = if (notes.isNotEmpty()) notes else existingPlant.notes
                    )
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

            // Если это выбранное растение, пересчитываем доступное количество
            if (_selectedPlant.value?.id == plantId) {
                calculateAvailableQuantity(plantId)
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

    // Получить количество доступных для посадки растений
    suspend fun getAvailableQuantityForPlant(plantId: Int): Int {
        val totalQuantity = repository.getPlantQuantityOnBed(bedId, plantId)
        val plantedCount = repository.getPlantedCountOnGrid(bedId, plantId)
        return totalQuantity - plantedCount
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