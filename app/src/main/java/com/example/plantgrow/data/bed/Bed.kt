package com.example.plantgrow.data.bed

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beds")
data class Bed(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    val description: String = "",
    val createdAt: String = "",
    val tileX: Int,
    val tileY: Int
)