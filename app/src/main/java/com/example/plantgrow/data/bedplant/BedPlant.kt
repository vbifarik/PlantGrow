package com.example.plantgrow.data.bedplant

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.plantgrow.data.bed.Bed
import com.example.plantgrow.data.plant.Plant

@Entity(
    tableName = "bed_plants",
    foreignKeys = [
        ForeignKey(
            entity = Bed::class,
            parentColumns = ["id"],
            childColumns = ["bedId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("bedId"),
        Index("plantId"),
        Index(value = ["bedId", "plantId"], unique = false)
    ]
)
data class BedPlant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bedId: Int,
    val plantId: Int,
    val plantingDate: String = "",
    val quantity: Int = 1,
    val notes: String = "",
    val posX: Int? = null,
    val posY: Int? = null
)
