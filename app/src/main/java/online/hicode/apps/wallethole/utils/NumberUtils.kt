package online.hicode.apps.wallethole.utils

/**
 * 校验金额输入正确
 *
 */
fun isCorrectAmount(amount: String): Boolean {
    val regex = "^(0|[1-9]\\d{0,8})(\\.\\d{1,2})?$".toRegex()
    return amount.isNotBlank() && regex.matches(amount) && amount.toDouble() > 0
}


/**
 * 格式化金额：保留两位小数，四舍五入，去掉末尾零
 * 示例：
 * - 0.00 → "0"
 * - 0.10 → "0.1"
 * - 0.01 → "0.01"
 * - 0.0112 → "0.01"
 * - 1.00 → "1"
 * - 1.10 → "1.1"
 * - 1.11 → "1.11"
 */
fun formatAmount(amount: Double): String {
    // 四舍五入到两位小数
    val formatted = String.format("%.2f", amount)

    // 去掉末尾零
    return when {
        formatted.endsWith(".00") -> formatted.substring(0, formatted.length - 3)  // 去掉 .00
        formatted.endsWith("0") -> formatted.substring(0, formatted.length - 1)    // 去掉末尾 0
        else -> formatted  // 保持原样
    }
}