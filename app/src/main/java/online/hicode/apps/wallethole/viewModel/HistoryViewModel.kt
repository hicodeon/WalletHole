package online.hicode.apps.wallethole.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import online.hicode.apps.wallethole.data.dao.WalletDao
import online.hicode.apps.wallethole.data.entity.Wallet
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class HistoryViewModel (private val walletDao: WalletDao) : ViewModel() {

    private val _ym = MutableStateFlow(YearMonth.now())

    val ym : StateFlow<YearMonth> = _ym

    @OptIn(ExperimentalCoroutinesApi::class)
    val monthTotal = _ym
        .map { it.format(DateTimeFormatter.ofPattern("yyyy-MM")) }
        .flatMapLatest { ymStr -> walletDao.getMonthTotal(ymStr) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val monthHistory = _ym
        .map { it.format(DateTimeFormatter.ofPattern("yyyy-MM")) }
        .flatMapLatest { ymStr -> walletDao.listMonthHistory(ymStr) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun prevMonth() {
        _ym.value = _ym.value.minusMonths(1)
    }

    fun nextMonth() {
        _ym.value = _ym.value.plusMonths(1)
    }

    fun deleteWallet(id: Int) {
        viewModelScope.launch {
            walletDao.deleteWalletById(id)
        }
    }

    fun updateWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletDao.updateWallet(wallet)
        }
    }
}

class HistoryViewModelFactory(private val walletDao: WalletDao) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(walletDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}