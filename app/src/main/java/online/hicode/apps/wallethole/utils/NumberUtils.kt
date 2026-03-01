package online.hicode.apps.wallethole.utils

/**
 * 校验金额输入正确
 *
 */
fun isCorrectAmount(amount: String): Boolean {
    val regex = "^(0|[1-9]\\d{0,8})(\\.\\d{1,2})?$".toRegex()
    return amount.isNotBlank() && regex.matches(amount) && amount.toDouble() > 0
}