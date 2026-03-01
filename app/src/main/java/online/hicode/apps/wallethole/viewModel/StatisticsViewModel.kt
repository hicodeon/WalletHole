package online.hicode.apps.wallethole.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import online.hicode.apps.wallethole.data.dao.WalletDao
import online.hicode.apps.wallethole.data.vo.MonthTrend
import online.hicode.apps.wallethole.data.vo.StatisticsVO
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class StatisticsViewModel(private val walletDao: WalletDao) : ViewModel() {

    private val _dayStatisticsFlow = MutableStateFlow<List<StatisticsVO>>(emptyList())
    private val _monthStatisticsFlow = MutableStateFlow<List<StatisticsVO>>(emptyList())
    private val _yearStatisticsFlow = MutableStateFlow<List<StatisticsVO>>(emptyList())


    private val _dayStatisticsMonth = MutableStateFlow<YearMonth>(YearMonth.now())
    private val _monthStatisticsYear = MutableStateFlow<Year>(Year.now())
    private val _yearStatisticsRange = MutableStateFlow<List<Year>>(listOf(Year.now().minusYears(5), Year.now()))

    val dayStatisticsMonth : StateFlow<YearMonth> = _dayStatisticsMonth
    val monthStatisticsYear : StateFlow<Year> = _monthStatisticsYear
    val yearStatisticsRange : StateFlow<List<Year>> = _yearStatisticsRange

    val dayStatistics = _dayStatisticsMonth
        .flatMapLatest { mon ->
            val monthStartDate = mon.atDay(1).with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val monthEndDate = mon.atDay(1).with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            walletDao.listDayStatistics(monthStartDate, monthEndDate)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val monthStatistics = _monthStatisticsYear
        .flatMapLatest { year ->
            val monthStartDate = year.format(DateTimeFormatter.ofPattern("yyyy")) + "-01-01"
            val monthEndDate = year.format(DateTimeFormatter.ofPattern("yyyy")) + "-12-31"
            walletDao.listMonthStatistics(monthStartDate, monthEndDate)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val yearStatistics = _yearStatisticsRange
        .flatMapLatest { yearRange ->
            val yearStartDate = yearRange[0].format(DateTimeFormatter.ofPattern("yyyy")) + "-01-01"
            val yearEndDate = yearRange[1].format(DateTimeFormatter.ofPattern("yyyy")) + "-12-31"
            walletDao.listYearStatistics(yearStartDate, yearEndDate)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())



    fun prevDayStatisticsMonth() {
        _dayStatisticsMonth.value = _dayStatisticsMonth.value.minusMonths(1)
    }

    fun nextDayStatisticsMonth() {
        _dayStatisticsMonth.value = _dayStatisticsMonth.value.plusMonths(1)
    }

    fun prevMonthStatisticsYear() {
        _monthStatisticsYear.value = _monthStatisticsYear.value.minusYears(1)
    }

    fun nextMonthStatisticsYear() {
        _monthStatisticsYear.value = _monthStatisticsYear.value.plusYears(1)
    }

    fun prevYearStatisticsRange() {
        val sy = _yearStatisticsRange.value[0]
        _yearStatisticsRange.value = listOf(sy.minusYears(6), sy.minusYears(1))
    }

    fun nextYearStatisticsRange() {
        val ey = _yearStatisticsRange.value[1]
        _yearStatisticsRange.value = listOf(ey.plusYears(1), ey.plusYears(6))
    }


//    init {
//        // 日统计支持查看最新15天的统计数据
//        // 月统计支持最新12个月的统计数据
//        // 年统计支持最新10年的统计数据
//        val today = LocalDate.now()
//        val startDate = today.minusDays(15).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//        val endDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//
//        val monthStartDate = today.minusMonths(12).with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//        val monthEndDate = today.with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//
//        val yearStartDate = today.minusYears(10).with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//        val yearEndDate = today.withMonth(12).with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//
//
//        viewModelScope.launch {
//            walletDao.listDayStatistics(startDate, endDate).collect { mt ->
//                _dayStatisticsFlow.value = mt
//            }
//        }
//
//        viewModelScope.launch {
//            walletDao.listMonthStatistics(monthStartDate, monthEndDate).collect { mt ->
//                _monthStatisticsFlow.value = mt
//            }
//        }
//
//        viewModelScope.launch {
//            walletDao.listYearStatistics(yearStartDate, yearEndDate).collect { mt ->
//                _yearStatisticsFlow.value = mt
//            }
//        }
//
//    }
}


class StatisticsViewModelFactory(private val walletDao: WalletDao) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(walletDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}