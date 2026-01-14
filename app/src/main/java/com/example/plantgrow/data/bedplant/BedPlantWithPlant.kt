package com.example.plantgrow.data.bedplant

import androidx.room.Embedded
import androidx.room.Relation
import com.example.plantgrow.data.plant.Plant

data class BedPlantWithPlant(
    @Embedded
    val bedPlant: BedPlant,

    @Relation(
        parentColumn = "plantId",
        entityColumn = "id"
    )
    val plant: Plant
) {
    // Преобразование в UI модель
    fun toUiModel(): PlantOnBedUiModel {
        return PlantOnBedUiModel(
            bedPlantId = bedPlant.id,
            plantId = plant.id,
            plantName = plant.name,
            plantDescription = plant.description,
            quantity = bedPlant.quantity,
            plantingDate = bedPlant.plantingDate,
            notes = bedPlant.notes
        )
    }
}

// UI модель для отображения
data class PlantOnBedUiModel(
    val bedPlantId: Int,
    val plantId: Int,
    val plantName: String,
    val plantDescription: String,
    val quantity: Int,
    val plantingDate: String,
    val notes: String
)