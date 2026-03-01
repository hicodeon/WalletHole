package online.hicode.apps.wallethole.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import online.hicode.apps.wallethole.data.entity.Wallet
import online.hicode.apps.wallethole.data.vo.DayStatistics
import online.hicode.apps.wallethole.data.vo.MonthTrend
import online.hicode.apps.wallethole.data.vo.StatisticsVO
import java.time.LocalDate

@Dao
interface WalletDao {

    @Insert
    suspend fun insertWallet(wallet: Wallet)

    @Update
    suspend fun updateWallet(wallet: Wallet)

    @Query("DELETE FROM wallet WHERE id = :id")
    suspend fun deleteWalletById(id: Int)

    @Query("""
        SELECT COUNT(*) as count, SUM(amount) as total
        FROM wallet
        WHERE date LIKE :date || '%' 
    """)
    fun getDayStatistics(date: String): Flow<DayStatistics>


    @Query("""
        SELECT date as date, SUM(amount) as total
        FROM wallet
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY date
        ORDER BY date
    """)
    fun listMonthTrend(startDate: String, endDate: String): Flow<List<MonthTrend>>

    @Query("""
        SELECT date as name, SUM(amount) as value
        FROM wallet
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY name
        ORDER BY name ASC
    """)
    fun listDayStatistics(startDate: String, endDate: String): Flow<List<StatisticsVO>>

    @Query("""
        SELECT strftime('%Y-%m', date) as name, SUM(amount) as value
        FROM wallet
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY name
        ORDER BY name ASC
    """)
    fun listMonthStatistics(startDate: String, endDate: String): Flow<List<StatisticsVO>>

    @Query("""
        SELECT strftime('%Y', date) as name, SUM(amount) as value
        FROM wallet
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY name
        ORDER BY name ASC
    """)
    fun listYearStatistics(startDate: String, endDate: String): Flow<List<StatisticsVO>>


    @Query("""
        SELECT SUM(amount) as total
        FROM wallet
        WHERE date LIKE :date || '%' 
    """)
    fun getMonthTotal(date: String): Flow<Double>

    @Query("""
        SELECT *
        FROM wallet
        WHERE date LIKE :date || '%' 
        ORDER BY date DESC, id DESC
    """)
    fun listMonthHistory(date: String): Flow<List<Wallet>>


}