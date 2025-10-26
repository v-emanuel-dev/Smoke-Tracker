package com.ivip.smoketrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivip.smoketrack.data.entity.Cigarette
import com.ivip.smoketrack.data.entity.DailyGoal
import com.ivip.smoketrack.data.preferences.ThemeMode
import com.ivip.smoketrack.data.preferences.ThemePreferences
import com.ivip.smoketrack.data.repository.SmokeTrackRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SmokeTrackUiState(
    val todayCigarettes: List<Cigarette> = emptyList(),
    val todayCount: Int = 0,
    val weekCount: Int = 0,
    val monthCount: Int = 0,
    val dailyGoal: DailyGoal? = null,
    val averagePerDay: Float = 0f,
    val isLoading: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

class SmokeTrackViewModel(
    private val repository: SmokeTrackRepository,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SmokeTrackUiState(isLoading = true))
    val uiState: StateFlow<SmokeTrackUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                themePreferences.themeMode,
                repository.getTodayCigarettes(),
                repository.getTodayCount(),
                repository.getGoal()
            ) { themeMode, todayCigarettes, todayCount, goal ->
                // Return all 4 values as a data class or list
                CombinedData(themeMode, todayCigarettes, todayCount, goal)
            }.collect { data ->
                // Call suspend functions outside combine
                val weekCount = repository.getWeekCount()
                val monthCount = repository.getMonthCount()
                val averagePerDay = if (monthCount > 0) monthCount / 30f else 0f

                _uiState.value = SmokeTrackUiState(
                    todayCigarettes = data.todayCigarettes,
                    todayCount = data.todayCount,
                    weekCount = weekCount,
                    monthCount = monthCount,
                    dailyGoal = data.goal,
                    averagePerDay = averagePerDay,
                    isLoading = false,
                    themeMode = data.themeMode
                )
            }
        }
    }

    fun addCigarette(note: String? = null) {
        viewModelScope.launch {
            repository.addCigarette(note)
        }
    }

    fun deleteCigarette(cigarette: Cigarette) {
        viewModelScope.launch {
            repository.deleteCigarette(cigarette)
        }
    }

    fun saveGoal(maxCigarettes: Int, targetReduction: Int) {
        viewModelScope.launch {
            repository.saveGoal(maxCigarettes, targetReduction)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.setThemeMode(mode)
        }
    }

    // Helper data class to hold combined flow values
    private data class CombinedData(
        val themeMode: ThemeMode,
        val todayCigarettes: List<Cigarette>,
        val todayCount: Int,
        val goal: DailyGoal?
    )
}