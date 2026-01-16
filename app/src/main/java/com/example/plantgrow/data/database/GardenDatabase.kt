package com.example.plantgrow.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.plantgrow.data.bed.Bed
import com.example.plantgrow.data.bed.BedDao
import com.example.plantgrow.data.bedplant.BedPlant
import com.example.plantgrow.data.bedplant.BedPlantDao
import com.example.plantgrow.data.pest.Pest
import com.example.plantgrow.data.pest.PestDao
import com.example.plantgrow.data.plant.Plant
import com.example.plantgrow.data.plant.PlantDao

@Database(
    entities = [Bed::class, Plant::class, Pest::class, BedPlant::class],
    version = 7,
    exportSchema = true
)
abstract class GardenDatabase : RoomDatabase() {
    abstract fun bedDao(): BedDao
    abstract fun plantDao(): PlantDao
    abstract fun pestDao(): PestDao
    abstract fun bedPlantDao(): BedPlantDao

    companion object {
        @Volatile
        private var INSTANCE: GardenDatabase? = null

        fun getDatabase(context: Context): GardenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GardenDatabase::class.java,
                    "garden.db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}