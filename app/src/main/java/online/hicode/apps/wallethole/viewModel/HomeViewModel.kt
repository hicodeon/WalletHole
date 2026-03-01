package online.hicode.apps.wallethole.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import online.hicode.apps.wallethole.data.dao.WalletDao
import online.hicode.apps.wallethole.data.vo.DayStatistics
import online.hicode.apps.wallethole.data.vo.MonthTrend
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

import java.time.temporal.TemporalAdjusters

class HomeViewModel(private val walletDao: WalletDao) : ViewModel() {

    private val _dayStatisticsFlow = MutableStateFlow(DayStatistics())
    private val _monthTrendFlow = MutableStateFlow<List<MonthTrend>>(emptyList())

    var dayStatistics : StateFlow<DayStatistics> = _dayStatisticsFlow
    var monthTrend : StateFlow<List<MonthTrend>> = _monthTrendFlow

    init {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) // "yyyy-MM-dd"
        val month = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        viewModelScope.launch {
            walletDao.getDayStatistics(today).collect { cs ->
                _dayStatisticsFlow.value = DayStatistics(cs.count, cs.total)
            }
        }

        viewModelScope.launch {
            walletDao.listMonthTrend(startDate, endDate).collect { mt ->
                _monthTrendFlow.value = mt
            }
        }
    }

}



class HomeViewModelFactory(private val walletDao: WalletDao) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(walletDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}