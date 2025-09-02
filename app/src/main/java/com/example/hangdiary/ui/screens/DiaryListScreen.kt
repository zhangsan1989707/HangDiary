package com.example.hangdiary.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.viewmodel.DiaryListViewModel
import java.time.format.DateTimeFormatter

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ModalNavigationDrawer

import com.example.hangdiary.ui.components.NavigationDrawer
import com.example.hangdiary.ui.components.TagManagementDialog
import com.example.hangdiary.viewmodel.CategoryViewModel
import com.example.hangdiary.viewmodel.TagManagementViewModel
import kotlinx.coroutines.launch

/**
 * 日记列表页面
 * 显示所有日记的列表，支持搜索、筛选、顶置等功能
 * @param viewModel 日记列表视图模型
 * @param navController 导航控制器
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DiaryListScreen(
    viewModel: DiaryListViewModel,
    categoryViewModel: CategoryViewModel,
    tagViewModel: TagManagementViewModel,
    navController: NavController
) {
    val diaryList by viewModel.diaryListState.collectAsState()
    val categories by categoryViewModel.categoryListState.collectAsState()
    val tags by tagViewModel.state.collectAsState()

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

    // 日期格式化器
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                categories = categories,
                tags = tags.tags,
                selectedCategoryId = null, // 需要跟踪当前选中的分类
                selectedTags = tags.selectedTags.map { it.id },
                onCategorySelected = { categoryId ->
                    if (categoryId != null) {
                        viewModel.loadDiariesByCategory(categoryId)
                    } else {
                        viewModel.loadAllDiaries()
                    }
                    scope.launch { drawerState.close() }
                },
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
                    // TODO: 导航到设置页面
                    scope.launch { drawerState.close() }
                },
                onAboutClick = {
                    // TODO: 导航到关于页面
                    scope.launch { drawerState.close() }
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "我的日记",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "菜单"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showSearchDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "搜索"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                if (diaryList.isEmpty()) {
                    // 空状态
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
                } else {
                    // 日记列表
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = diaryList,
                            key = { it.diary.id }
                        ) { diaryWithTags ->
                            DiaryItem(
                                diary = diaryWithTags.diary,
                                tags = diaryWithTags.tags,
                                dateFormatter = dateFormatter,
                                onClick = {
                                    navController.navigate("diaryDetail/${diaryWithTags.diary.id}")
                                },
                                onLongClick = {
                                    selectedDiary = diaryWithTags.diary
                                    showContextMenu = true
                                },
                                onFavoriteClick = {
                                    viewModel.toggleFavorite(diaryWithTags.diary)
                                }
                            )
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
                                    imageVector = Icons.Default.Label,
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
    }
    
    // 标签管理对话框
    if (showTagManagementDialog && selectedDiary != null) {
        TagManagementDialog(
            diary = selectedDiary!!,
            viewModel = tagViewModel,
            onDismiss = { showTagManagementDialog = false }
        )
    }
}

/**
 * 日记列表项组件
 * 显示单条日记的卡片式布局
 * @param diary 日记数据
 * @param dateFormatter 日期格式化器
 * @param onClick 点击事件
 * @param onLongClick 长按事件
 * @param onFavoriteClick 收藏点击事件
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryItem(
    diary: Diary,
    tags: List<Tag>,
    dateFormatter: DateTimeFormatter,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFavoriteClick: () -> Unit
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
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (diary.isPinned) 8.dp else 4.dp
        )
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
                    // 顶置图标
                    if (diary.isPinned) {
                        Icon(
                            imageVector = Icons.Default.VerticalAlignTop,
                            contentDescription = "已顶置",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // 收藏图标
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (diary.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (diary.isFavorite) "取消收藏" else "收藏",
                            tint = if (diary.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
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

            // 日期和分类
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = diary.createdAt.format(dateFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                diary.categoryId?.let {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "分类 $it",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                
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
}