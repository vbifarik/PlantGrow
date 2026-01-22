package com.example.plantgrow.screen.pest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantgrow.data.pest.Pest
import com.example.plantgrow.data.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@HiltViewModel
class PestViewModel @Inject constructor(
    private val repository: GardenRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")

    // Flow с фильтрацией
    val pests: Flow<List<Pest>> = combine(
        repository.getAllPests(),
        _searchQuery
    ) { pests, query ->
        if (query.isBlank()) {
            pests
        } else {
            pests.filter { pest ->
                pest.name.contains(query, ignoreCase = true)
            }
        }
    }.flowOn(Dispatchers.Default)

    // Метод для обновления поиска
    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            _searchQuery.emit(query)
        }
    }
    fun populatePestDatabase() {
        viewModelScope.launch {
            repository.populatePests()
        }
    }
}