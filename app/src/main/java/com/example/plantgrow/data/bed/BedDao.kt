package com.example.plantgrow.data.bed

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BedDao {
    @Query("SELECT * FROM beds ORDER BY name ASC")
    fun getAllBeds(): Flow<List<Bed>>

    @Query("SELECT * FROM beds WHERE id = :bedId")
    suspend fun getBedById(bedId: Int): Bed?

    @Insert
    suspend fun insert(bed: Bed): Long

    @Update
    suspend fun update(bed: Bed)

    @Delete
    suspend fun delete(bed: Bed)

    @Query("DELETE FROM beds WHERE id = :bedId")
    suspend fun deleteById(bedId: Int)
}