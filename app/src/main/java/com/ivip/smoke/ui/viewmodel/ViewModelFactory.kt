package com.ivip.smoketrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ivip.smoketrack.data.repository.SmokeTrackRepository

class SmokeTrackViewModelFactory(
    private val repository: SmokeTrackRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SmokeTrackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SmokeTrackViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}