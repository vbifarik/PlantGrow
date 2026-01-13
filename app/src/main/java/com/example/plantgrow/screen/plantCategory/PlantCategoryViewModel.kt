package com.example.plantgrow.screen.plantCategory

import com.example.plantgrow.data.plant.PlantCategory

import androidx.lifecycle.ViewModel
import com.example.plantgrow.data.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PlantCategoryViewModel @Inject constructor(
    private val repository: GardenRepository
) : ViewModel() {

    // Поток категорий растений
    val plantCategories: Flow<List<PlantCategory>> =
        repository.getAllPlants()
            .map { plants ->
                // Группируем растения по mainGenus
                val groupedByGenus = plants.groupBy { it.mainGenus }

                // Преобразуем в UI модели категорий
                groupedByGenus.map { (genus, plantList) ->
                    PlantCategory(
                        genus = genus,
                        iconEmoji = PlantCategory.getEmojiForGenus(genus),
                        plantCount = plantList.size
                    )
                }.sortedBy { it.genus } // Сортируем по алфавиту
            }

    // Альтернативный вариант - получение категорий напрямую из БД
    suspend fun getGeneraWithCount(): List<PlantCategory> {
        val generaWithCount = repository.getGeneraWithCount()
        return generaWithCount.map { genusWithCount ->
            PlantCategory(
                genus = genusWithCount.mainGenus,
                iconEmoji = PlantCategory.getEmojiForGenus(genusWithCount.mainGenus),
                plantCount = genusWithCount.plantCount
            )
        }
    }
}