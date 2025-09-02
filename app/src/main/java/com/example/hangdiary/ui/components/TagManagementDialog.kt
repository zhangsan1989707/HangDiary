package com.example.hangdiary.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.viewmodel.TagManagementViewModel
import kotlinx.coroutines.launch

/**
 * 标签管理对话框
 * 用于为日记添加或移除标签
 * @param diary 当前日记
 * @param viewModel 标签管理视图模型
 * @param onDismiss 关闭对话框回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManagementDialog(
    diary: Diary,
    viewModel: TagManagementViewModel,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    
    var newTagName by remember { mutableStateOf("") }
    var showAddTagField by remember { mutableStateOf(false) }
    
    // 加载日记的标签
    LaunchedEffect(diary.id) {
        viewModel.loadTagsForDiary(diary.id)
    }
    
    // 加载所有标签
    LaunchedEffect(Unit) {
        viewModel.loadAllTags()
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Label,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "管理标签",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭"
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 当前日记的标签
                if (state.selectedTags.isNotEmpty()) {
                    Text(
                        text = "当前标签",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.selectedTags.forEach { tag ->
                            InputChip(
                                selected = true,
                                onClick = {
                                    scope.launch {
                                        viewModel.removeTagFromDiary(diary.id, tag.id)
                                    }
                                },
                                label = { Text(tag.name) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "移除标签",
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = InputChipDefaults.inputChipColors(
                                    containerColor = Color(tag.color),
                                    labelColor = Color.White,
                                    leadingIconColor = Color.White,
                                    trailingIconColor = Color.White,
                                    disabledContainerColor = Color.Gray,
                                    disabledLabelColor = Color.LightGray,
                                    disabledLeadingIconColor = Color.LightGray,
                                    disabledTrailingIconColor = Color.LightGray,
                                    selectedContainerColor = Color(tag.color),
                                    disabledSelectedContainerColor = Color.Gray,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White,
                                    selectedTrailingIconColor = Color.White
                                )
                            )
                        }
                    }
                }
                
                // 所有标签
                Text(
                    text = "所有标签",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                if (state.tags.isEmpty()) {
                    Text(
                        text = "暂无标签，请添加新标签",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                    ) {
                        items(state.tags) { tag ->
                            val isSelected = state.selectedTags.any { it.id == tag.id }
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = MaterialTheme.shapes.small,
                                        color = Color(tag.color),
                                        modifier = Modifier.size(16.dp)
                                    ) {}
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        text = tag.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        scope.launch {
                                            if (isSelected) {
                                                viewModel.removeTagFromDiary(diary.id, tag.id)
                                            } else {
                                                viewModel.addTagToDiary(diary.id, tag.id)
                                            }
                                        }
                                    },
                                    label = {
                                        Text(if (isSelected) "已添加" else "添加")
                                    }
                                )
                            }
                        }
                    }
                }
                
                // 添加新标签区域
                Spacer(modifier = Modifier.height(16.dp))
                
                if (showAddTagField) {
                    OutlinedTextField(
                        value = newTagName,
                        onValueChange = { newTagName = it },
                        label = { Text("新标签名称") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (newTagName.isNotBlank()) {
                                        scope.launch {
                                            val color = viewModel.generateRandomTagColor()
                                            viewModel.createTag(newTagName, color)
                                            newTagName = ""
                                            showAddTagField = false
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "添加标签"
                                )
                            }
                        }
                    )
                } else {
                    TextButton(
                        onClick = { showAddTagField = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("添加新标签")
                    }
                }
                
                // 错误提示
                state.error?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("完成")
            }
        }
    )
}