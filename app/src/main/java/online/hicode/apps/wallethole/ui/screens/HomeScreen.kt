package online.hicode.apps.wallethole.ui.screens

import android.content.Context
import androidx.annotation.Dimension
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import online.hicode.apps.wallethole.data.database.AppDatabase
import online.hicode.apps.wallethole.data.vo.DayStatistics
import online.hicode.apps.wallethole.data.vo.MonthTrend
import online.hicode.apps.wallethole.viewModel.HomeViewModel
import online.hicode.apps.wallethole.viewModel.HomeViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = run {
        val context = LocalContext.current
        val dao = AppDatabase.getDatabase(context).walletDao()
        viewModel(factory = HomeViewModelFactory(dao))
    }
) {
    val dayStatistics by homeViewModel.dayStatistics.collectAsState()
    val monthTrends by homeViewModel.monthTrend.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // 添加垂直滚动
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 当日支出统计卡片
        DayStatisticsCard(dayStatistics)

        // 当月日支出趋势柱状图卡片
        MonthTrendCard(monthTrends)
    }
}

@Composable
fun DayStatisticsCard(dayStatistics: DayStatistics) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 支出笔数
            StatItem(
                label = "当日支出笔数",
                value = "${dayStatistics.count}",
                valueColor = Color(0xFF2E7D32)
            )

            // 分隔线
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(60.dp)
                    .background(Color.Gray.copy(alpha = 0.3f))
            )

            // 支出金额
            StatItem(
                label = "当日支出金额",
                value = "￥${dayStatistics.total}",
                valueColor = Color(0xFF2E7D32)
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String, valueColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
fun MonthTrendCard(monthTrends: List<MonthTrend>) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start=16.dp, end = 16.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "当月日支出情况",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            // 简单柱状图（可用 MPAndroidChart 库增强）
            DayTrendGrid(
                monthTrends = monthTrends,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun DayTrendGrid(monthTrends: List<MonthTrend>, modifier: Modifier = Modifier) {
    // 将数据转换为 Map 便于查找
    val trendMap = remember(monthTrends) {
        monthTrends.associateBy { it.date }
    }

    // 获取当前月份的信息
    val currentMonth = remember {
        java.time.YearMonth.now()
    }

    val daysInMonth = remember(currentMonth) {
        currentMonth.lengthOfMonth()
    }

    val firstDayOfWeek = remember(currentMonth) {
        currentMonth.atDay(1).dayOfWeek.value % 7 // 0=Sunday, 1=Monday, ..., 6=Saturday
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // 星期标题行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
                Text(
                    text = day,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 日历网格
        val weeks = (daysInMonth + firstDayOfWeek + 6) / 7 // 计算需要的周数

        repeat(weeks) { weekIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(7) { dayIndex ->
                    val dayOfMonth = weekIndex * 7 + dayIndex - firstDayOfWeek + 1

                    if (dayOfMonth > 0 && dayOfMonth <= daysInMonth) {
                        val dateStr = "${currentMonth.year}-${currentMonth.monthValue.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
                        val trend = trendMap[dateStr]

                        CalendarDayCell(
                            day = dayOfMonth,
                            amount = trend?.total ?: 0.0,
                            hasData = trend != null,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // 空白单元格
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            if (weekIndex < weeks - 1) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    day: Int,
    amount: Double,
    hasData: Boolean,
    modifier: Modifier = Modifier
) {
    // 添加显示模式状态：true显示金额，false显示日期
    val (showAmount, setShowAmount) = remember { mutableStateOf(false) }

    val backgroundColor = if (hasData) {
        if (amount > 500) Color(0xFFE57373) // 高支出 - 红色
        else if (amount > 100) Color(0xFFFFB74D) // 中等支出 - 橙色
        else Color(0xFF81C784) // 低支出 - 绿色
    } else {
        Color(0xFFF5F5F5) // 无数据 - 灰色
    }

    val textColor = if (hasData) Color.White else Color.Gray

    Card(
        modifier = modifier
            .fillMaxHeight()
            .padding(2.dp)
            .clickable { setShowAmount(!showAmount) },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showAmount) {
                if (hasData) {
                    Text(
                        text = "¥${"%.2f".format(amount)}",
                        fontSize = 8.sp,
                        color = textColor,
                        maxLines = 1
                    )
                } else {
                    Text(
                        text = "¥0.00",
                        fontSize = 8.sp,
                        color = textColor,
                        maxLines = 1
                    )
                }

            } else {
                Text(
                    text = "$day",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}