package com.example.hangdiary.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hangdiary.data.model.Todo
import com.example.hangdiary.viewmodel.TodoListViewModel
import java.time.format.DateTimeFormatter

/**
 * 待办事项列表页面
 * 显示所有待办事项，支持添加、编辑、完成等操作
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel,
    navController: NavController
) {
    val todoList by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<Todo?>(null) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "待办事项",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
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
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加待办",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (todoList.todos.isEmpty()) {
                // 空状态
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "暂无待办事项",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "点击右下角按钮添加新的待办",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            } else {
                // 待办事项列表
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = todoList.todos,
                        key = { it.id }
                    ) { todo ->
                        TodoItem(
                            todo = todo,
                            onToggleComplete = {
                                viewModel.handleEvent(TodoListViewModel.TodoListEvent.ToggleTodoCompletion(todo))
                            },
                            onEdit = {
                                editingTodo = todo
                                showEditDialog = true
                            },
                            onDelete = {
                                viewModel.handleEvent(TodoListViewModel.TodoListEvent.DeleteTodo(todo))
                            }
                        )
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
 * 待办事项项组件
 */
@Composable
private fun TodoItem(
    todo: Todo,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (todo.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 完成状态复选框
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggleComplete() }
            )

            // 内容区域
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (todo.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )

                if (todo.content?.isNotBlank() == true) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = todo.content ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (todo.isCompleted) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = todo.createdAt.format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // 删除按钮
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * 待办事项编辑对话框
 */
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = todoTitle,
                    onValueChange = { todoTitle = it },
                    label = { Text("标题") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = todoContent,
                    onValueChange = { todoContent = it },
                    label = { Text("内容") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(todoTitle, todoContent) },
                enabled = todoTitle.isNotBlank()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}