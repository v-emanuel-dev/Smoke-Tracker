package com.ivip.smoketrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ivip.smoketrack.data.preferences.ThemePreferences
import com.ivip.smoketrack.data.repository.SmokeTrackRepository

class SmokeTrackViewModelFactory(
    private val repository: SmokeTrackRepository,
    private val themePreferences: ThemePreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SmokeTrackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SmokeTrackViewModel(repository, themePreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}