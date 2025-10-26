package com.ivip.smoketrack.data.dao

import androidx.room.*
import com.ivip.smoketrack.data.entity.DailyGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: DailyGoal)

    @Query("SELECT * FROM daily_goals WHERE id = 1")
    fun getGoal(): Flow<DailyGoal?>

    @Update
    suspend fun update(goal: DailyGoal)
}