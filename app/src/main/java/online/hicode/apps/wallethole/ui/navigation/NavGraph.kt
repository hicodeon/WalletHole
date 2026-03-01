package online.hicode.apps.wallethole.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import online.hicode.apps.wallethole.ui.screens.HistoryScreen
import online.hicode.apps.wallethole.ui.screens.HomeScreen
import online.hicode.apps.wallethole.ui.screens.StatisticsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Statistics.route) { StatisticsScreen(navController) }
        composable(Screen.History.route) { HistoryScreen(navController) }
    }
}