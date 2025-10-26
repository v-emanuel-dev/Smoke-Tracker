package com.ivip.smoketrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.ivip.smoketrack.data.database.SmokeTrackDatabase
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

        // Criar ViewModel
        val factory = SmokeTrackViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[SmokeTrackViewModel::class.java]

        setContent {
            SmokeTrackTheme {
                SmokeTrackNavigation(viewModel = viewModel)
            }
        }
    }
}