package online.hicode.apps.wallethole.ui.form

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import online.hicode.apps.wallethole.ui.theme.WalletHoleTheme
import online.hicode.apps.wallethole.utils.isCorrectAmount
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.text.toDouble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletAddForm(
    onSave: (account: Double, date: String, remark: String) -> Unit,
    onSaveCallback: () -> Unit
) {
    val amount = remember { mutableStateOf("") }
    val date = remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    val remark = remember { mutableStateOf("") }
    val showDatePicker = remember { mutableStateOf(false) }
    val showErrorAlert = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    val tags = remember { mutableStateOf(listOf("超市", "外卖", "水果", "零食", "交通", "服饰", "水费", "电费", "燃气费", "话费", "其它")) }
    val selectedTagIndex = remember { mutableIntStateOf(-1) }
    val tagScrollState: ScrollState = rememberScrollState()

    val customTagInput = remember { mutableStateOf("") }
    val showCustomTagDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部区域：金额显示和日期选择
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 金额显示区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2E7D32), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "¥ ${amount.value.ifEmpty { "0.00" }}",
                        fontSize = 28.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = date.value,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                        IconButton(
                            onClick = { showDatePicker.value = true },
                            modifier = Modifier
                                .size(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "编辑日期",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            // 日期选择
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                OutlinedTextField(
//                    value = date.value,
//                    onValueChange = {},
//                    label = { Text("日期") },
//                    modifier = Modifier.weight(1f),
//                    shape = RoundedCornerShape(12.dp),
//                    singleLine = true,
//                    readOnly = true,
//                    enabled = true,
//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Default.Create,
//                            contentDescription = "选择日期",
//                            tint = Color(0xFF2E7D32)
//                        )
//                    },
//                    colors = OutlinedTextFieldDefaults.colors(
//                        focusedBorderColor = Color(0xFF2E7D32),
//                        unfocusedBorderColor = Color(0xFF2E7D32),
//                        cursorColor = Color(0xFF2E7D32),
//                        focusedLabelColor = Color(0xFF2E7D32),
//                        unfocusedLabelColor = Color(0xFF2E7D32)
//                    ),
//                )
//                Spacer(modifier = Modifier.width(12.dp))
//                Button(
//                    onClick = { showDatePicker.value = true },
//                    modifier = Modifier.width(90.dp).height(52.dp),
//                    shape = RoundedCornerShape(12.dp),
//                    contentPadding = PaddingValues(vertical = 12.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF2E7D32),
//                        contentColor = Color.White
//                    )
//                ) {
//                    Text(text = "选择")
//                }
//            }

            // 标签选择 - 可左右滚动
            Column {
//                Text(
//                    text = "支出标签",
//                    fontSize = 14.sp,
//                    color = Color.Gray,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(tags.value.size) { index ->
                            TagChip(
                                tag = tags.value[index],
                                isSelected = selectedTagIndex.intValue == index,
                                onClick = {
                                    selectedTagIndex.intValue = index
                                    remark.value = tags.value[index]
                                }
                            )
                        }
                    }
                    // 编辑按钮，固定在末尾
                    TagChip(
                        tag = "自定义",  // 或使用图标
                        isSelected = true,
                        onClick = { showCustomTagDialog.value = true },
                        isEditButton = true
                    )
                }

            }

            // 说明输入框
//            OutlinedTextField(
//                value = remark.value,
//                onValueChange = { remark.value = it },
//                label = { Text("说明") },
//                placeholder = { Text("资金用途") },
//                shape = RoundedCornerShape(12.dp),
//                modifier = Modifier.fillMaxWidth(),
//                minLines = 2,
//                maxLines = 2,
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color(0xFF2E7D32),
//                    unfocusedBorderColor = Color(0xFF2E7D32),
//                    cursorColor = Color(0xFF2E7D32),
//                    focusedLabelColor = Color(0xFF2E7D32),
//                    unfocusedLabelColor = Color(0xFF2E7D32)
//                )
//            )

            // 底部数字键盘
            NumberKeyboard(
                onNumberClick = { digit ->
                    if (digit == "." && amount.value.contains(".")) return@NumberKeyboard
                    if (digit == "." && amount.value.isEmpty()) return@NumberKeyboard
                    amount.value += digit
                },
                onDeleteClick = {
                    if (amount.value.isNotEmpty()) {
                        amount.value = amount.value.dropLast(1)
                    }
                },
                onSaveClick = {
                    if (amount.value.isNotEmpty() && isCorrectAmount(amount.value)) {
                        onSave(amount.value.toDouble(), date.value, remark.value)
                        amount.value = ""
                        date.value = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        remark.value = ""
                        selectedTagIndex.intValue = -1
                    } else {
                        showErrorAlert.value = true
                    }
                }
            )
        }
    }

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

    if (showCustomTagDialog.value) {
        AlertDialog(
            onDismissRequest = { showCustomTagDialog.value = false },
            title = { Text("添加自定义标签") },
            text = {
                TextField(
                    value = customTagInput.value,
                    onValueChange = { customTagInput.value = it },
                    label = { Text("输入标签") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (customTagInput.value.isNotBlank() && !tags.value.contains(customTagInput.value)) {
                        // 注意：tags 仍为 listOf，需要改为 mutableStateOf<List<String>>
                        // 假设您已改为 val tags = remember { mutableStateOf(listOf("超市", ...)) }
                        tags.value = listOf(customTagInput.value) + tags.value
                        selectedTagIndex.intValue = 0
                        remark.value = customTagInput.value
                        customTagInput.value = ""
                    }
                    showCustomTagDialog.value = false
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(onClick = { showCustomTagDialog.value = false }) {
                    Text("取消")
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

    val tags = remember { mutableStateOf(listOf("超市", "外卖", "水果", "零食", "交通", "服饰", "水费", "电费", "燃气费", "话费", "其它"))}
    val selectedTagIndex = remember { mutableIntStateOf(-1) }
    val tagScrollState: ScrollState = rememberScrollState()
    val customTagInput = remember { mutableStateOf("") }
    val showCustomTagDialog = remember { mutableStateOf(false) }

    val index = tags.value.indexOf(remark.value)
    if (index >= 0) {
        selectedTagIndex.intValue = index
    } else if (remark.value.isNotBlank()) {
        // 如果不在且不为空，添加到列表前面并选中
        tags.value = listOf(remark.value) + tags.value
        selectedTagIndex.intValue = 0
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部区域：金额显示和日期选择
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 金额显示区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2E7D32), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "¥ ${amount.value.ifEmpty { "0.00" }}",
                        fontSize = 28.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = date.value,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                        IconButton(
                            onClick = { showDatePicker.value = true },
                            modifier = Modifier
                                .size(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "编辑日期",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            // 日期选择
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                OutlinedTextField(
//                    value = date.value,
//                    onValueChange = {},
//                    label = { Text("日期") },
//                    modifier = Modifier.weight(1f),
//                    shape = RoundedCornerShape(12.dp),
//                    singleLine = true,
//                    readOnly = true,
//                    enabled = true,
//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Default.Create,
//                            contentDescription = "选择日期",
//                            tint = Color(0xFF2E7D32)
//                        )
//                    },
//                    colors = OutlinedTextFieldDefaults.colors(
//                        focusedBorderColor = Color(0xFF2E7D32),
//                        unfocusedBorderColor = Color(0xFF2E7D32),
//                        cursorColor = Color(0xFF2E7D32),
//                        focusedLabelColor = Color(0xFF2E7D32),
//                        unfocusedLabelColor = Color(0xFF2E7D32)
//                    ),
//                )
//                Spacer(modifier = Modifier.width(12.dp))
//                Button(
//                    onClick = { showDatePicker.value = true },
//                    modifier = Modifier.width(90.dp).height(52.dp),
//                    shape = RoundedCornerShape(12.dp),
//                    contentPadding = PaddingValues(vertical = 12.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF2E7D32),
//                        contentColor = Color.White
//                    )
//                ) {
//                    Text(text = "选择")
//                }
//            }

            // 标签选择 - 可左右滚动
            Column {
//                Text(
//                    text = "支出标签",
//                    fontSize = 14.sp,
//                    color = Color.Gray,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(tags.value.size) { index ->
                            TagChip(
                                tag = tags.value[index],
                                isSelected = selectedTagIndex.intValue == index,
                                onClick = {
                                    selectedTagIndex.intValue = index
                                    remark.value = tags.value[index]
                                }
                            )
                        }
                    }
                    // 编辑按钮，固定在末尾
                    TagChip(
                        tag = "自定义",  // 或使用图标
                        isSelected = true,
                        onClick = { showCustomTagDialog.value = true },
                        isEditButton = true
                    )
                }
            }

            // 说明输入框
//            OutlinedTextField(
//                value = remark.value,
//                onValueChange = { remark.value = it },
//                label = { Text("说明") },
//                placeholder = { Text("资金用途") },
//                shape = RoundedCornerShape(12.dp),
//                modifier = Modifier.fillMaxWidth(),
//                minLines = 2,
//                maxLines = 2,
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color(0xFF2E7D32),
//                    unfocusedBorderColor = Color(0xFF2E7D32),
//                    cursorColor = Color(0xFF2E7D32),
//                    focusedLabelColor = Color(0xFF2E7D32),
//                    unfocusedLabelColor = Color(0xFF2E7D32)
//                )
//            )

            // 底部数字键盘
            NumberKeyboard(
                onNumberClick = { digit ->
                    if (digit == "." && amount.value.contains(".")) return@NumberKeyboard
                    if (digit == "." && amount.value.isEmpty()) return@NumberKeyboard
                    amount.value += digit
                },
                onDeleteClick = {
                    if (amount.value.isNotEmpty()) {
                        amount.value = amount.value.dropLast(1)
                    }
                },
                onSaveClick = {
                    if (amount.value.isNotEmpty() && isCorrectAmount(amount.value)) {
                        onSave(walletId, amount.value.toDouble(), date.value, remark.value)
                        amount.value = ""
                        date.value = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        remark.value = ""
                        selectedTagIndex.intValue = -1
                    } else {
                        showErrorAlert.value = true
                    }
                }
            )
        }
    }

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

    // 在 WalletAddForm 的末尾，添加对话框（如果尚未添加）
    if (showCustomTagDialog.value) {
        AlertDialog(
            onDismissRequest = { showCustomTagDialog.value = false },
            title = { Text("添加自定义标签") },
            text = {
                TextField(
                    value = customTagInput.value,
                    onValueChange = { customTagInput.value = it },
                    label = { Text("输入标签") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (customTagInput.value.isNotBlank() && !tags.value.contains(customTagInput.value)) {
                        // 注意：tags 仍为 listOf，需要改为 mutableStateOf<List<String>>
                        // 假设您已改为 val tags = remember { mutableStateOf(listOf("超市", ...)) }
                        tags.value = listOf(customTagInput.value) + tags.value
                        selectedTagIndex.intValue = 0
                        remark.value = customTagInput.value
                        customTagInput.value = ""
                    }
                    showCustomTagDialog.value = false
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(onClick = { showCustomTagDialog.value = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun TagChip(
    tag: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isEditButton: Boolean = false
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 3.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF2E7D32) else Color(0xFFE8F5E9),
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            if (isEditButton) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加自定义标签",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            } else {
                Text(
                    text = tag,
                    color = if (isSelected) Color.White else Color(0xFF2E7D32),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun NumberKeyboard(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
    ) {
        // 第一行：7 8 9 删除
        // 第二行：4 5 6 保存
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(3f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        KeyboardButton("7", onNumberClick)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        KeyboardButton("8", onNumberClick)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        KeyboardButton("9", onNumberClick)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        KeyboardButton("4", onNumberClick)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        KeyboardButton("5", onNumberClick)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        KeyboardButton("6", onNumberClick)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        KeyboardButton("1", onNumberClick)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        KeyboardButton("2", onNumberClick)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        KeyboardButton("3", onNumberClick)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        KeyboardButton(".", onNumberClick)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        KeyboardButton("0", onNumberClick, wide = true)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        KeyboardActionButton("⌫", Color(0xFFFFB74D), onActionClick = onDeleteClick)
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                KeyboardSaveActionButton("保存", Color(0xFF2E7D32), onActionClick = onSaveClick)
            }

        }

    }
}

@Composable
private fun KeyboardButton(
    text: String,
    onClick: (String) -> Unit,
    wide: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick(text) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            color = Color(0xFF2E7D32),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun KeyboardActionButton(
    text: String,
    backgroundColor: Color,
    onActionClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onActionClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun KeyboardSaveActionButton(
    text: String,
    backgroundColor: Color,
    onActionClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(184.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onActionClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center
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
