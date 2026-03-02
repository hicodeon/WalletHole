package online.hicode.apps.wallethole.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 页面列表
    val screens = listOf(Screen.Home, Screen.Statistics, Screen.History)
    // 导航栈
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination : NavDestination? = backStackEntry.value?.destination

    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            icon = { androidx.compose.material3.Icon(imageVector = Screen.Home.icon, contentDescription = Screen.Home.label) },
            label = { Text(Screen.Home.label) },
            selected = currentDestination?.route == Screen.Home.route,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2E7D32),
                selectedTextColor = Color(0xFF2E7D32),
                unselectedIconColor = Color(0xFF3B3B3B),
                unselectedTextColor = Color(0xFF3B3B3B),
                indicatorColor = Color(0xFFE8F5E9)  // 移除默认指示器
            ),
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )


        // 中间加号按钮
        Box(
            modifier = Modifier
                .weight(1f)
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF2A8A2E), RoundedCornerShape(12.dp))
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        NavigationBarItem(
            icon = { Icon(imageVector = Screen.Statistics.icon, contentDescription = Screen.Statistics.label) },
            label = { Text(Screen.Statistics.label) },
            selected = currentDestination?.route == Screen.Statistics.route,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2E7D32),
                selectedTextColor = Color(0xFF2E7D32),
                unselectedIconColor = Color(0xFF3B3B3B),
                unselectedTextColor = Color(0xFF3B3B3B),
                indicatorColor = Color(0xFFE8F5E9)  // 移除默认指示器
            ),
            onClick = {
                navController.navigate(Screen.Statistics.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}