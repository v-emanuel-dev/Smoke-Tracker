package com.ivip.smoketrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.ivip.smoketrack.data.database.SmokeTrackDatabase
import com.ivip.smoketrack.data.preferences.ThemeMode
import com.ivip.smoketrack.data.preferences.ThemePreferences
import com.ivip.smoketrack.data.repository.SmokeTrackRepository
import com.ivip.smoketrack.navigation.SmokeTrackNavigation
import com.ivip.smoketrack.ui.theme.SmokeTrackTheme
import com.ivip.smoketrack.ui.viewmodel.SmokeTrackViewModel
import com.ivip.smoketrack.ui.viewmodel.SmokeTrackViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: SmokeTrackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar banco de dados e repositório
        val database = SmokeTrackDatabase.getDatabase(applicationContext)
        val repository = SmokeTrackRepository(
            cigaretteDao = database.cigaretteDao(),
            dailyGoalDao = database.dailyGoalDao()
        )

        // Inicializar preferências de tema
        val themePreferences = ThemePreferences(applicationContext)

        // Criar ViewModel
        val factory = SmokeTrackViewModelFactory(repository, themePreferences)
        viewModel = ViewModelProvider(this, factory)[SmokeTrackViewModel::class.java]

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val isDarkTheme = when (uiState.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            SmokeTrackTheme(darkTheme = isDarkTheme) {
                SmokeTrackNavigation(viewModel = viewModel)
            }
        }
    }
}