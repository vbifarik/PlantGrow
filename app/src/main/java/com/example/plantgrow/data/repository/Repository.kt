package com.example.plantgrow.data.repository

import com.example.plantgrow.data.bed.Bed
import com.example.plantgrow.data.bed.BedDao
import com.example.plantgrow.data.pest.Pest
import com.example.plantgrow.data.pest.PestDao
import com.example.plantgrow.data.plant.Plant
import com.example.plantgrow.data.plant.PlantDao
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class GardenRepository @Inject constructor(
    private val bedDao: BedDao,
    private val plantDao: PlantDao,
    private val pestDao: PestDao
) {
    fun getAllBeds(): Flow<List<Bed>> = bedDao.getAllBeds()
    suspend fun getBedById(bedId: Int): Bed? = bedDao.getBedById(bedId)
    suspend fun insertBed(bed: Bed): Long = bedDao.insert(bed)
    suspend fun updateBed(bed: Bed) = bedDao.update(bed)
    suspend fun deleteBed(bed: Bed) = bedDao.delete(bed)

    fun getPlantsForBed(bedId: Int): Flow<List<Plant>> = plantDao.getPlantsForBed(bedId)
    suspend fun getPlantById(plantId: Int): Plant? = plantDao.getPlantById(plantId)
    suspend fun insertPlant(plant: Plant): Long = plantDao.insert(plant)
    suspend fun updatePlant(plant: Plant) = plantDao.update(plant)
    suspend fun deletePlant(plant: Plant) = plantDao.delete(plant)
    suspend fun deletePlantsByBedId(bedId: Int) = plantDao.deletePlantsByBedId(bedId)

    fun getAllPests(): Flow<List<Pest>> = pestDao.getAllPests()
    suspend fun getPestById(pestId: Int): Pest? = pestDao.getPestById(pestId)
    suspend fun insertPest(pest: Pest): Long = pestDao.insert(pest)
    suspend fun updatePest(pest: Pest) = pestDao.update(pest)
    suspend fun deletePest(pest: Pest) = pestDao.delete(pest)
}