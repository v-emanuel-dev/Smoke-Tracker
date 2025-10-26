package com.ivip.smoketrack.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goals")
data class DailyGoal(
    @PrimaryKey
    val id: Int = 1,
    val maxCigarettesPerDay: Int = 20,
    val targetReduction: Int = 1 // Reduzir X cigarros por semana
)