package com.example.hangdiary.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.ui.components.NavigationDrawer
import com.example.hangdiary.ui.components.TagManagementDialog
import com.example.hangdiary.viewmodel.DiaryListViewModel
import com.example.hangdiary.viewmodel.SettingsViewModel
import com.example.hangdiary.viewmodel.TagManagementViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DiaryListScreen(
    viewModel: DiaryListViewModel,
    tagViewModel: TagManagementViewModel,
    settingsViewModel: SettingsViewModel,
    navController: NavController,
    darkTheme: MutableState<Boolean>
) {
    val diaryList by viewModel.diaryListState.collectAsState()
    val tags by tagViewModel.state.collectAsState()
    val settings by settingsViewModel.settingsState.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 搜索相关状态
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchKeyword by remember { mutableStateOf("") }

    // 长按菜单状态
    var showContextMenu by remember { mutableStateOf(false) }
    var selectedDiary by remember { mutableStateOf<Diary?>(null) }

    // 标签管理对话框状态
    var showTagManagementDialog by remember { mutableStateOf(false) }

    // 多选模式状态
    var isMultiSelectMode by remember { mutableStateOf(false) }
    var selectedDiaries by remember { mutableStateOf(setOf<Long>()) }

    // 颜色选择对话框状态
    var showColorPickerDialog by remember { mutableStateOf(false) }

    // 日期格式化器，包含星期几（中文格式）
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日 HH点mm分 EEEE")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                tags = tags.tags,
                selectedTags = tags.selectedTags.map { it.id },
                onTagSelected = { tagId ->
                    viewModel.loadDiariesByTags(listOf(tagId))
                    scope.launch { drawerState.close() }
                },
                onTagsCleared = {
                    viewModel.loadAllDiaries()
                    scope.launch { drawerState.close() }
                },
                onTodoClick = {
                    navController.navigate("todoList")
                    scope.launch { drawerState.close() }
                },
                onSettingsClick = {
                    navController.navigate("settings")
                    scope.launch { drawerState.close() }
                },
                onAboutClick = {
                    navController.navigate("about")
                    scope.launch { drawerState.close() }
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                },
                navController = navController as NavHostController
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isMultiSelectMode) "已选择 ${selectedDiaries.size} 项" else "我的日记",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        if (isMultiSelectMode) {
                            IconButton(
                                onClick = {
                                    isMultiSelectMode = false
                                    selectedDiaries = emptySet()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "退出多选"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "菜单"
                                )
                            }
                        }
                    },
                    actions = {
                        if (isMultiSelectMode) {
                            // 多选模式下的操作按钮
                            IconButton(
                                onClick = {
                                    // 全选/取消全选
                                    if (selectedDiaries.size == diaryList.size) {
                                        selectedDiaries = emptySet()
                                    } else {
                                        selectedDiaries = diaryList.map { it.diary.id }.toSet()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selectedDiaries.size == diaryList.size) Icons.Default.Deselect else Icons.Default.SelectAll,
                                    contentDescription = if (selectedDiaries.size == diaryList.size) "取消全选" else "全选"
                                )
                            }

                            IconButton(
                                onClick = {
                                    // 批量删除
                                    if (selectedDiaries.isNotEmpty()) {
                                        viewModel.deleteDiaries(selectedDiaries.toList())
                                        isMultiSelectMode = false
                                        selectedDiaries = emptySet()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "批量删除"
                                )
                            }

                            IconButton(
                                onClick = {
                                    // 打开颜色选择对话框
                                    showColorPickerDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Colorize,
                                    contentDescription = "设置颜色"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { showSearchDialog = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "搜索"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isMultiSelectMode) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = if (isMultiSelectMode) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 待办事项按钮
                    FloatingActionButton(
                        onClick = { navController.navigate("todoList") },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "待办事项",
                            tint = Color.White
                        )
                    }

                    // 新建日记按钮
                    FloatingActionButton(
                        onClick = { navController.navigate("diaryDetail/0") },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "新建日记",
                            tint = Color.White
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (diaryList.isEmpty() && !viewModel.isLoading.value) {
                    // 空状态 - 只有在非加载状态下才显示
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "暂无日记",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "点击右下角按钮开始记录你的第一篇日记",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (viewModel.isLoading.value) {
                    // 加载状态
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // 根据视图模式显示日记列表或网格
                    if (settings?.viewMode ?: "list" == "grid") {
                        // 网格视图
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 150.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                count = diaryList.size,
                                key = { index -> diaryList[index].diary.id }
                            ) { index ->
                                val diaryWithTags = diaryList[index]
                                DiaryItemGrid(
                                    diary = diaryWithTags.diary,
                                    tags = diaryWithTags.tags,
                                    dateFormatter = dateFormatter,
                                    onClick = {
                                        if (isMultiSelectMode) {
                                            // 多选模式下，点击切换选择状态
                                            if (selectedDiaries.contains(diaryWithTags.diary.id)) {
                                                selectedDiaries = selectedDiaries - diaryWithTags.diary.id
                                            } else {
                                                selectedDiaries = selectedDiaries + diaryWithTags.diary.id
                                            }
                                        } else {
                                            navController.navigate("diaryDetail/${diaryWithTags.diary.id}")
                                        }
                                    },
                                    onLongClick = {
                                        if (!isMultiSelectMode) {
                                            // 长按进入多选模式
                                            isMultiSelectMode = true
                                            selectedDiaries = setOf(diaryWithTags.diary.id)
                                        } else {
                                            // 多选模式下，长按显示上下文菜单
                                            selectedDiary = diaryWithTags.diary
                                            showContextMenu = true
                                        }
                                    },
                                    isSelected = selectedDiaries.contains(diaryWithTags.diary.id),
                                    isMultiSelectMode = isMultiSelectMode
                                )
                            }
                        }
                    } else {
                        // 列表视图
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                count = diaryList.size,
                                key = { index -> diaryList[index].diary.id }
                            ) { index ->
                                val diaryWithTags = diaryList[index]
                                DiaryItem(
                                    diary = diaryWithTags.diary,
                                    tags = diaryWithTags.tags,
                                    dateFormatter = dateFormatter,
                                    onClick = {
                                        if (isMultiSelectMode) {
                                            // 多选模式下，点击切换选择状态
                                            if (selectedDiaries.contains(diaryWithTags.diary.id)) {
                                                selectedDiaries = selectedDiaries - diaryWithTags.diary.id
                                            } else {
                                                selectedDiaries = selectedDiaries + diaryWithTags.diary.id
                                            }
                                        } else {
                                            navController.navigate("diaryDetail/${diaryWithTags.diary.id}")
                                        }
                                    },
                                    onLongClick = {
                                        if (!isMultiSelectMode) {
                                            // 长按进入多选模式
                                            isMultiSelectMode = true
                                            selectedDiaries = setOf(diaryWithTags.diary.id)
                                        } else {
                                            // 多选模式下，长按显示上下文菜单
                                            selectedDiary = diaryWithTags.diary
                                            showContextMenu = true
                                        }
                                    },
                                    isSelected = selectedDiaries.contains(diaryWithTags.diary.id),
                                    isMultiSelectMode = isMultiSelectMode
                                )
                            }
                        }
                    }
                }
            }
        }

        // 搜索对话框
        if (showSearchDialog) {
            AlertDialog(
                onDismissRequest = { showSearchDialog = false },
                title = { Text("搜索日记") },
                text = {
                    OutlinedTextField(
                        value = searchKeyword,
                        onValueChange = { searchKeyword = it },
                        label = { Text("输入关键词") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.searchDiaries(searchKeyword)
                            showSearchDialog = false
                        }
                    ) {
                        Text("搜索")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showSearchDialog = false }
                    ) {
                        Text("取消")
                    }
                }
            )
        }

        // 长按弹出菜单
        selectedDiary?.let { diary ->
            if (showContextMenu) {
                AlertDialog(
                    onDismissRequest = { showContextMenu = false },
                    title = { Text("选择操作") },
                    text = { Text("对日记：${diary.title}") },
                    confirmButton = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 顶置/取消顶置
                            Button(
                                onClick = {
                                    viewModel.togglePinned(diary)
                                    showContextMenu = false
                                    selectedDiary = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = if (diary.isPinned) Icons.Default.VerticalAlignTop else Icons.Default.PushPin,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (diary.isPinned) "取消顶置" else "顶置日记")
                            }

                            // 编辑
                            Button(
                                onClick = {
                                    navController.navigate("diaryDetail/${diary.id}")
                                    showContextMenu = false
                                    selectedDiary = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("编辑")
                            }

                            // 添加标签
                            Button(
                                onClick = {
                                    showTagManagementDialog = true
                                    showContextMenu = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Label,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("添加标签")
                            }

                            // 删除
                            Button(
                                onClick = {
                                    viewModel.deleteDiary(diary)
                                    showContextMenu = false
                                    selectedDiary = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("删除")
                            }
                        }
                    }
                )
            }
        }

        // 标签管理对话框
        if (showTagManagementDialog) {
            selectedDiary?.let { diary ->
                TagManagementDialog(
                    diary = diary,
                    viewModel = tagViewModel,
                    onDismiss = { showTagManagementDialog = false }
                )
            }
        }

        // 颜色选择对话框
        if (showColorPickerDialog) {
            AlertDialog(
                onDismissRequest = { showColorPickerDialog = false },
                title = { Text("设置颜色") },
                text = {
                    Column {
                        Text(
                            text = "为选中的 ${selectedDiaries.size} 篇日记设置颜色",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // 颜色选项网格
                        val colorOptions = listOf(
                            "red" to Color(0xFFFFEBEE),
                            "pink" to Color(0xFFFCE4EC),
                            "purple" to Color(0xFFF3E5F5),
                            "deep_purple" to Color(0xFFEDE7F6),
                            "indigo" to Color(0xFFE8EAF6),
                            "blue" to Color(0xFFE3F2FD),
                            "light_blue" to Color(0xFFE1F5FE),
                            "cyan" to Color(0xFFE0F7FA),
                            "teal" to Color(0xFFE0F2F1),
                            "green" to Color(0xFFE8F5E9),
                            "light_green" to Color(0xFFF1F8E9),
                            "lime" to Color(0xFFF9FBE7),
                            "yellow" to Color(0xFFFFFDE7),
                            "amber" to Color(0xFFFFF8E1),
                            "orange" to Color(0xFFFFF3E0),
                            "deep_orange" to Color(0xFFFBE9E7),
                            "brown" to Color(0xFFEFEBE9),
                            "grey" to Color(0xFFFAFAFA),
                            "blue_grey" to Color(0xFFECEFF1)
                        )

                        // 使用LazyVerticalGrid显示颜色选项
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            modifier = Modifier.height(200.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(colorOptions.size) { index ->
                                val (colorName, colorValue) = colorOptions[index]
                                Card(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            // 应用选中的颜色
                                            viewModel.updateDiariesColor(selectedDiaries.toList(), colorName)
                                            showColorPickerDialog = false
                                            isMultiSelectMode = false
                                            selectedDiaries = emptySet()
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = colorValue
                                    ),
                                    border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                                ) {
                                    // 空内容，只显示颜色
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 清除颜色按钮
                        Button(
                            onClick = {
                                // 清除颜色
                                viewModel.updateDiariesColor(selectedDiaries.toList(), null)
                                showColorPickerDialog = false
                                isMultiSelectMode = false
                                selectedDiaries = emptySet()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("清除颜色")
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showColorPickerDialog = false }
                    ) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

/**
 * 日记网格项组件
 * 在网格视图中显示单条日记的卡片式布局
 * @param diary 日记数据
 * @param tags 标签列表
 * @param dateFormatter 日期格式化器
 * @param onClick 点击事件
 * @param onLongClick 长按事件
 * @param isSelected 是否选中
 * @param isMultiSelectMode 是否多选模式
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryItemGrid(
    diary: Diary,
    tags: List<Tag>,
    dateFormatter: DateTimeFormatter,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean = false,
    isMultiSelectMode: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (diary.isPinned) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else if (!diary.color.isNullOrEmpty()) {
                // 根据color字段设置卡片背景色
                when (diary.color) {
                    "red" -> Color(0xFFFFEBEE)
                    "pink" -> Color(0xFFFCE4EC)
                    "purple" -> Color(0xFFF3E5F5)
                    "deep_purple" -> Color(0xFFEDE7F6)
                    "indigo" -> Color(0xFFE8EAF6)
                    "blue" -> Color(0xFFE3F2FD)
                    "light_blue" -> Color(0xFFE1F5FE)
                    "cyan" -> Color(0xFFE0F7FA)
                    "teal" -> Color(0xFFE0F2F1)
                    "green" -> Color(0xFFE8F5E9)
                    "light_green" -> Color(0xFFF1F8E9)
                    "lime" -> Color(0xFFF9FBE7)
                    "yellow" -> Color(0xFFFFFDE7)
                    "amber" -> Color(0xFFFFF8E1)
                    "orange" -> Color(0xFFFFF3E0)
                    "deep_orange" -> Color(0xFFFBE9E7)
                    "brown" -> Color(0xFFEFEBE9)
                    "grey" -> Color(0xFFFAFAFA)
                    "blue_grey" -> Color(0xFFECEFF1)
                    else -> MaterialTheme.colorScheme.surface
                }
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (diary.isPinned) 8.dp else if (isSelected) 8.dp else 4.dp
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // 标题和顶置/收藏状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = diary.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 多选模式下的复选框
                    if (isMultiSelectMode) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null, // 由父组件控制
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // 顶置图标
                    if (diary.isPinned) {
                        Icon(
                            imageVector = Icons.Default.VerticalAlignTop,
                            contentDescription = "已顶置",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 日期
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = diary.createdAt.format(DateTimeFormatter.ofPattern("M月d日 EEEE")),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 内容预览
            Text(
                text = diary.content.take(50) + if (diary.content.length > 50) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 显示标签（只显示第一个标签）
            if (tags.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = tags.first().name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


/**
 * 日记列表项组件
 * 显示单条日记的卡片式布局
 * @param diary 日记数据
 * @param tags 标签列表
 * @param dateFormatter 日期格式化器
 * @param onClick 点击事件
 * @param onLongClick 长按事件
 * @param isSelected 是否选中
 * @param isMultiSelectMode 是否多选模式
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryItem(
    diary: Diary,
    tags: List<Tag>,
    dateFormatter: DateTimeFormatter,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean = false,
    isMultiSelectMode: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (diary.isPinned) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else if (!diary.color.isNullOrEmpty()) {
                // 根据color字段设置卡片背景色
                when (diary.color) {
                    "red" -> Color(0xFFFFEBEE)
                    "pink" -> Color(0xFFFCE4EC)
                    "purple" -> Color(0xFFF3E5F5)
                    "deep_purple" -> Color(0xFFEDE7F6)
                    "indigo" -> Color(0xFFE8EAF6)
                    "blue" -> Color(0xFFE3F2FD)
                    "light_blue" -> Color(0xFFE1F5FE)
                    "cyan" -> Color(0xFFE0F7FA)
                    "teal" -> Color(0xFFE0F2F1)
                    "green" -> Color(0xFFE8F5E9)
                    "light_green" -> Color(0xFFF1F8E9)
                    "lime" -> Color(0xFFF9FBE7)
                    "yellow" -> Color(0xFFFFFDE7)
                    "amber" -> Color(0xFFFFF8E1)
                    "orange" -> Color(0xFFFFF3E0)
                    "deep_orange" -> Color(0xFFFBE9E7)
                    "brown" -> Color(0xFFEFEBE9)
                    "grey" -> Color(0xFFFAFAFA)
                    "blue_grey" -> Color(0xFFECEFF1)
                    else -> MaterialTheme.colorScheme.surface
                }
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (diary.isPinned) 8.dp else if (isSelected) 8.dp else 4.dp
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题和顶置/收藏状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = diary.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 多选模式下的复选框
                    if (isMultiSelectMode) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null, // 由父组件控制
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }

                    // 顶置图标
                    if (diary.isPinned) {
                        Icon(
                            imageVector = Icons.Default.VerticalAlignTop,
                            contentDescription = "已顶置",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 日期
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = diary.createdAt.format(dateFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 内容预览
            Text(
                text = diary.content.take(100) + if (diary.content.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 显示标签
            if (tags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
