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
import java.util.Locale

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
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<Todo?>(null) }
    var selectedTodo by remember { mutableStateOf<Todo?>(null) }
    val listState = rememberLazyListState()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日 HH点mm分", Locale.CHINA)

    Scaffold(
        topBar = {
            // Modern gradient top bar with enhanced styling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button with modern styling
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
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Enhanced title with subtitle
                    Column(
                        modifier = Modifier.weight(1f)
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
                    
                    // Task counter badge
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
            }
        },
        floatingActionButton = {
            // Enhanced floating action button with modern design
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
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
            // 显示加载状态
            if (todoList.isLoading) {
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
            } else {
                // 显示错误状态
                if (todoList.error != null) {
                    ErrorState(
                        errorMessage = todoList.error ?: "加载失败",
                        onRetry = { viewModel.handleEvent(TodoListViewModel.TodoListEvent.SearchTodos("")) }
                    )
                } else {
                    // 显示待办事项列表或空状态
                    if (todoList.todos.isEmpty()) {
                        EmptyState {
                            showAddDialog = true
                        }
                    } else {
                        // Modern todo list with enhanced sections
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // 将待办事项分为已完成和未完成两组
                            val completedTodos = todoList.todos.filter { it.isCompleted }
                            val activeTodos = todoList.todos.filter { !it.isCompleted }

                            // Modern section header for active todos
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
                                ) { todo ->
                                    TodoItem(
                                        todo = todo,
                                        onToggleComplete = {
                                            viewModel.handleEvent(
                                                TodoListViewModel.TodoListEvent.ToggleTodoCompletion(todo)
                                            )
                                        },
                                        onEdit = {
                                            editingTodo = todo
                                            showEditDialog = true
                                        },
                                        onDelete = {
                                            viewModel.handleEvent(
                                                TodoListViewModel.TodoListEvent.DeleteTodo(todo)
                                            )
                                        }
                                    )
                                }
                            }

                            // Modern section header for completed todos
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
                                ) { todo ->
                                    TodoItem(
                                        todo = todo,
                                        onToggleComplete = {
                                            viewModel.handleEvent(
                                                TodoListViewModel.TodoListEvent.ToggleTodoCompletion(todo)
                                            )
                                        },
                                        onEdit = {
                                            editingTodo = todo
                                            showEditDialog = true
                                        },
                                        onDelete = {
                                            viewModel.handleEvent(
                                                TodoListViewModel.TodoListEvent.DeleteTodo(todo)
                                            )
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

    // 添加待办对话框
    if (showAddDialog) {
        TodoDialog(
            title = "添加待办",
            onDismiss = { showAddDialog = false },
            onConfirm = { title, content ->
                viewModel.handleEvent(TodoListViewModel.TodoListEvent.CreateTodo(title, content))
                showAddDialog = false
            }
        )
    }

    // 编辑待办对话框
    if (showEditDialog && editingTodo != null) {
        TodoDialog(
            title = "编辑待办",
            initialTitle = editingTodo!!.title,
            initialContent = editingTodo!!.content ?: "",
            onDismiss = {
                showEditDialog = false
                editingTodo = null
            },
            onConfirm = { title, content ->
                val updatedTodo = editingTodo!!.copy(title = title, content = content)
                viewModel.handleEvent(TodoListViewModel.TodoListEvent.UpdateTodo(updatedTodo))
                showEditDialog = false
                editingTodo = null
            }
        )
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
    // Enhanced animations with spring physics
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
                // Enhanced animated checkbox with modern styling
                ModernAnimatedCheckbox(
                    checked = todo.isCompleted,
                    onCheckedChange = { onToggleComplete() }
                )

                // Enhanced content area with better spacing
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

                    if (todo.content?.isNotBlank() == true) {
                        Spacer(modifier = Modifier.height(6.dp))
                        // 内容 - 添加完成状态的动画过渡
                        AnimatedVisibility(
                            visible = !todo.isCompleted,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = todo.content ?: "",
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
                                text = todo.content ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                textDecoration = TextDecoration.LineThrough,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    // Enhanced timestamp with modern styling
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

                // Modern action buttons
                Row {
                    // Edit button
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
                    
                    // Delete button with enhanced styling
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
 * 待办事项编辑对话框
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TodoDialog(
    title: String,
    initialTitle: String = "",
    initialContent: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var todoTitle by remember { mutableStateOf(initialTitle) }
    var todoContent by remember { mutableStateOf(initialContent) }
    var titleError by remember { mutableStateOf(false) }

    // 添加对话框显示和隐藏的动画
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + scaleIn(initialScale = 0.95f),
        exit = fadeOut() + scaleOut(targetScale = 0.95f)
    ) {
        AlertDialog(
            onDismissRequest = { 
                if (title.isNotEmpty() && todoTitle.isNotBlank()) {
                    titleError = false
                }
                onDismiss() 
            },
            confirmButton = {},
            dismissButton = {},
            title = { Text(text = title) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 标题输入框
                    OutlinedTextField(
                        value = todoTitle,
                        onValueChange = { 
                            todoTitle = it
                            // 清除错误状态
                            if (titleError && it.isNotBlank()) {
                                titleError = false
                            }
                        },
                        label = { Text("标题") },
                        placeholder = { Text("请输入待办事项标题") },
                        singleLine = true,
                        isError = titleError,
                        supportingText = if (titleError) { 
                            { Text("请输入标题") } 
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    // 内容输入框
                    OutlinedTextField(
                        value = todoContent,
                        onValueChange = { todoContent = it },
                        label = { Text("内容") },
                        placeholder = { Text("请输入待办事项内容（可选）") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
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
                            Text(
                                "取消",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = {
                                if (todoTitle.isBlank()) {
                                    titleError = true
                                } else {
                                    onConfirm(todoTitle.trim(), todoContent.trim())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "确定",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
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
                imageVector = Icons.Outlined.Assignment,
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
        
        // Enhanced call-to-action button
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
        
        // Enhanced retry button
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
}/**
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
