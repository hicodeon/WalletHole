package online.hicode.apps.wallethole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import online.hicode.apps.wallethole.data.database.AppDatabase
import online.hicode.apps.wallethole.data.entity.Wallet
import online.hicode.apps.wallethole.ui.form.WalletAddForm
import online.hicode.apps.wallethole.ui.navigation.BottomNavigationBar
import online.hicode.apps.wallethole.ui.navigation.NavGraph
import online.hicode.apps.wallethole.ui.navigation.Screen
import online.hicode.apps.wallethole.ui.theme.WalletHoleTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WalletHoleTheme {
                val navController = rememberNavController()
                val showSheet = remember { mutableStateOf(false) }
                val sheetState = rememberModalBottomSheetState()
                val coroutineScope = rememberCoroutineScope()
                val database = AppDatabase.getDatabase(this@MainActivity)

                val backStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry.value?.destination?.route
                // 当路由为 History 时隐藏底部导航
                val showBottomBar = currentRoute != Screen.History.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(navController = navController, onAddClick = { showSheet.value = true })
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }


                if (showSheet.value) {
                    ModalBottomSheet(
                        onDismissRequest = { showSheet.value = false },
                        sheetState = sheetState
                    ) {
                        // Sheet 表单内容
                        WalletAddForm(
                            onSave = { amount, date, remark ->
                                coroutineScope.launch {
                                    val wallet = Wallet(
                                        amount = amount,
                                        date = date,
                                        remark = remark
                                    )
                                    database.walletDao().insertWallet(wallet)
                                    showSheet.value = false
                                }
                            },
                            onSaveCallback = {
                                // 根据当前页，更新首页或统计页数据
                            }
                        )
                    }
                }
            }
        }
    }

}
