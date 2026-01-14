package com.example.plantgrow.screen.bedDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantgrow.data.bed.Bed
import com.example.plantgrow.data.bedplant.BedPlant
import com.example.plantgrow.data.bedplant.BedPlantWithPlant
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

    val bedPlants: Flow<List<BedPlantWithPlant>> = repository.getBedPlantsWithPlants(bedId)

    init {
        loadBed()
    }

    private fun loadBed() {
        viewModelScope.launch {
            _bed.value = repository.getBedById(bedId)
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
                    plantingDate = getCurrentDate()
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