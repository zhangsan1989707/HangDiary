package com.example.hangdiary.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hangdiary.R
import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.repository.DiaryRepository
import com.example.hangdiary.viewmodel.DiaryDetailViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


/**
 * 日记详情页面
 * 支持创建和编辑日记
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(
    navController: NavController,
    diaryRepository: DiaryRepository,
    diaryId: Long = 0L
) {
    val context = LocalContext.current
    val viewModel: DiaryDetailViewModel = remember { DiaryDetailViewModel(diaryRepository) }
    
    // 初始化日记数据
    LaunchedEffect(diaryId) {
        viewModel.loadDiary(diaryId)
    }
    
    val diary by viewModel.diaryState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (diaryId == 0L) stringResource(R.string.new_diary) else stringResource(R.string.diary_detail)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (diaryId != 0L && !viewModel.isEditing) {
                        IconButton(onClick = { viewModel.isEditing = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit))
                        }
                        IconButton(onClick = {
                            scope.launch {
                                viewModel.deleteDiary()
                                navController.navigateUp()
                            }
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete))
                        }
                    }
                    if (viewModel.isEditing) {
                        IconButton(onClick = {
                            scope.launch {
                                viewModel.saveDiary()
                                viewModel.isEditing = false
                            }
                        }) {
                            Icon(Icons.Filled.Save, contentDescription = stringResource(R.string.save))
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (diaryId == 0L) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            viewModel.saveDiary()
                            navController.navigateUp()
                        }
                    }
                ) {
                    Icon(Icons.Filled.Save, contentDescription = stringResource(R.string.save))
                }
            }
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
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = error ?: stringResource(R.string.error_occurred))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (viewModel.isEditing || diaryId == 0L) {
                    // 编辑模式
                    EditableContent(
                        title = viewModel.title,
                        onTitleChange = { viewModel.title = it },
                        content = viewModel.content,
                        onContentChange = { viewModel.content = it },
                        imagePaths = viewModel.imagePaths,
                        onImagePathsChange = { viewModel.imagePaths = it }
                    )
                } else {
                    // 查看模式
                    diary?.let { ViewableContent(it) }
                }
            }
        }
    }
}

/**
 * 编辑模式内容区域
 */
@Composable
fun EditableContent(
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    imagePaths: List<String>,
    onImagePathsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    // 获取当前日期并格式化为"xxxx年X月x日星期X"格式
    val currentDate = LocalDate.now()
    val year = currentDate.year
    val month = currentDate.monthValue
    val day = currentDate.dayOfMonth
    val dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINA)
    val formattedDate = "${year}年${month}月${day}日$dayOfWeek"
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题输入
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // 日期显示
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "日期",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
        
        // 内容输入
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            label = { Text(stringResource(R.string.content)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            maxLines = 10
        )
        
        // 图片上传区域
        ImageSection(
            imagePaths = imagePaths,
            onImagePathsChange = onImagePathsChange
        )
    }
}

/**
 * 查看模式内容区域
 */
@Composable
fun ViewableContent(
    diary: Diary
) {
    val scrollState = rememberScrollState()
    
    // 格式化日期
    val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日 EEEE")
    val formattedDate = diary.createdAt.format(formatter)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        Text(
            text = diary.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // 日期显示
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "日期",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
        
        // 内容
        Text(
            text = diary.content,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        
        // 图片区域
        if (diary.imagePaths.isNotEmpty()) {
            Text(
                text = "图片",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(diary.imagePaths.size) { index ->
                    ImageItem(
                        path = diary.imagePaths[index],
                        contentDescription = "图片 ${index + 1}"
                    )
                }
            }
        }
    }
}

/**
 * 图片项组件
 */
@Composable
fun ImageItem(
    path: String,
    contentDescription: String
) {
    Card(
        modifier = Modifier.size(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = path,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * 图片区域组件 - 简化版本
 */
@Composable
fun ImageSection(
    imagePaths: List<String>,
    onImagePathsChange: (List<String>) -> Unit
) {
    val imagesText = stringResource(R.string.images)
    
    Column {
        Text(text = imagesText)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(imagePaths.size) { index ->
                ImageItem(
                    path = imagePaths[index],
                    contentDescription = imagesText
                )
            }
            
            item {
                Card(
                    modifier = Modifier.size(100.dp),
                    onClick = { /* 简化版本，暂时不实现添加图片功能 */ }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                    }
                }
            }
        }
    }
}