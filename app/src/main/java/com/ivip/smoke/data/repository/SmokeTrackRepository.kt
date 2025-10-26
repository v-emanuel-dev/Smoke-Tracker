package com.ivip.smoketrack.data.repository

import com.ivip.smoketrack.data.dao.CigaretteDao
import com.ivip.smoketrack.data.dao.DailyGoalDao
import com.ivip.smoketrack.data.entity.Cigarette
import com.ivip.smoketrack.data.entity.DailyGoal
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class SmokeTrackRepository(
    private val cigaretteDao: CigaretteDao,
    private val dailyGoalDao: DailyGoalDao
) {

    // Cigarette operations
    suspend fun addCigarette(note: String? = null) {
        cigaretteDao.insert(Cigarette(note = note))
    }

    suspend fun deleteCigarette(cigarette: Cigarette) {
        cigaretteDao.delete(cigarette)
    }

    fun getAllCigarettes(): Flow<List<Cigarette>> {
        return cigaretteDao.getAllCigarettes()
    }

    fun getTodayCigarettes(): Flow<List<Cigarette>> {
        val (startOfDay, endOfDay) = getTodayTimestamps()
        return cigaretteDao.getCigarettesForDay(startOfDay, endOfDay)
    }

    fun getTodayCount(): Flow<Int> {
        val (startOfDay, endOfDay) = getTodayTimestamps()
        return cigaretteDao.getCountForDay(startOfDay, endOfDay)
    }

    suspend fun getWeekCount(): Int {
        val (startOfWeek, endOfWeek) = getWeekTimestamps()
        return cigaretteDao.getCountForWeek(startOfWeek, endOfWeek)
    }

    suspend fun getMonthCount(): Int {
        val (startOfMonth, endOfMonth) = getMonthTimestamps()
        return cigaretteDao.getCountForMonth(startOfMonth, endOfMonth)
    }

    // Goal operations
    fun getGoal(): Flow<DailyGoal?> {
        return dailyGoalDao.getGoal()
    }

    suspend fun saveGoal(maxCigarettes: Int, targetReduction: Int) {
        dailyGoalDao.insert(
            DailyGoal(
                maxCigarettesPerDay = maxCigarettes,
                targetReduction = targetReduction
            )
        )
    }

    // Helper functions
    private fun getTodayTimestamps(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis

        return Pair(startOfDay, endOfDay)
    }

    private fun getWeekTimestamps(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfWeek = calendar.timeInMillis

        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        val endOfWeek = calendar.timeInMillis

        return Pair(startOfWeek, endOfWeek)
    }

    private fun getMonthTimestamps(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.timeInMillis

        return Pair(startOfMonth, endOfMonth)
    }
}