package com.example.plantgrow.screen.pestdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantgrow.data.pest.Pest
import com.example.plantgrow.data.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PestDetailViewModel @Inject constructor(
    private val repository: GardenRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pestId: Int = savedStateHandle.get<Int>("pestId") ?: 0

    private val _pest = MutableStateFlow<Pest?>(null)
    val pest: StateFlow<Pest?> = _pest

    init {
        loadPest()
    }

    private fun loadPest() {
        viewModelScope.launch {
            val pest = repository.getPestById(pestId)
            _pest.value = pest
        }
    }
}