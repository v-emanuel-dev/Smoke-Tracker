package com.ivip.smoketrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ivip.smoketrack.ui.screens.HomeScreen
import com.ivip.smoketrack.ui.screens.SettingsScreen
import com.ivip.smoketrack.ui.screens.StatisticsScreen
import com.ivip.smoketrack.ui.viewmodel.SmokeTrackViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
}

@Composable
fun SmokeTrackNavigation(
    viewModel: SmokeTrackViewModel,
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                uiState = uiState,
                onAddCigarette = { note -> viewModel.addCigarette(note) },
                onDeleteCigarette = { cigarette -> viewModel.deleteCigarette(cigarette) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() },
                onSaveGoal = { max, reduction -> viewModel.saveGoal(max, reduction) }
            )
        }
    }
}