package com.example.plantgrow.data.bed

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BedDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bed: Bed): Long

    @Update
    suspend fun update(bed: Bed)

    @Query("DELETE FROM beds WHERE id = :bedId")
    suspend fun delete(bedId: Int)

    @Query("SELECT * FROM beds ORDER BY id DESC")
    fun getAllBeds(): Flow<List<Bed>>

    @Query("SELECT * FROM beds WHERE id = :bedId")
    suspend fun getBedById(bedId: Int): Bed?

    @Query("SELECT COUNT(*) FROM beds")
    suspend fun getBedCount(): Int
}