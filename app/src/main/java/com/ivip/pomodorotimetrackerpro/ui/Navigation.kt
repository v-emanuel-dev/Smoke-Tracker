package com.ivip.pomodorotimetrackerpro.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ivip.pomodorotimetrackerpro.ui.screens.SettingsScreen
import com.ivip.pomodorotimetrackerpro.ui.screens.StatisticsScreen
import com.ivip.pomodorotimetrackerpro.ui.screens.TimerScreen

sealed class Screen(val route: String) {
    object Timer : Screen("timer")
    object Settings : Screen("settings")
    object Statistics : Screen("statistics")
}

@Composable
fun TimeTrackerNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Timer.route
    ) {
        composable(Screen.Timer.route) {
            TimerScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistics.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
