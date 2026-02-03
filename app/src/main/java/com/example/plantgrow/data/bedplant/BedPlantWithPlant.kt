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
)