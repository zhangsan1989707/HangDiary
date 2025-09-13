package com.example.hangdiary.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hangdiary.data.model.DiaryWithTags
import com.example.hangdiary.ui.common.UiState
import com.example.hangdiary.ui.theme.DiaryDesignSystem

/**
 * 优化的日记列表组件
 * 支持列表和网格两种视图模式，具有良好的性能表现
 */
@Composable
fun OptimizedDiaryList(
    uiState: UiState<List<DiaryWithTags>>,
    viewMode: ViewMode = ViewMode.LIST,
    selectedDiaries: Set<Long> = emptySet(),
    isMultiSelectMode: Boolean = false,
    onDiaryClick: (DiaryWithTags) -> Unit,
    onDiaryLongClick: (DiaryWithTags) -> Unit,
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    gridState: LazyGridState = rememberLazyGridState()
) {
    when (uiState) {
        is UiState.Loading -> {
            LoadingContent(modifier = modifier)
        }
        is UiState.Error -> {
            ErrorContent(
                message = uiState.message,
                onRetry = onRefresh,
                modifier = modifier
            )
        }
        is UiState.Empty -> {
            EmptyContent(
                message = uiState.message,
                modifier = modifier
            )
        }
        is UiState.Success -> {
            DiaryListContent(
                diaries = uiState.data,
                viewMode = viewMode,
                selectedDiaries = selectedDiaries,
                isMultiSelectMode = isMultiSelectMode,
                onDiaryClick = onDiaryClick,
                onDiaryLongClick = onDiaryLongClick,
                modifier = modifier,
                listState = listState,
                gridState = gridState
            )
        }
        is UiState.Refreshing -> {
            // 显示数据，同时显示刷新指示器
            Box(modifier = modifier) {
                DiaryListContent(
                    diaries = uiState.data,
                    viewMode = viewMode,
                    selectedDiaries = selectedDiaries,
                    isMultiSelectMode = isMultiSelectMode,
                    onDiaryClick = onDiaryClick,
                    onDiaryLongClick = onDiaryLongClick,
                    listState = listState,
                    gridState = gridState
                )
                
                // 顶部刷新指示器
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun DiaryListContent(
    diaries: List<DiaryWithTags>,
    viewMode: ViewMode,
    selectedDiaries: Set<Long>,
    isMultiSelectMode: Boolean,
    onDiaryClick: (DiaryWithTags) -> Unit,
    onDiaryLongClick: (DiaryWithTags) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    gridState: LazyGridState = rememberLazyGridState()
) {
    when (viewMode) {
        ViewMode.LIST -> {
            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(DiaryDesignSystem.Dimensions.spacingL),
                verticalArrangement = Arrangement.spacedBy(DiaryDesignSystem.Dimensions.spacingM)
            ) {
                items(
                    items = diaries,
                    key = { it.diary.id }
                ) { diaryWithTags ->
                    OptimizedDiaryCard(
                        diaryWithTags = diaryWithTags,
                        isSelected = selectedDiaries.contains(diaryWithTags.diary.id),
                        isMultiSelectMode = isMultiSelectMode,
                        onClick = { onDiaryClick(diaryWithTags) },
                        onLongClick = { onDiaryLongClick(diaryWithTags) }
                    )
                }
            }
        }
        ViewMode.GRID -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                state = gridState,
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(DiaryDesignSystem.Dimensions.spacingL),
                verticalArrangement = Arrangement.spacedBy(DiaryDesignSystem.Dimensions.spacingM),
                horizontalArrangement = Arrangement.spacedBy(DiaryDesignSystem.Dimensions.spacingM)
            ) {
                items(
                    items = diaries,
                    key = { it.diary.id }
                ) { diaryWithTags ->
                    OptimizedDiaryCard(
                        diaryWithTags = diaryWithTags,
                        isSelected = selectedDiaries.contains(diaryWithTags.diary.id),
                        isMultiSelectMode = isMultiSelectMode,
                        onClick = { onDiaryClick(diaryWithTags) },
                        onLongClick = { onDiaryLongClick(diaryWithTags) },
                        modifier = Modifier.aspectRatio(0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DiaryDesignSystem.Dimensions.spacingL)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "加载中...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = DiaryDesignSystem.Alpha.medium)
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DiaryDesignSystem.Dimensions.spacingL)
        ) {
            Text(
                text = "😔",
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = DiaryDesignSystem.Alpha.high)
            )
            Button(
                onClick = onRetry,
                shape = DiaryDesignSystem.Shapes.button
            ) {
                Text("重试")
            }
        }
    }
}

@Composable
private fun EmptyContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DiaryDesignSystem.Dimensions.spacingL)
        ) {
            Text(
                text = "📝",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = DiaryDesignSystem.Alpha.medium)
            )
            Text(
                text = "点击右下角按钮开始记录",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = DiaryDesignSystem.Alpha.inactive)
            )
        }
    }
}

/**
 * 视图模式枚举
 */
enum class ViewMode {
    LIST,   // 列表视图
    GRID    // 网格视图
}