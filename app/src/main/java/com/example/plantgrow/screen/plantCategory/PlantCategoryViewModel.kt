package com.example.plantgrow.screen.plantCategory

import com.example.plantgrow.data.plant.PlantCategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantgrow.data.pest.Pest
import com.example.plantgrow.data.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantCategoryViewModel @Inject constructor(
    private val repository: GardenRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")

    val plantCategories: Flow<List<PlantCategory>> = repository.getPlantGenus()

    val filteredPlantCategories: Flow<List<PlantCategory>> = combine(
        plantCategories,
        _searchQuery
    ) { categories, query ->
        if (query.isBlank()) {
            categories
        } else {
            categories.filter { category ->
                category.genus.contains(query, ignoreCase = true)
            }
        }
    }.flowOn(Dispatchers.Default)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}