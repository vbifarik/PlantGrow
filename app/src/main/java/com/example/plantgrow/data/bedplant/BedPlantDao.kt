package com.example.plantgrow.data.bedplant

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.plantgrow.data.plant.GenusWithCount
import kotlinx.coroutines.flow.Flow

@Dao
interface BedPlantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bedPlant: BedPlant): Long

    @Update
    suspend fun update(bedPlant: BedPlant)

    @Query("DELETE FROM bed_plants WHERE id = :bedPlantId")
    suspend fun deleteById(bedPlantId: Int)

    @Query("DELETE FROM bed_plants WHERE bedId = :bedId")
    suspend fun deleteAllBedPlantsByBed(bedId: Int)

    @Query("SELECT * FROM bed_plants WHERE bedId = :bedId AND plantId = :plantId LIMIT 1")
    suspend fun getBedPlant(bedId: Int, plantId: Int): BedPlant?

    @Query("SELECT * FROM bed_plants WHERE id = :bedPlantId")
    suspend fun getBedPlantById(bedPlantId: Int): BedPlant?

    // Получить все растения на грядке
    @Query("SELECT * FROM bed_plants WHERE bedId = :bedId ORDER BY id DESC")
    fun getPlantsForBed(bedId: Int): Flow<List<BedPlant>>

    // Получить количество растений на грядке
    @Query("SELECT COUNT(*) FROM bed_plants WHERE bedId = :bedId")
    suspend fun getPlantCountForBed(bedId: Int): Int

    // ИЗМЕНЕНО: Правильный запрос для получения BedPlantWithPlant
    @Transaction
    @Query("""
        SELECT * FROM bed_plants
        WHERE bedId = :bedId
        ORDER BY id DESC
    """)
    fun getBedPlantsWithPlants(bedId: Int): Flow<List<BedPlantWithPlant>>
    @Query("SELECT mainGenus, COUNT(*) as plantCount FROM plants WHERE mainGenus != '' GROUP BY mainGenus ORDER BY mainGenus")
    suspend fun getGeneraWithCount(): List<GenusWithCount>

    @Query("DELETE FROM bed_plants WHERE bedId = :bedId AND plantId = :plantId AND (posX IS NULL OR posX = 0)")
    suspend fun deleteUnplantedBedPlant(bedId: Int, plantId: Int)

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM bed_plants WHERE bedId = :bedId AND plantId = :plantId AND posX IS NOT NULL")
    suspend fun getPlantedCountOnGrid(bedId: Int, plantId: Int): Int

    @Query("SELECT * FROM bed_plants WHERE bedId = :bedId AND plantId = :plantId")
    suspend fun getAllBedPlantsForPlant(bedId: Int, plantId: Int): List<BedPlant>
}