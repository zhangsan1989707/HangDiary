package com.example.hangdiary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hangdiary.viewmodel.SettingsViewModel

/**
 * 设置页面
 * 提供应用的各种设置选项
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    darkTheme: MutableState<Boolean>,
    onBack: () -> Unit
) {
    val settingsState by settingsViewModel.settingsState.collectAsState()
    val isLoading by settingsViewModel.isLoading.collectAsState()
    val error by settingsViewModel.error.collectAsState()

    // 视图选项
    val viewModeOptions = listOf("list", "grid")
    val viewModeLabels = mapOf(
        "list" to "列表",
        "grid" to "网格"
    )

    // 字体大小选项
    val fontSizeOptions = listOf(12, 14, 16, 18, 20, 22, 24)
    
    // 监听设置变化并更新主题
    LaunchedEffect(settingsState) {
        settingsState?.let { settings ->
            darkTheme.value = settings.isDarkMode
        }
    }

    // 错误提示
    if (error != null) {
        LaunchedEffect(error) {
            // 显示错误提示
            settingsViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            settingsState?.let { settings ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    // 主题设置
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "主题",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "暗黑模式")
                                    Switch(
                                        checked = settings.isDarkMode,
                                        onCheckedChange = { isDarkMode ->
                                            settingsViewModel.updateDarkMode(isDarkMode)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 视图设置
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "视图",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "视图模式")
                                    CustomDropdownMenu(
                                                items = viewModeOptions,
                                                selectedItem = settings.viewMode,
                                                itemLabel = { viewModeLabels[it] ?: it },
                                                onItemSelected = { viewMode ->
                                                    settingsViewModel.updateViewMode(viewMode)
                                                }
                                            )
                                }

                            }
                        }
                    }





                    // 默认设置
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "默认设置",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "默认日记颜色")
                                    ColorPicker(
                                        selectedColor = settings.defaultDiaryColor,
                                        onColorSelected = { color ->
                                            settingsViewModel.updateDefaultDiaryColor(color)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 自定义下拉菜单组件
 */
@Composable
fun <T> CustomDropdownMenu(
    items: List<T>,
    selectedItem: T,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = itemLabel(selectedItem))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "下拉菜单"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = itemLabel(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * 颜色选择器组件
 * 允许用户选择日记颜色
 */
@Composable
fun ColorPicker(
    selectedColor: String?,
    onColorSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    // 预定义颜色选项
    val colorOptions = listOf(
        "#FF5733" to "红色",
        "#33FF57" to "绿色",
        "#3357FF" to "蓝色",
        "#F3FF33" to "黄色",
        "#FF33F3" to "粉色",
        "#33FFF3" to "青色",
        "#000000" to "黑色",
        "#808080" to "灰色",
        null to "默认"
    )
    
    // 当前选中的颜色显示文本
    val displayText = selectedColor ?: "默认"
    
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            // 显示颜色预览
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            ) {
                if (selectedColor != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(selectedColor)))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "默认",
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.Center),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Text(text = displayText)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "颜色选择"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            colorOptions.forEach { (color, name) ->
                DropdownMenuItem(
                    text = { 
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 颜色预览
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp)
                            ) {
                                if (color != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(color)))
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surface)
                                            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "默认",
                                            modifier = Modifier
                                                .size(16.dp)
                                                .align(Alignment.Center),
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            Text(text = name)
                        }
                    },
                    onClick = {
                        onColorSelected(color)
                        expanded = false
                    }
                )
            }
        }
    }
}