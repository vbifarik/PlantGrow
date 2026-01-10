package com.example.plantgrow.data.pest

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PestDao {
    @Query("SELECT * FROM pests ORDER BY name ASC")
    fun getAllPests(): Flow<List<Pest>>

    @Query("SELECT * FROM pests WHERE id = :pestId")
    suspend fun getPestById(pestId: Int): Pest?

    @Insert
    suspend fun insert(pest: Pest): Long

    @Update
    suspend fun update(pest: Pest)

    @Delete
    suspend fun delete(pest: Pest)
}