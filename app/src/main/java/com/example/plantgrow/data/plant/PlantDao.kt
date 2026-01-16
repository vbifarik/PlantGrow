package com.example.plantgrow.data.plant

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Insert
    suspend fun insert(plant: Plant): Long

    @Update
    suspend fun update(plant: Plant)

    @Delete
    suspend fun delete(plant: Plant)

    @Query("SELECT * FROM plants WHERE id = :plantId")
    suspend fun getPlantById(plantId: Int): Plant?

    @Query("SELECT * FROM plants ORDER BY name")
    fun getAllPlants(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE name LIKE '%' || :searchQuery || '%' OR mainGenus LIKE '%' || :searchQuery || '%'")
    suspend fun searchPlants(searchQuery: String): List<Plant>

    @Query("SELECT * FROM plants WHERE mainGenus = :genus ORDER BY name")
    suspend fun getPlantsByGenus(genus: String): List<Plant>

    @Query("SELECT DISTINCT mainGenus FROM plants WHERE mainGenus != '' ORDER BY mainGenus")
    suspend fun getAllGenera(): List<String>

    @Query("SELECT mainGenus, COUNT(*) as plantCount FROM plants WHERE mainGenus != '' GROUP BY mainGenus ORDER BY mainGenus")
    suspend fun getGeneraWithCount(): List<GenusWithCount>

    @Query("""
        SELECT p.* FROM plants p
        INNER JOIN bed_plants bp ON p.id = bp.plantId
        WHERE bp.bedId = :bedId 
        AND (bp.posX IS NULL OR bp.posX = 0)
        ORDER BY p.name ASC
    """)
    fun getUnplantedPlants(bedId: Int): Flow<List<Plant>>
}
data class GenusWithCount(
    val mainGenus: String,
    val plantCount: Int
)