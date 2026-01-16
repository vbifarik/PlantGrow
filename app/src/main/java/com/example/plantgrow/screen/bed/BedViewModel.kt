package com.example.plantgrow.screen.bed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantgrow.data.bed.Bed
import com.example.plantgrow.data.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel
class BedViewModel @Inject constructor(
    private val repository: GardenRepository
) : ViewModel() {

    val beds: Flow<List<Bed>> = repository.getAllBeds()

    fun addBed(name: String, tilesX: Int, tilesY: Int) {
        viewModelScope.launch {
            val bed = Bed(
                name = name,
                createdAt = getCurrentDate(),
                tileX = tilesX,
                tileY = tilesY
            )
            repository.insertBed(bed)
        }
    }

    fun updateBed(bed: Bed) {
        viewModelScope.launch {
            repository.updateBed(bed)
        }
    }

    fun deleteBed(bed: Bed) {
        viewModelScope.launch {
            repository.deleteBed(bed)
        }
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return formatter.format(Date())
    }
    fun populatePlantsDatabase() {
        viewModelScope.launch {
            repository.populateInitialPlantsData()
        }
    }
}