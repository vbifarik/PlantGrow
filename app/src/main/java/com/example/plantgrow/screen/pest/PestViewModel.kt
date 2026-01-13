package com.example.plantgrow.screen.pest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantgrow.data.pest.Pest
import com.example.plantgrow.data.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@HiltViewModel
class PestViewModel @Inject constructor(
    private val repository: GardenRepository
) : ViewModel() {
    val pests: Flow<List<Pest>> = repository.getAllPests()
    fun populatePestDatabase() {
        viewModelScope.launch {
            repository.populatePests()
        }
    }
}