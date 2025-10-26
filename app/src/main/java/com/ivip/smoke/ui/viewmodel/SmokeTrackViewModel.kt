package com.ivip.smoketrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivip.smoketrack.data.entity.Cigarette
import com.ivip.smoketrack.data.entity.DailyGoal
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
    val isLoading: Boolean = false
)

class SmokeTrackViewModel(
    private val repository: SmokeTrackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SmokeTrackUiState(isLoading = true))
    val uiState: StateFlow<SmokeTrackUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                repository.getTodayCigarettes(),
                repository.getTodayCount(),
                repository.getGoal()
            ) { todayCigarettes, todayCount, goal ->
                Triple(todayCigarettes, todayCount, goal)
            }.collect { (todayCigarettes, todayCount, goal) ->
                val weekCount = repository.getWeekCount()
                val monthCount = repository.getMonthCount()
                val averagePerDay = if (monthCount > 0) monthCount / 30f else 0f

                _uiState.value = SmokeTrackUiState(
                    todayCigarettes = todayCigarettes,
                    todayCount = todayCount,
                    weekCount = weekCount,
                    monthCount = monthCount,
                    dailyGoal = goal,
                    averagePerDay = averagePerDay,
                    isLoading = false
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
}