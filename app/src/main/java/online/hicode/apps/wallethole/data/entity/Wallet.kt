package online.hicode.apps.wallethole.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
data class Wallet(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Double,

    val date : String,

    val remark : String

)