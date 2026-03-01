package online.hicode.apps.wallethole.ui.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import online.hicode.apps.wallethole.ui.theme.WalletHoleTheme
import online.hicode.apps.wallethole.utils.isCorrectAmount
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.text.isNotBlank
import kotlin.text.toDouble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletAddForm(
    onSave: (account: Double, date: String, remark: String) -> Unit,
    onSaveCallback: () -> Unit
) {
    val amount = remember { mutableStateOf("0") }
    val date = remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    val remark = remember { mutableStateOf("资金用途") }
    val showDatePicker = remember { mutableStateOf(false) }
    val showErrorAlert = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp) .verticalScroll(scrollState)
    ) {

        // 金额输入框
        OutlinedTextField(
            value = amount.value,
            onValueChange = { amount.value = it },
            placeholder = { Text("0") },
            label = { Text("支出金额") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            leadingIcon = {
                Text(
                    text = "¥",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF2E7D32)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E7D32),      // 获得焦点时边框颜色
                unfocusedBorderColor = Color(0xFF2E7D32),    // 失去焦点时边框颜色
                cursorColor = Color(0xFF2E7D32),             // 光标颜色
                focusedLabelColor = Color(0xFF2E7D32),       // 获得焦点时标签颜色
                unfocusedLabelColor = Color(0xFF2E7D32)      // 失去焦点时标签颜色
            )
        )

        // 日期选择
        OutlinedTextField(
            value = date.value,
            onValueChange = {},
            label = { Text("日期") },
            modifier = Modifier
                .fillMaxWidth().padding(bottom = 8.dp)
                .clickable { showDatePicker.value = true },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "选择日期",
                    tint = Color(0xFF2E7D32)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E7D32),      // 获得焦点时边框颜色
                unfocusedBorderColor = Color(0xFF2E7D32),    // 失去焦点时边框颜色
                cursorColor = Color(0xFF2E7D32),             // 光标颜色
                focusedLabelColor = Color(0xFF2E7D32),       // 获得焦点时标签颜色
                unfocusedLabelColor = Color(0xFF2E7D32)      // 失去焦点时标签颜色
            )
        )

        if (showDatePicker.value) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker.value = false },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val selectedDate = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            date.value = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        }
                        showDatePicker.value = false
                    }) {
                        Text("确认")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDatePicker.value = false }) {
                        Text("取消")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // 说明输入框
        OutlinedTextField(
            value = remark.value,
            onValueChange = { remark.value = it },
            label = { Text("说明") },
            placeholder = { Text("资金用途") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 8.dp),
            minLines = 2,
            maxLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E7D32),      // 获得焦点时边框颜色
                unfocusedBorderColor = Color(0xFF2E7D32),    // 失去焦点时边框颜色
                cursorColor = Color(0xFF2E7D32),             // 光标颜色
                focusedLabelColor = Color(0xFF2E7D32),       // 获得焦点时标签颜色
                unfocusedLabelColor = Color(0xFF2E7D32)      // 失去焦点时标签颜色
            )
        )

        // 保存按钮
        Button(
            onClick = {
                if (isCorrectAmount(amount.value)) {
                    onSave(amount.value.toDouble(), date.value, remark.value)
                    amount.value = ""
                    date.value = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    remark.value = ""
                } else {
                    // 提示金额输入问题
                    showErrorAlert.value = true
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32),  // 深绿色
                contentColor = Color.White
            )
        ) {
            Text(
                text = "保存",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }

    if (showErrorAlert.value) {
        AlertDialog(
            onDismissRequest = { showErrorAlert.value = false },
            title = { Text("输入错误") },
            text = { Text("请输入正确的金额（正数，最多两位小数）") },
            confirmButton = {
                Button(onClick = { showErrorAlert.value = false }) {
                    Text("确定")
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletUpdateForm(
    walletId: Int,
    amount: Double,
    remark: String,
    date: String,
    onSave: (id: Int, account: Double, date: String, remark: String) -> Unit,
    onSaveCallback: () -> Unit
) {
    val amount = remember { mutableStateOf(amount.toString()) }
    val date = remember { mutableStateOf(date) }
    val remark = remember { mutableStateOf(remark) }
    val showDatePicker = remember { mutableStateOf(false) }
    val showErrorAlert = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {

        // 金额输入框
        OutlinedTextField(
            value = amount.value,
            onValueChange = { amount.value = it },
            placeholder = { Text("0") },
            label = { Text("支出金额") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            leadingIcon = {
                Text(
                    text = "¥",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF2E7D32)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E7D32),      // 获得焦点时边框颜色
                unfocusedBorderColor = Color(0xFF2E7D32),    // 失去焦点时边框颜色
                cursorColor = Color(0xFF2E7D32),             // 光标颜色
                focusedLabelColor = Color(0xFF2E7D32),       // 获得焦点时标签颜色
                unfocusedLabelColor = Color(0xFF2E7D32)      // 失去焦点时标签颜色
            )
        )

        // 日期选择
        OutlinedTextField(
            value = date.value,
            onValueChange = {},
            label = { Text("日期") },
            modifier = Modifier
                .fillMaxWidth().padding(bottom = 8.dp)
                .clickable { showDatePicker.value = true },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "选择日期",
                    tint = Color(0xFF2E7D32)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E7D32),      // 获得焦点时边框颜色
                unfocusedBorderColor = Color(0xFF2E7D32),    // 失去焦点时边框颜色
                cursorColor = Color(0xFF2E7D32),             // 光标颜色
                focusedLabelColor = Color(0xFF2E7D32),       // 获得焦点时标签颜色
                unfocusedLabelColor = Color(0xFF2E7D32)      // 失去焦点时标签颜色
            )
        )

        if (showDatePicker.value) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker.value = false },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val selectedDate = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            date.value = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        }
                        showDatePicker.value = false
                    }) {
                        Text("确认")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDatePicker.value = false }) {
                        Text("取消")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // 说明输入框
        OutlinedTextField(
            value = remark.value,
            onValueChange = { remark.value = it },
            label = { Text("说明") },
            placeholder = { Text("资金用途") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 8.dp),
            minLines = 2,
            maxLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E7D32),      // 获得焦点时边框颜色
                unfocusedBorderColor = Color(0xFF2E7D32),    // 失去焦点时边框颜色
                cursorColor = Color(0xFF2E7D32),             // 光标颜色
                focusedLabelColor = Color(0xFF2E7D32),       // 获得焦点时标签颜色
                unfocusedLabelColor = Color(0xFF2E7D32)      // 失去焦点时标签颜色
            )
        )

        // 保存按钮
        Button(
            onClick = {
                if (isCorrectAmount(amount.value)) {
                    onSave(walletId, amount.value.toDouble(), date.value, remark.value)
                    onSaveCallback()
                    amount.value = ""
                    date.value = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    remark.value = ""
                } else {
                    // 提示金额输入问题
                    showErrorAlert.value = true
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32),  // 深绿色
                contentColor = Color.White
            )
        ) {
            Text(
                text = "修改",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }

    if (showErrorAlert.value) {
        AlertDialog(
            onDismissRequest = { showErrorAlert.value = false },
            title = { Text("输入错误") },
            text = { Text("请输入正确的金额（正数，最多两位小数）") },
            confirmButton = {
                Button(onClick = { showErrorAlert.value = false }) {
                    Text("确定")
                }
            }
        )
    }
}


@Preview
@Composable
fun WalletFormAddPreview() {
    WalletHoleTheme {
        WalletAddForm(onSave = { _, _, _ -> }, onSaveCallback = {}  )
    }
}
