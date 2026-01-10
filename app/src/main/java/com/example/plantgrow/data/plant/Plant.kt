package com.example.plantgrow.data.plant

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.plantgrow.data.bed.Bed

@Entity(
    tableName = "plants",
    foreignKeys = [
        ForeignKey(
            entity = Bed::class,
            parentColumns = ["id"],
            childColumns = ["bedId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bedId")]
)
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bedId: Int,
    val name: String,
    val description: String,
    val imageUrl: String? = null
)
