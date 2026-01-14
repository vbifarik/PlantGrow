package com.example.plantgrow.data.appmodule

import android.content.Context
import com.example.plantgrow.data.bed.BedDao
import com.example.plantgrow.data.bedplant.BedPlantDao
import com.example.plantgrow.data.database.GardenDatabase
import com.example.plantgrow.data.pest.PestDao
import com.example.plantgrow.data.plant.PlantDao
import com.example.plantgrow.data.repository.GardenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext app: Context): GardenDatabase =
        GardenDatabase.getDatabase(app)

    @Provides
    @Singleton
    fun provideBedDao(db: GardenDatabase): BedDao = db.bedDao()

    @Provides
    @Singleton
    fun providePlantDao(db: GardenDatabase): PlantDao = db.plantDao()

    @Provides
    @Singleton
    fun providePestDao(db: GardenDatabase): PestDao = db.pestDao()

    @Provides
    @Singleton
    fun provideBedPlantDao(db: GardenDatabase): BedPlantDao = db.bedPlantDao()

    @Provides
    @Singleton
    fun provideGardenRepository(
        bedDao: BedDao,
        plantDao: PlantDao,
        pestDao: PestDao,
        bedPlantDao: BedPlantDao
    ): GardenRepository = GardenRepository(bedDao, plantDao, pestDao, bedPlantDao)
}