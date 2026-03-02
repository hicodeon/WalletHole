package online.hicode.apps.wallethole.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import online.hicode.apps.wallethole.data.database.AppDatabase
import online.hicode.apps.wallethole.data.entity.Wallet
import online.hicode.apps.wallethole.ui.form.WalletUpdateForm
import online.hicode.apps.wallethole.viewModel.HistoryViewModel
import online.hicode.apps.wallethole.viewModel.HistoryViewModelFactory
import online.hicode.apps.wallethole.viewModel.HomeViewModel
import online.hicode.apps.wallethole.viewModel.HomeViewModelFactory
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue
import kotlin.text.format

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    historyViewModel: HistoryViewModel = run {
        val context = LocalContext.current
        val dao = AppDatabase.getDatabase(context).walletDao()
        viewModel(factory = HistoryViewModelFactory(dao))
    }
) {
    val ym by historyViewModel.ym.collectAsState()
    val monthTotal by historyViewModel.monthTotal.collectAsState()
    val monthHistory by historyViewModel.monthHistory.collectAsState()

    val ymFormatter = DateTimeFormatter.ofPattern("yyyy年MM月")

    // 编辑表单状态
    var showEditSheet by remember { mutableStateOf(false) }
    var currentEditingItem by remember { mutableStateOf<online.hicode.apps.wallethole.data.entity.Wallet?>(null) }
    var editAmount by remember { mutableDoubleStateOf(0.00) }
    var editWalletId by remember { mutableIntStateOf(0) }
    var editRemark by remember { mutableStateOf("") }
    var editDate by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp)
        ) {
            // 月份选择与合计
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                    IconButton(onClick = { historyViewModel.prevMonth() }) {
                        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "上一月")
                    }
                    Text(text = ym.format(ymFormatter), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { historyViewModel.nextMonth() }) {
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "下一月")
                    }
                }

                // 当前月总金额
                Text(text = "合计: ¥${"%.2f".format(monthTotal)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            }

            // 记录列表
            Surface {
                if (monthHistory.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Text(modifier = Modifier.padding(16.dp), text = "本月暂无记录", color = Color.Gray)
                    }
                } else {
                    LazyColumn {
                        items(monthHistory) { item ->
                            SwipeToDeleteEditItem(
                                item = item,
                                onDelete = { walletId ->
                                    // 删除操作
                                    scope.launch {
                                        historyViewModel.deleteWallet(walletId)
                                    }
                                },
                                onEdit = { wallet ->
                                    currentEditingItem = wallet
                                    editWalletId = wallet.id
                                    editAmount = wallet.amount
                                    editRemark = wallet.remark ?: ""
                                    editDate = wallet.date ?: ""
                                    showEditSheet = true
                                }
                            )
//                            WalletRow(item, onClick = { /* 可跳转到详情 */ })
                        }
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

            }
        }
    }

    // 编辑表单底部弹窗
    if (showEditSheet && currentEditingItem != null) {
        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            sheetState = sheetState
        ) {
            WalletUpdateForm(
                walletId = editWalletId,
                amount = editAmount,
                remark = editRemark,
                date = editDate,
                onSave = { id, amount, date, remark ->
                    scope.launch {
                        val wallet = Wallet(
                            id = id,
                            amount = amount,
                            date = date,
                            remark = remark
                        )
                        historyViewModel.updateWallet(wallet)
                        showEditSheet = false
                    }
                },
                onSaveCallback = {
                    // 根据当前页，更新首页或统计页数据
                }
            )
        }
    }
}

@Composable
fun SwipeToDeleteEditItem(
    item: Wallet,
    onDelete: (Int) -> Unit,
    onEdit: (Wallet) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val swipeThreshold = with(density) { 80.dp.toPx() }

    // 修复：使用正确的动画状态
    val backgroundOffset by animateFloatAsState(
        targetValue = offsetX,
        label = "backgroundOffset"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            offsetX.absoluteValue >= swipeThreshold -> Color.Red
            offsetX < 0 -> Color(0x00FFFFFF)
            else -> Color.Transparent
        },
        label = "backgroundColor"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // 背景按钮区域
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 编辑按钮
            if (offsetX < -40f) {  // 当滑动足够时显示编辑按钮
                IconButton(
                    onClick = { onEdit(item) },
                    modifier = Modifier
                        .background(Color(0xFF2E7D32), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑",
                        tint = Color.White
                    )
                }

                Spacer(Modifier.width(12.dp))
            }

            IconButton(
                onClick = {
                    offsetX = 0f
                    onDelete(item.id)
                },
                modifier = Modifier
                    .background(Color(0xFFB4001B), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = Color.White
                )
            }
        }

        // 前景内容
        Box(
            modifier = Modifier
                .offset(x = offsetX.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            // 拖拽开始时的处理
                        },
                        onDragEnd = {
                            // 修复：根据滑动距离决定行为
                            if (offsetX.absoluteValue >= swipeThreshold) {
                                if (offsetX < 0) {
                                    // 向左滑动超过阈值，执行删除
                                    offsetX = 0f
                                    onDelete(item.id)
                                }
                            } else if (offsetX < -40f) {
                                // 中等滑动，显示编辑按钮
                                // 保持当前位置让用户点击编辑按钮
                            } else {
                                // 滑动不够，回到原位
                                offsetX = 0f
                            }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        val newValue = offsetX + dragAmount
                        // 限制滑动范围
                        offsetX = newValue.coerceIn(-120f, 0f)
                    }
                }
                .clickable {
                    // 点击时重置位置
                    if (offsetX != 0f) {
                        offsetX = 0f
                    }
                }
        ) {
            WalletRow(item) { /* 点击查看详情 */ }
        }
    }
}

@Composable
fun WalletRow(item: online.hicode.apps.wallethole.data.entity.Wallet, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.date, fontSize = 14.sp)
                Text(text = "¥${"%.2f".format(item.amount)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            }
            if (item.remark.isNotEmpty()) {
                Text(text = item.remark ?: "", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}