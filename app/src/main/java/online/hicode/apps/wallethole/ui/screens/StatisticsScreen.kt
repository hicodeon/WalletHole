package online.hicode.apps.wallethole.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import online.hicode.apps.wallethole.data.database.AppDatabase
import online.hicode.apps.wallethole.data.vo.StatisticsVO
import online.hicode.apps.wallethole.viewModel.StatisticsViewModel
import online.hicode.apps.wallethole.viewModel.StatisticsViewModelFactory
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    statisticsViewModel: StatisticsViewModel = run {
        val context = LocalContext.current
        val dao = AppDatabase.getDatabase(context).walletDao()
        viewModel(factory = StatisticsViewModelFactory(dao))
    }
) {

    val dayStatistics by statisticsViewModel.dayStatistics.collectAsState()
    val monthStatistics by statisticsViewModel.monthStatistics.collectAsState()
    val yearStatistics by statisticsViewModel.yearStatistics.collectAsState()

    val scrollState = rememberScrollState()

//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(text = "统计", fontWeight = FontWeight.Bold) },
//                actions = {
//                    IconButton(onClick = {
//                        // 请根据实际 NavGraph 中的 route 名称调整 "history"
//                        navController.navigate("history")
//                    }) {
//                        Icon(
//                            imageVector = Icons.Default.History,
//                            contentDescription = "历史",
//                            modifier = Modifier.size(24.dp)
//                        )
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//
//    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "统计", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // 当前月总金额
                IconButton(onClick = {
                    // 请根据实际 NavGraph 中的 route 名称调整 "history"
                    navController.navigate("history")
                }) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "历史",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Surface {
                LazyColumn {
                    // 当月日支出趋势柱状图卡片
                    item {
                        DayStatisticsCard(statisticsViewModel, dayStatistics)
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item {
                        MonthStatisticsCard(statisticsViewModel, monthStatistics)
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item {
                        YearStatisticsCard(statisticsViewModel, yearStatistics)
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

            }

        }
    }
}

@Composable
fun DayStatisticsCard(statisticsViewModel: StatisticsViewModel, dayStatistics: List<StatisticsVO>) {
    val dayStatisticsMonth by statisticsViewModel.dayStatisticsMonth.collectAsState()
    val ymFormatter = DateTimeFormatter.ofPattern("yyyy年MM月")
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    statisticsViewModel.prevDayStatisticsMonth()
                }) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "上一月")
                }

                // 当前月总金额
                Text(
                    text = "${dayStatisticsMonth.format(ymFormatter)} - 日支出趋势",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )

                IconButton(onClick = { statisticsViewModel.nextDayStatisticsMonth() }) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "下一月")
                }
            }

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            // 简单柱状图（可用 MPAndroidChart 库增强）
            DayStatisticsGrid(
                dayStatisticsMonth,
                dayStatistics = dayStatistics,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun DayStatisticsGrid(dayStatisticsMonth: YearMonth, dayStatistics: List<StatisticsVO>, modifier: Modifier = Modifier) {
    // 将数据转换为 Map 便于查找
    val trendMap = remember(dayStatistics) {
        dayStatistics.associateBy { it.name }
    }

    // 获取当前月份的信息
    val daysInMonth = remember(dayStatisticsMonth) {
        dayStatisticsMonth.lengthOfMonth()
    }

    val firstDayOfWeek = remember(dayStatisticsMonth) {
        dayStatisticsMonth.atDay(1).dayOfWeek.value % 7 // 0=Sunday, 1=Monday, ..., 6=Saturday
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
                        val dateStr = "${dayStatisticsMonth.year}-${dayStatisticsMonth.monthValue.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
                        val trend = trendMap[dateStr]

                        DayCalendarCell(
                            day = dayOfMonth,
                            amount = trend?.value ?: 0.0,
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
fun DayCalendarCell(
    day: Int,
    amount: Double,
    hasData: Boolean,
    modifier: Modifier = Modifier
) {
    // 添加显示模式状态：true显示金额，false显示日期
    val (showAmount, setShowAmount) = remember { mutableStateOf(true) }

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
                        text = "$day",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
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



// ----------------------------------------------------------------


@Composable
fun MonthStatisticsCard(statisticsViewModel: StatisticsViewModel, monthStatistics: List<StatisticsVO>) {
    val monthStatisticsYear by statisticsViewModel.monthStatisticsYear.collectAsState()
    val ymFormatter = DateTimeFormatter.ofPattern("yyyy年")
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { statisticsViewModel.prevMonthStatisticsYear() }) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "上一月")
                }

                // 当前月总金额
                Text(
                    text = "${monthStatisticsYear.format(ymFormatter)} - 月支出趋势",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )

                IconButton(onClick = { statisticsViewModel.nextMonthStatisticsYear() }) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "下一月")
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            // 简单柱状图（可用 MPAndroidChart 库增强）
            MonthStatisticsGrid(
                monthStatisticsYear,
                monthStatistics = monthStatistics,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun MonthStatisticsGrid(monthStatisticsYear: Year, monthStatistics: List<StatisticsVO>, modifier: Modifier = Modifier) {
    // 将数据转换为 Map 便于查找
    val trendMap = remember(monthStatistics) {
        monthStatistics.associateBy { it.name }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // 日历网格
        repeat(3) { weekIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(4) { dayIndex ->
                    val dayOfMonth = weekIndex * 4 + dayIndex - 0 + 1

                    val dateStr = "${monthStatisticsYear.format(DateTimeFormatter.ofPattern("yyyy"))}-${dayOfMonth.toString().padStart(2, '0')}"
                    val trend = trendMap[dateStr]

                    MonthCalendarCell(
                        day = dayOfMonth,
                        amount = trend?.value ?: 0.0,
                        hasData = trend != null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (weekIndex < 2) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun MonthCalendarCell(
    day: Int,
    amount: Double,
    hasData: Boolean,
    modifier: Modifier = Modifier
) {
    // 添加显示模式状态：true显示金额，false显示日期
    val (showAmount, setShowAmount) = remember { mutableStateOf(true) }

    val backgroundColor = if (hasData) {
        if (amount > 5000) Color(0xFFE57373) // 高支出 - 红色
        else if (amount > 2000) Color(0xFFFFB74D) // 中等支出 - 橙色
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
                        text = "$day",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
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

// ----------------------------------------------------------------

@Composable
fun YearStatisticsCard(statisticsViewModel: StatisticsViewModel, yearStatistics: List<StatisticsVO>) {
    val yearStatisticsRange by statisticsViewModel.yearStatisticsRange.collectAsState()
    val ymFormatter = DateTimeFormatter.ofPattern("yyyy")
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { statisticsViewModel.prevYearStatisticsRange() }) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "上一月")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 当前月总金额
                    Text(
                        text = "${yearStatisticsRange[0].format(ymFormatter)}-${
                            yearStatisticsRange[1].format(
                                ymFormatter
                            )
                        }年支出趋势",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                IconButton(onClick = { statisticsViewModel.nextYearStatisticsRange() }) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "下一月")
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            // 简单柱状图（可用 MPAndroidChart 库增强）
            YearStatisticsGrid(
                yearStatisticsRange,
                yearStatistics = yearStatistics,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun YearStatisticsGrid(yearStatisticsRange: List<Year>, yearStatistics: List<StatisticsVO>, modifier: Modifier = Modifier) {
    // 将数据转换为 Map 便于查找
    val trendMap = remember(yearStatistics) {
        yearStatistics.associateBy { it.name }
    }
    val startYear = yearStatisticsRange[0]
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        repeat(2) { weekIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(3) { dayIndex ->
                    val offset: Int = weekIndex * 3 + dayIndex
                    val year = startYear.plusYears(offset.toLong()).format(DateTimeFormatter.ofPattern("yyyy"))

                    val trend = trendMap[year]

                    YearCalendarCell(
                        day = year.toInt(),
                        amount = trend?.value ?: 0.0,
                        hasData = trend != null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (weekIndex < 2 - 1) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun YearCalendarCell(
    day: Int,
    amount: Double,
    hasData: Boolean,
    modifier: Modifier = Modifier
) {
    // 添加显示模式状态：true显示金额，false显示日期
    val (showAmount, setShowAmount) = remember { mutableStateOf(true) }

    val backgroundColor = if (hasData) {
        if (amount > 30000) Color(0xFFE57373) // 高支出 - 红色
        else if (amount > 15000) Color(0xFFFFB74D) // 中等支出 - 橙色
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
                        text = "$day",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
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
