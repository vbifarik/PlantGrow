package com.example.plantgrow.screen.bed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantgrow.data.bed.Bed
import com.example.plantgrow.data.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@HiltViewModel
class BedViewModel @Inject constructor(
    private val repository: GardenRepository
) : ViewModel() {
    val beds: Flow<List<Bed>> = repository.getAllBeds()

    fun addBed(name: String) = viewModelScope.launch {
        repository.insertBed(Bed(name = name))
    }
}