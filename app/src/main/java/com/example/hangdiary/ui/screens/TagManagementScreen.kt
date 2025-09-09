package com.example.hangdiary.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.viewmodel.TagManagementViewModel
import com.example.hangdiary.viewmodel.TagManagementViewModel.TagManagementEvent
import kotlinx.coroutines.launch

/**
 * 标签管理页面
 * 提供标签的创建、编辑、删除功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManagementScreen(
    viewModel: TagManagementViewModel,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var tagToDelete by remember { mutableStateOf<Tag?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var tagToEdit by remember { mutableStateOf<Tag?>(null) }
    var newTagName by remember { mutableStateOf("") }
    var newTagColor by remember { mutableStateOf(Color.Blue.hashCode()) }
    
    // 加载所有标签
    LaunchedEffect(Unit) {
        viewModel.loadAllTags()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("标签管理") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        newTagName = ""
                        newTagColor = Color.Blue.hashCode()
                        showEditDialog = true
                        tagToEdit = null
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "添加标签")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.tags.isEmpty()) {
                // 空状态
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Label,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "暂无标签",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "点击右上角按钮创建新标签",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // 标签列表
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.tags) { tag ->
                        TagManagementItem(
                            tag = tag,
                            onEdit = {
                                tagToEdit = tag
                                newTagName = tag.name
                                newTagColor = tag.color
                                showEditDialog = true
                            },
                            onDelete = {
                                tagToDelete = tag
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
    
    // 删除确认对话框
    if (showDeleteDialog && tagToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                tagToDelete = null
            },
            title = { Text("确认删除") },
            text = { Text("确定要删除标签'${tagToDelete?.name}'吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            tagToDelete?.let { viewModel.handleEvent(TagManagementEvent.DeleteTag(it)) }
                            showDeleteDialog = false
                            tagToDelete = null
                        }
                    }
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteDialog = false
                        tagToDelete = null
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
    
    // 添加/编辑标签对话框
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { 
                showEditDialog = false
                tagToEdit = null
            },
            title = { Text(if (tagToEdit == null) "添加标签" else "编辑标签") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTagName,
                        onValueChange = { newTagName = it },
                        label = { Text("标签名称") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "标签颜色",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val colors = listOf(
                            Color.Red, Color.Blue, Color.Green, Color.Yellow,
                            Color.Magenta, Color.Cyan, Color(0xFFFF9800), Color(0xFF9C27B0)
                        )
                        
                        colors.forEach { color ->
                            Card(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { newTagColor = color.hashCode() },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (newTagColor == color.hashCode()) 
                                        color else color.copy(alpha = 0.5f)
                                ),
                                border = if (newTagColor == color.hashCode()) 
                                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
                                else null
                            ) {}
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTagName.isNotBlank()) {
                            scope.launch {
                                try {
                                    if (tagToEdit == null) {
                                        // 创建新标签
                                        viewModel.createTag(newTagName, newTagColor)
                                    } else {
                                        // 更新现有标签
                                        val updatedTag = tagToEdit!!.copy(name = newTagName, color = newTagColor)
                                        viewModel.handleEvent(TagManagementEvent.UpdateTag(updatedTag))
                                    }
                                    showEditDialog = false
                                    tagToEdit = null
                                } catch (_: Exception) {
                                    // 处理异常
                                }
                            }
                        }
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showEditDialog = false
                        tagToEdit = null
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

/**
 * 标签管理项组件
 * @param tag 标签数据
 * @param onEdit 编辑回调
 * @param onDelete 删除回调
 */
@Composable
fun TagManagementItem(
    tag: Tag,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = Color(tag.color),
                    modifier = Modifier.size(24.dp)
                ) {}
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = tag.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}