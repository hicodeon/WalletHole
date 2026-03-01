package online.hicode.apps.wallethole.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddRoad
import androidx.compose.material.icons.filled.AlignVerticalBottom
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ManageHistory
import androidx.compose.ui.graphics.vector.ImageVector

/**
 *
 */
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {

    object Home : Screen("home", "首页", Icons.Default.Home)

    object Statistics : Screen("statistics", "统计", Icons.Default.BarChart)

    object History : Screen("history", "历史", Icons.Default.History)

}