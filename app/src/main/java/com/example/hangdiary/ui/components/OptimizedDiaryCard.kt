package com.example.hangdiary.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.model.DiaryWithTags
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.ui.theme.DiaryDesignSystem
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 优化的日记卡片组件
 * 提供流畅的动画效果和更好的性能
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OptimizedDiaryCard(
    diaryWithTags: DiaryWithTags,
    isSelected: Boolean = false,
    isMultiSelectMode: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 使用key优化重组性能
    key(diaryWithTags.diary.id, diaryWithTags.diary.updatedAt) {
        AnimatedDiaryCard(
            diary = diaryWithTags.diary,
            tags = diaryWithTags.tags,
            isSelected = isSelected,
            isMultiSelectMode = isMultiSelectMode,
            onClick = onClick,
            onLongClick = onLongClick,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AnimatedDiaryCard(
    diary: Diary,
    tags: List<Tag>,
    isSelected: Boolean,
    isMultiSelectMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 动画状态
    val scale by animateFloatAsState(
        targetValue = when {
            isSelected -> 0.95f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )
    
    val elevation by animateDpAsState(
        targetValue = when {
            diary.isPinned -> DiaryDesignSystem.Shadows.large
            isSelected -> DiaryDesignSystem.Shadows.large
            else -> DiaryDesignSystem.Shadows.medium
        },
        animationSpec = tween(DiaryDesignSystem.Animations.durationMedium),
        label = "card_elevation"
    )
    
    // 卡片颜色动画
    val cardColor by animateColorAsState(
        targetValue = when {
            diary.isPinned -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            !diary.color.isNullOrEmpty() -> DiaryDesignSystem.Colors.getDiaryColor(diary.color)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(DiaryDesignSystem.Animations.durationMedium),
        label = "card_color"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = DiaryDesignSystem.Shapes.card,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DiaryDesignSystem.Dimensions.spacingL)
        ) {
            // 标题行
            DiaryCardHeader(
                diary = diary,
                isSelected = isSelected,
                isMultiSelectMode = isMultiSelectMode
            )
            
            Spacer(modifier = Modifier.height(DiaryDesignSystem.Dimensions.spacingS))
            
            // 日期
            DiaryCardDate(diary = diary)
            
            Spacer(modifier = Modifier.height(DiaryDesignSystem.Dimensions.spacingM))
            
            // 内容预览
            DiaryCardContent(diary = diary)
            
            if (tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(DiaryDesignSystem.Dimensions.spacingM))
                
                // 标签
                DiaryCardTags(tags = tags)
            }
        }
    }
}

@Composable
private fun DiaryCardHeader(
    diary: Diary,
    isSelected: Boolean,
    isMultiSelectMode: Boolean
) {
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
            horizontalArrangement = Arrangement.spacedBy(DiaryDesignSystem.Dimensions.spacingS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 多选模式复选框
            AnimatedVisibility(
                visible = isMultiSelectMode,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            
            // 顶置图标
            AnimatedVisibility(
                visible = diary.isPinned,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(DiaryDesignSystem.Dimensions.iconSizeL + 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "已顶置",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(DiaryDesignSystem.Dimensions.iconSizeM)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DiaryCardDate(diary: Diary) {
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("M月d日 EEEE HH:mm", Locale.CHINA)
    }
    
    Text(
        text = diary.createdAt.format(dateFormatter),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = DiaryDesignSystem.Alpha.medium)
    )
}

@Composable
private fun DiaryCardContent(diary: Diary) {
    val previewText = remember(diary.content) {
        if (diary.content.length > 100) {
            diary.content.take(100) + "..."
        } else {
            diary.content
        }
    }
    
    Text(
        text = previewText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = DiaryDesignSystem.Alpha.high),
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun DiaryCardTags(tags: List<Tag>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(DiaryDesignSystem.Dimensions.spacingXS),
        modifier = Modifier.fillMaxWidth()
    ) {
        // 最多显示3个标签
        tags.take(3).forEach { tag ->
            DiaryTag(tag = tag)
        }
        
        // 如果有更多标签，显示数量
        if (tags.size > 3) {
            Surface(
                shape = DiaryDesignSystem.Shapes.chip,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "+${tags.size - 3}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = DiaryDesignSystem.Alpha.medium),
                    modifier = Modifier.padding(
                        horizontal = DiaryDesignSystem.Dimensions.spacingS,
                        vertical = DiaryDesignSystem.Dimensions.spacingXXS
                    )
                )
            }
        }
    }
}

@Composable
private fun DiaryTag(tag: Tag) {
    Surface(
        shape = DiaryDesignSystem.Shapes.chip,
        color = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Text(
            text = tag.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(
                horizontal = DiaryDesignSystem.Dimensions.spacingS,
                vertical = DiaryDesignSystem.Dimensions.spacingXXS
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}