package com.example.hangdiary.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

import androidx.compose.ui.graphics.*

import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hangdiary.R
import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.data.repository.DiaryRepository
import com.example.hangdiary.data.repository.TagRepository
import com.example.hangdiary.viewmodel.DiaryDetailViewModel
import com.example.hangdiary.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.format.TextStyle

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

/**
 * 编辑模式内容区域
 */
/**
 * 颜色选择器组件
 */
@Composable
fun ColorPicker(
    selectedColor: String?,
    onColorChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Pair("red", Color(0xFFFFEBEE)),
        Pair("pink", Color(0xFFFCE4EC)),
        Pair("purple", Color(0xFFF3E5F5)),
        Pair("deep_purple", Color(0xFFEDE7F6)),
        Pair("indigo", Color(0xFFE8EAF6)),
        Pair("blue", Color(0xFFE3F2FD)),
        Pair("light_blue", Color(0xFFE1F5FE)),
        Pair("cyan", Color(0xFFE0F7FA)),
        Pair("teal", Color(0xFFE0F2F1)),
        Pair("green", Color(0xFFE8F5E9)),
        Pair("light_green", Color(0xFFF1F8E9)),
        Pair("lime", Color(0xFFF9FBE7)),
        Pair("yellow", Color(0xFFFFFDE7)),
        Pair("amber", Color(0xFFFFF8E1)),
        Pair("orange", Color(0xFFFFF3E0)),
        Pair("deep_orange", Color(0xFFFBE9E7)),
        Pair("brown", Color(0xFFEFEBE9)),
        Pair("grey", Color(0xFFFAFAFA)),
        Pair("blue_grey", Color(0xFFECEFF1)),
        Pair(null, Color.Transparent)
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "日记卡片颜色",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 颜色预览
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "当前颜色: ",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Card(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedColor != null) {
                        colors.find { it.first == selectedColor }?.second ?: Color.Transparent
                    } else {
                        Color.Transparent
                    }
                ),
                border = if (selectedColor == null) {
                    BorderStroke(1.dp, Color.Gray)
                } else {
                    null
                }
            ) {}
            
            Text(
                text = if (selectedColor != null) {
                    when (selectedColor) {
                        "red" -> "红色"
                        "pink" -> "粉色"
                        "purple" -> "紫色"
                        "deep_purple" -> "深紫色"
                        "indigo" -> "靛蓝色"
                        "blue" -> "蓝色"
                        "light_blue" -> "浅蓝色"
                        "cyan" -> "青色"
                        "teal" -> "蓝绿色"
                        "green" -> "绿色"
                        "light_green" -> "浅绿色"
                        "lime" -> "酸橙色"
                        "yellow" -> "黄色"
                        "amber" -> "琥珀色"
                        "orange" -> "橙色"
                        "deep_orange" -> "深橙色"
                        "brown" -> "棕色"
                        "grey" -> "灰色"
                        "blue_grey" -> "蓝灰色"
                        else -> "自定义"
                    }
                } else {
                    "默认"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 颜色选择网格
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
        ) {
            items(colors.size / 5 + if (colors.size % 5 > 0) 1 else 0) { rowIndex ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (colIndex in 0 until 5) {
                        val index = rowIndex * 5 + colIndex
                        if (index < colors.size) {
                            val colorPair = colors[index]
                            Card(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable {
                                        onColorChange(colorPair.first)
                                    },
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(
                                    containerColor = colorPair.second
                                ),
                                border = if (colorPair.first == selectedColor) {
                                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                } else if (colorPair.first == null) {
                                    BorderStroke(1.dp, Color.Gray)
                                } else {
                                    null
                                }
                            ) {
                                if (colorPair.first == null) {
                                    // 默认颜色显示一个斜线
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Canvas(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            val strokeWidth = 2f
                                            drawLine(
                                                color = Color.Gray,
                                                start = Offset(0f, size.height),
                                                end = Offset(size.width, 0f),
                                                strokeWidth = strokeWidth
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun EditableContent(
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    imagePaths: List<String>,
    onImagePathsChange: (List<String>) -> Unit,
    selectedTags: List<Tag>,
    onSelectedTagsChange: (List<Tag>) -> Unit,
    allTags: List<Tag>,
    onShowTagDialogChange: (Boolean) -> Unit,
    viewModel: DiaryDetailViewModel,
    color: String?,
    onColorChange: (String?) -> Unit,
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

        // 标签选择区域
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "标签",
            style = MaterialTheme.typography.titleMedium
        )

        // 已选择的标签
        if (selectedTags.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedTags) { tag ->
                    Card(
                        modifier = Modifier.clickable {
                            // 取消选择标签
                            val updatedTags = selectedTags.filter { it.id != tag.id }
                            onSelectedTagsChange(updatedTags)
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(tag.color)
                        )
                    ) {
                        Text(
                            text = tag.name,
                            modifier = Modifier.padding(8.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 统一的标签选择按钮
        TextButton(
            onClick = { onShowTagDialogChange(true) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.textButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = if (allTags.isEmpty()) "创建第一个标签" else "选择/创建标签")
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "打开标签选择"
                )
            }
        }

        // 颜色选择器
        ColorPicker(
            selectedColor = color,
            onColorChange = onColorChange
        )
        

    }
}

/**
 * 颜色预览组件
 */
@Composable
fun ColorPreview(
    color: String?,
    modifier: Modifier = Modifier
) {
    val colors = mapOf(
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

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "卡片颜色: ",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Card(
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = if (color != null) {
                    colors[color] ?: Color.Transparent
                } else {
                    Color.Transparent
                }
            ),
            border = if (color == null) {
                BorderStroke(1.dp, Color.Gray)
            } else {
                null
            }
        ) {}
        
        Text(
            text = if (color != null) {
                when (color) {
                    "red" -> "红色"
                    "pink" -> "粉色"
                    "purple" -> "紫色"
                    "deep_purple" -> "深紫色"
                    "indigo" -> "靛蓝色"
                    "blue" -> "蓝色"
                    "light_blue" -> "浅蓝色"
                    "cyan" -> "青色"
                    "teal" -> "蓝绿色"
                    "green" -> "绿色"
                    "light_green" -> "浅绿色"
                    "lime" -> "酸橙色"
                    "yellow" -> "黄色"
                    "amber" -> "琥珀色"
                    "orange" -> "橙色"
                    "deep_orange" -> "深橙色"
                    "brown" -> "棕色"
                    "grey" -> "灰色"
                    "blue_grey" -> "蓝灰色"
                    else -> "自定义"
                }
            } else {
                "默认"
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * 查看模式内容区域
 */
@Composable
fun ViewableContent(
    diary: Diary,
    tags: List<Tag> = emptyList()
) {
    val scrollState = rememberScrollState()

    // 格式化日期（中文格式）
    val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日 HH点mm分ss秒 EEEE", Locale.CHINA)
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
            text = diary.title.ifBlank { "无标题" },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = if (diary.title.isBlank()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
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
            text = diary.content.ifBlank { "暂无内容" },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            color = if (diary.content.isBlank()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
        )
        
        // 颜色信息
        ColorPreview(color = diary.color)

        // 标签显示
        if (tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "标签",
                style = MaterialTheme.typography.titleMedium
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tags) { tag ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(tag.color)
                        )
                    ) {
                        Text(
                            text = tag.name,
                            modifier = Modifier.padding(8.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }

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
 * 日记详情页面
 * 支持创建和编辑日记
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(
    navController: NavController,
    diaryRepository: DiaryRepository,
    tagRepository: TagRepository,
    settingsViewModel: SettingsViewModel,
    diaryId: Long = 0L
) {
    val context = LocalContext.current
    val settings by settingsViewModel.settingsState.collectAsState()
    val viewModel: DiaryDetailViewModel = hiltViewModel()

    val diary by viewModel.diaryState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // 标签相关状态
    var selectedTags by remember { mutableStateOf<List<Tag>>(emptyList()) }
    var allTags by remember { mutableStateOf<List<Tag>>(emptyList()) }
    var showTagDialog by remember { mutableStateOf(false) }
    var newTagName by remember { mutableStateOf("") }
    var newTagColor by remember { mutableStateOf(Color.Blue.toArgb()) }

    // 日记相关状态
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<String>>(emptyList()) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 初始化日记数据
    // ViewModel现在会在初始化时自动加载日记，无需手动调用

    // 加载标签数据
    LaunchedEffect(Unit) {
        viewModel.loadAllTags()
        viewModel.allTags.collect { tags ->
            allTags = tags
        }
    }

    // 监听日记数据变化
    LaunchedEffect(diary) {
        diary?.let {
            title = it.title
            content = it.content
            imageUris = it.imagePaths ?: emptyList()

            // 加载日记的标签
            scope.launch {
                // 直接从数据库加载日记的标签
                val tags = viewModel.getTagsForDiary(it.id)
                selectedTags = tags
                // 同时更新selectedTagIds
                viewModel.selectedTagIds = tags.map { tag -> tag.id }
            }
        }
    }

    // 监听设置变化，实现默认颜色设置的即时生效
    LaunchedEffect(settings?.defaultDiaryColor) {
        settings?.defaultDiaryColor?.let { defaultColor ->
            viewModel.updateDefaultColor(defaultColor)
        }
    }

    // 监听错误状态，避免瞬间闪现
    LaunchedEffect(error) {
        error?.let {
            // 只有当错误持续存在时才显示
            delay(300) // 短暂延迟避免闪现
            if (error != null) { // 再次检查错误是否仍然存在
                scope.launch {
                    snackbarHostState.showSnackbar(it)
                }
            }
        }
    }

    // 标签选择对话框
    if (showTagDialog) {
        AlertDialog(
            onDismissRequest = { showTagDialog = false },
            title = { Text("选择标签") },
            text = {
                Column {
                    // 新建标签输入框
                    OutlinedTextField(
                        value = newTagName,
                        onValueChange = { newTagName = it },
                        label = { Text("新建标签") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 已有标签列表
                    Text(
                        text = "已有标签",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (allTags.isEmpty()) {
                        Text(
                            text = "暂无标签，请先创建标签",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            items(allTags) { tag ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            // 切换标签选择状态
                                            if (selectedTags.any { it.id == tag.id }) {
                                                selectedTags = selectedTags.filter { it.id != tag.id }
                                            } else {
                                                selectedTags = selectedTags + tag
                                            }
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedTags.any { it.id == tag.id },
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                selectedTags = selectedTags + tag
                                            } else {
                                                selectedTags = selectedTags.filter { it.id != tag.id }
                                            }
                                        }
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(tag.color)
                                        )
                                    ) {
                                        Text(
                                            text = tag.name,
                                            modifier = Modifier.padding(8.dp),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 保存所有选择的标签
                        viewModel.selectedTagIds = selectedTags.map { it.id }
                        
                        // 创建新标签
                        if (newTagName.isNotBlank()) {
                            scope.launch {
                                val newTag = viewModel.createTag(newTagName, newTagColor)
                                if (newTag != null) {
                                    selectedTags = selectedTags + newTag
                                    viewModel.selectedTagIds = selectedTags.map { it.id }
                                    newTagName = ""
                                }
                                showTagDialog = false
                            }
                        } else {
                            showTagDialog = false
                        }
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTagDialog = false }
                ) {
                    Text("关闭")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (diaryId == 0L) stringResource(R.string.new_diary) else stringResource(R.string.diary_detail)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (diaryId == 0L) {
                        // 新建日记时的保存按钮
                        IconButton(onClick = {
                            scope.launch {
                                try {
                                    // 设置选中的标签ID列表
                                    viewModel.selectedTagIds = selectedTags.map { it.id }
                                    viewModel.saveDiary()
                                    navController.navigateUp()
                                } catch (e: Exception) {
                                    // 处理保存失败的情况，错误信息已经在viewModel中设置
                                    // Snackbar会通过LaunchedEffect自动显示错误信息
                                }
                            }
                        }) {
                            Icon(Icons.Filled.Save, contentDescription = stringResource(R.string.save))
                        }
                    } else if (diaryId != 0L && !viewModel.isEditing) {
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
                    } else if (diaryId != 0L && viewModel.isEditing) {
                        // 编辑模式下的保存按钮（仅用于已有日记）
                        IconButton(onClick = {
                            scope.launch {
                                try {
                                    // 设置选中的标签ID列表
                                    viewModel.selectedTagIds = selectedTags.map { it.id }
                                    viewModel.saveDiary()
                                    viewModel.isEditing = false
                                } catch (e: Exception) {
                                    // 处理保存失败的情况，错误信息已经在viewModel中设置
                                    // Snackbar会通过LaunchedEffect自动显示错误信息
                                }
                            }
                        }) {
                            Icon(Icons.Filled.Save, contentDescription = stringResource(R.string.save))
                        }
                    }
                }
            )
        },

        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = error ?: stringResource(R.string.error_occurred),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Button(
                        onClick = { viewModel.reloadDiary() }
                    ) {
                        Text("重试")
                    }
                    if (diaryId != 0L) {
                        Button(
                            onClick = { navController.navigateUp() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("返回")
                        }
                    }
                }
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
                        onImagePathsChange = { viewModel.imagePaths = it },
                        selectedTags = selectedTags,
                        onSelectedTagsChange = { selectedTags = it },
                        allTags = allTags,
                        onShowTagDialogChange = { showTagDialog = it },
                        viewModel = viewModel,
                        color = viewModel.color,
                        onColorChange = { viewModel.color = it }
                    )
                } else {
                    // 查看模式
                    val currentDiary = diary
                    if (currentDiary != null) {
                        ViewableContent(currentDiary, selectedTags)
                    } else {
                        // 日记不存在或加载失败的空状态
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "日记不存在或已被删除",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "请返回列表页面重试",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}