package com.example.hangdiary.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hangdiary.data.model.Todo
import com.example.hangdiary.viewmodel.TodoListViewModel
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.DatePicker
import androidx.compose.material3.TimePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip

/**
 * 待办事项列表页面
 * 显示所有待办事项，支持添加、编辑、完成等操作
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel,
    navController: NavController
) {
    val todoList by viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<Todo?>(null) }
    val listState = rememberLazyListState()

    // 获取顶部应用栏的背景颜色渐变
    val topAppBarBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer
        )
    )

    Scaffold(
        topBar = {
            // 现代化渐变顶部导航栏
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent // 透明背景，用于显示渐变
                ),
                modifier = Modifier
                    .background(brush = topAppBarBrush)
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "待办事项",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "管理你的任务",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    // 返回按钮
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    // 任务计数器徽章
                    if (todoList.todos.isNotEmpty()) {
                        val activeTodos = todoList.todos.count { !it.isCompleted }
                        Card(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            ),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Text(
                                text = "$activeTodos",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // 增强型浮动操作按钮
            ExtendedFloatingActionButton(
                onClick = { 
                    isEditMode = false
                    showDialog = true 
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                ),
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加待办",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "添加任务",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            // 状态管理
            when {
                todoList.isLoading -> {
                    // 加载状态
                    LoadingState()
                }
                todoList.error != null -> {
                    // 错误状态
                    ErrorState(
                        errorMessage = todoList.error ?: "加载失败",
                        onRetry = { viewModel.handleEvent(TodoListViewModel.TodoListEvent.SearchTodos("")) }
                    )
                }
                todoList.todos.isEmpty() -> {
                    // 空状态
                    EmptyState {
                        isEditMode = false
                        showDialog = true
                    }
                }
                else -> {
                    // 待办事项列表
                    TodoList(
                        activeTodos = todoList.todos.filter { !it.isCompleted },
                        completedTodos = todoList.todos.filter { it.isCompleted },
                        onToggleComplete = { todo ->
                            viewModel.handleEvent(
                                TodoListViewModel.TodoListEvent.ToggleTodoCompletion(todo)
                            )
                        },
                        onEdit = { todo ->
                            editingTodo = todo
                            isEditMode = true
                            showDialog = true
                        },
                        onDelete = { todo ->
                            viewModel.handleEvent(
                                TodoListViewModel.TodoListEvent.DeleteTodo(todo)
                            )
                        }
                    )
                }
            }
        }
    }

    // 现代化的待办事项对话框（添加/编辑）
    if (showDialog) {
        ModernTodoDialog(
            title = if (isEditMode) "编辑待办" else "添加新任务",
            initialTodo = editingTodo,
            categories = todoList.categories,
            onDismiss = { 
                showDialog = false
                editingTodo = null
            },
            onConfirm = { title, notes, dueDate, dueTime, category, priority ->
                if (isEditMode && editingTodo != null) {
                    val updatedTodo = editingTodo!!.copy(
                        title = title, 
                        notes = notes,
                        dueDate = dueDate,
                        dueTime = dueTime,
                        category = category,
                        priority = priority
                    )
                    viewModel.handleEvent(TodoListViewModel.TodoListEvent.UpdateTodo(updatedTodo))
                } else {
                    viewModel.handleEvent(TodoListViewModel.TodoListEvent.CreateTodo(
                        title = title,
                        notes = notes,
                        dueDate = dueDate,
                        dueTime = dueTime,
                        category = category,
                        priority = priority
                    ))
                }
                showDialog = false
                editingTodo = null
            }
        )
    }
}

/**
 * 加载状态组件
 */
@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "加载中...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * 待办事项列表组件
 */
@Composable
private fun TodoList(
    activeTodos: List<Todo>,
    completedTodos: List<Todo>,
    onToggleComplete: (Todo) -> Unit,
    onEdit: (Todo) -> Unit,
    onDelete: (Todo) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 待处理事项部分
        if (activeTodos.isNotEmpty()) {
            item {
                ModernSectionHeader(
                    title = "待处理",
                    count = activeTodos.size,
                    icon = Icons.Outlined.RadioButtonUnchecked,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(
                items = activeTodos,
                key = { it.id }
            ) {
                TodoItem(
                    todo = it,
                    onToggleComplete = { onToggleComplete(it) },
                    onEdit = { onEdit(it) },
                    onDelete = { onDelete(it) }
                )
            }
        }

        // 已完成事项部分
        if (completedTodos.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                ModernSectionHeader(
                    title = "已完成",
                    count = completedTodos.size,
                    icon = Icons.Default.CheckCircle,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            items(
                items = completedTodos,
                key = { it.id }
            ) {
                TodoItem(
                    todo = it,
                    onToggleComplete = { onToggleComplete(it) },
                    onEdit = { onEdit(it) },
                    onDelete = { onDelete(it) }
                )
            }
        }
    }
}

/**
 * Modern todo item component with enhanced styling
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
private fun TodoItem(
    todo: Todo,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // 增强的弹簧物理动画
    val animatedScale by animateFloatAsState(
        targetValue = if (todo.isCompleted) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300)) + 
                slideInVertically(initialOffsetY = { 30 }, animationSpec = tween(300)) +
                scaleIn(initialScale = 0.9f, animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(200)) + 
               slideOutVertically(targetOffsetY = { -30 }, animationSpec = tween(200)) +
               scaleOut(targetScale = 0.9f, animationSpec = tween(200))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                }
                .combinedClickable(
                    onClick = onEdit,
                    onLongClick = { /* Enhanced long press feedback */ }
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    todo.isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    else -> MaterialTheme.colorScheme.surface
                },
                contentColor = when {
                    todo.isCompleted -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.onSurface
                }
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (todo.isCompleted) 1.dp else 4.dp,
                pressedElevation = 8.dp
            ),
            border = if (!todo.isCompleted) {
                BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                )
            } else null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 增强的动画复选框
                ModernAnimatedCheckbox(
                    checked = todo.isCompleted,
                    onCheckedChange = onToggleComplete
                )

                // 增强的内容区域
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    // 标题 - 添加完成状态的动画过渡
                    AnimatedVisibility(
                        visible = !todo.isCompleted,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = todo.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    AnimatedVisibility(
                        visible = todo.isCompleted,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = todo.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // 分类和优先级标签
                    if (todo.category != null || todo.priority > 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 分类标签
                            if (todo.category != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Category,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = todo.category!!,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            
                            // 优先级标签
                            if (todo.priority > 1) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = when (todo.priority) {
                                        3 -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                        2 -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Flag,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = when (todo.priority) {
                                                3 -> MaterialTheme.colorScheme.error
                                                2 -> MaterialTheme.colorScheme.tertiary
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = when (todo.priority) {
                                                3 -> "高"
                                                2 -> "中"
                                                else -> "低"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = when (todo.priority) {
                                                3 -> MaterialTheme.colorScheme.error
                                                2 -> MaterialTheme.colorScheme.tertiary
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (todo.notes?.isNotBlank() == true) {
                        Spacer(modifier = Modifier.height(6.dp))
                        // 备注 - 添加完成状态的动画过渡
                        AnimatedVisibility(
                            visible = !todo.isCompleted,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = todo.notes ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        AnimatedVisibility(
                            visible = todo.isCompleted,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = todo.notes ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                textDecoration = TextDecoration.LineThrough,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // 截止日期和时间
                    if (todo.dueDate != null || todo.dueTime != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = buildString {
                                    todo.dueDate?.let { date ->
                                        append(date.format(DateTimeFormatter.ofPattern("M月d日", Locale.CHINA)))
                                    }
                                    todo.dueTime?.let { time ->
                                        if (todo.dueDate != null) append(" ")
                                        append(time.format(DateTimeFormatter.ofPattern("HH:mm")))
                                    }
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    // 增强的时间戳
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = todo.createdAt.format(DateTimeFormatter.ofPattern("M月d日 HH:mm", Locale.CHINA)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                // 现代风格的操作按钮
                Row {
                    // 编辑按钮
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "编辑",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // 删除按钮
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "删除",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modern animated checkbox with enhanced styling
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ModernAnimatedCheckbox(
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    val checkboxColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        animationSpec = tween(200),
        label = "checkbox color"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "checkbox scale"
    )
    
    Box(
        modifier = Modifier
            .size(28.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = if (checked) checkboxColor.copy(alpha = 0.1f) else Color.Transparent
            )
            .border(
                width = 2.dp,
                color = checkboxColor,
                shape = RoundedCornerShape(8.dp)
            )
            .combinedClickable(
                onClick = onCheckedChange
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = checkboxColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * 现代化待办事项编辑对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTodoDialog(
    title: String,
    initialTodo: Todo? = null,
    categories: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (String, String?, LocalDate?, LocalTime?, String?, Int) -> Unit
) {
    var todoTitle by remember { mutableStateOf(initialTodo?.title ?: "") }
    var todoNotes by remember { mutableStateOf(initialTodo?.notes ?: "") }
    var selectedDate by remember { mutableStateOf(initialTodo?.dueDate) }
    var selectedTime by remember { mutableStateOf(initialTodo?.dueTime) }
    var selectedCategory by remember { mutableStateOf(initialTodo?.category ?: "") }
    var selectedPriority by remember { mutableStateOf(initialTodo?.priority ?: 1) }
    var titleError by remember { mutableStateOf(false) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        title = { 
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 标题输入框
                OutlinedTextField(
                    value = todoTitle,
                    onValueChange = { 
                        todoTitle = it
                        if (titleError && it.isNotBlank()) {
                            titleError = false
                        }
                    },
                    label = { Text("任务标题") },
                    placeholder = { Text("输入任务标题") },
                    singleLine = true,
                    isError = titleError,
                    supportingText = if (titleError) { 
                        { Text("请输入任务标题") } 
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Assignment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                // 分类选择
                Box {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = { selectedCategory = it },
                        label = { Text("分类") },
                        placeholder = { Text("选择或输入分类") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showCategoryDropdown = !showCategoryDropdown }) {
                                Icon(
                                    imageVector = if (showCategoryDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                // 日期和时间选择行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 日期选择
                    OutlinedTextField(
                        value = selectedDate?.toString() ?: "",
                        onValueChange = { },
                        label = { Text("日期") },
                        placeholder = { Text("选择日期") },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    // 时间选择
                    OutlinedTextField(
                        value = selectedTime?.toString() ?: "",
                        onValueChange = { },
                        label = { Text("时间") },
                        placeholder = { Text("选择时间") },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }

                // 优先级选择
                Column {
                    Text(
                        text = "优先级",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            1 to "低",
                            2 to "中", 
                            3 to "高"
                        ).forEach { (priority, label) ->
                            FilterChip(
                                onClick = { selectedPriority = priority },
                                label = { Text(label) },
                                selected = selectedPriority == priority,
                                leadingIcon = if (selectedPriority == priority) {
                                    { Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                    }
                }

                // 备注输入框
                OutlinedTextField(
                    value = todoNotes,
                    onValueChange = { todoNotes = it },
                    label = { Text("备注") },
                    placeholder = { Text("添加备注（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    minLines = 2,
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Notes,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                // 按钮区域
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("取消")
                    }

                    Button(
                        onClick = { 
                            if (todoTitle.isBlank()) {
                                titleError = true
                            } else {
                                onConfirm(
                                    todoTitle.trim(),
                                    todoNotes.trim().takeIf { it.isNotBlank() },
                                    selectedDate,
                                    selectedTime,
                                    selectedCategory.takeIf { it.isNotBlank() },
                                    selectedPriority
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    )

    // 日期选择器
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 时间选择器
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("取消")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

/**
 * Modern empty state component with enhanced design
 */
@Composable
private fun EmptyState(onAddTodo: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Modern empty state illustration
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Assignment,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "还没有待办事项",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "添加你的第一个待办事项，开始高效管理时间！\n让每一天都更有条理。",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // 增强的行动号召按钮
        FilledTonalButton(
            onClick = onAddTodo,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "创建第一个待办",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Modern error state component
 */
@Composable
private fun ErrorState(errorMessage: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Modern error state illustration
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "加载失败",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // 增强的重试按钮
        OutlinedButton(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "重新加载",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Modern section header component
 */
@Composable
private fun ModernSectionHeader(
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Section icon with background
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = color
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Section title
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            
            // Count badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.2f)
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}
