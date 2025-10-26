package com.ivip.smoketrack.data.dao

import androidx.room.*
import com.ivip.smoketrack.data.entity.Cigarette
import kotlinx.coroutines.flow.Flow

@Dao
interface CigaretteDao {

    @Insert
    suspend fun insert(cigarette: Cigarette)

    @Delete
    suspend fun delete(cigarette: Cigarette)

    @Query("SELECT * FROM cigarettes ORDER BY timestamp DESC")
    fun getAllCigarettes(): Flow<List<Cigarette>>

    @Query("SELECT * FROM cigarettes WHERE timestamp >= :startOfDay AND timestamp < :endOfDay ORDER BY timestamp DESC")
    fun getCigarettesForDay(startOfDay: Long, endOfDay: Long): Flow<List<Cigarette>>

    @Query("SELECT COUNT(*) FROM cigarettes WHERE timestamp >= :startOfDay AND timestamp < :endOfDay")
    fun getCountForDay(startOfDay: Long, endOfDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM cigarettes WHERE timestamp >= :startOfWeek AND timestamp < :endOfWeek")
    suspend fun getCountForWeek(startOfWeek: Long, endOfWeek: Long): Int

    @Query("SELECT COUNT(*) FROM cigarettes WHERE timestamp >= :startOfMonth AND timestamp < :endOfMonth")
    suspend fun getCountForMonth(startOfMonth: Long, endOfMonth: Long): Int

    @Query("DELETE FROM cigarettes WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    @Query("SELECT * FROM cigarettes WHERE id = :id")
    suspend fun getCigaretteById(id: Long): Cigarette?
}