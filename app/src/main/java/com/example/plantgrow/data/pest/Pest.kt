package com.example.plantgrow.data.pest

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pests",
    indices = [Index("name", unique = false)]
)
data class Pest(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String
)
