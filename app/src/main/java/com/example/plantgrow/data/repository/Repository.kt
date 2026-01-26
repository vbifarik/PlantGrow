package com.example.plantgrow.data.repository

import com.example.plantgrow.data.bed.Bed
import com.example.plantgrow.data.bed.BedDao
import com.example.plantgrow.data.bedplant.BedPlant
import com.example.plantgrow.data.bedplant.BedPlantDao
import com.example.plantgrow.data.bedplant.BedPlantWithPlant
import com.example.plantgrow.data.pest.Pest
import com.example.plantgrow.data.pest.PestDao
import com.example.plantgrow.data.plant.GenusWithCount
import com.example.plantgrow.data.plant.Plant
import com.example.plantgrow.data.plant.PlantCategory
import com.example.plantgrow.data.plant.PlantDao
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class GardenRepository @Inject constructor(
    private val bedDao: BedDao,
    private val plantDao: PlantDao,
    private val pestDao: PestDao,
    private val bedPlantDao: BedPlantDao
) {
    fun getAllBeds(): Flow<List<Bed>> = bedDao.getAllBeds()
    suspend fun getBedById(bedId: Int): Bed? = bedDao.getBedById(bedId)
    suspend fun insertBed(bed: Bed): Long = bedDao.insert(bed)
    suspend fun updateBed(bed: Bed) = bedDao.update(bed)
    suspend fun deleteBed(bed: Bed) = bedDao.delete(bed.id)

    // === Методы для растений на грядках ===
    suspend fun addPlantToBed(bedPlant: BedPlant): Long = bedPlantDao.insert(bedPlant)
    suspend fun removePlantFromBed(bedPlantId: Int) = bedPlantDao.deleteById(bedPlantId)
    suspend fun updateBedPlant(bedPlant: BedPlant) = bedPlantDao.update(bedPlant)
    suspend fun getBedPlantById(bedPlantId: Int): BedPlant? = bedPlantDao.getBedPlantById(bedPlantId)
    suspend fun getBedPlant(bedId: Int, plantId: Int): BedPlant? = bedPlantDao.getBedPlant(bedId, plantId)
    fun getBedPlantsWithPlants(bedId: Int): Flow<List<BedPlantWithPlant>> = bedPlantDao.getBedPlantsWithPlants(bedId)

    suspend fun isPlantPlantedOnBed(bedId: Int, plantId: Int): Boolean {
        return bedPlantDao.getBedPlant(bedId, plantId) != null
    }

    suspend fun getPlantQuantityOnBed(bedId: Int, plantId: Int): Int {
        return bedPlantDao.getBedPlant(bedId, plantId)?.quantity ?: 0
    }

    // === Методы для растений ===
    fun getAllPlants(): Flow<List<Plant>> = plantDao.getAllPlants()
    suspend fun getPlantById(plantId: Int): Plant? = plantDao.getPlantById(plantId)
    suspend fun insertPlant(plant: Plant): Long = plantDao.insert(plant)
    suspend fun updatePlant(plant: Plant) = plantDao.update(plant)
    suspend fun deletePlant(plant: Plant) = plantDao.delete(plant)
    suspend fun searchPlants(searchQuery: String): List<Plant> = plantDao.searchPlants(searchQuery)
    suspend fun getPlantsByGenus(genus: String): List<Plant> = plantDao.getPlantsByGenus(genus)
    fun getPlantGenus(): Flow<List<PlantCategory>> {
        return plantDao.getPlantGenus().map { list ->
            list.map { result ->
                PlantCategory(
                    genus = result.genus,
                    plantCount = result.plantCount,
                    iconEmoji = PlantCategory.getEmojiForGenus(result.genus)
                )
            }
        }
    }
    // === Методы для вредителей ===
    fun getAllPests(): Flow<List<Pest>> = pestDao.getAllPests()
    suspend fun getPestById(pestId: Int): Pest? = pestDao.getPestById(pestId)
    suspend fun insertPest(pest: Pest): Long = pestDao.insert(pest)
    suspend fun updatePest(pest: Pest) = pestDao.update(pest)
    suspend fun deletePest(pest: Pest) = pestDao.delete(pest)

    suspend fun getAllGenera(): List<String> = plantDao.getAllGenera()
    suspend fun getGeneraWithCount(): List<GenusWithCount> = plantDao.getGeneraWithCount()

    fun getUnplantedPlants(bedId: Int): Flow<List<Plant>> {
        return plantDao.getUnplantedPlants(bedId)
    }
    suspend fun getPlantedCountOnGrid(bedId: Int, plantId: Int): Int {
        return bedPlantDao.getPlantedCountOnGrid(bedId, plantId)
    }
    suspend fun getAllBedPlantsForPlant(bedId: Int, plantId: Int): List<BedPlant> {
        return bedPlantDao.getAllBedPlantsForPlant(bedId, plantId)
    }
    // Удалить запись растения без позиции
    suspend fun removeUnplantedBedPlant(bedId: Int, plantId: Int) {
        bedPlantDao.deleteUnplantedBedPlant(bedId, plantId)
    }
}