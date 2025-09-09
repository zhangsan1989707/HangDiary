package com.example.hangdiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.ui.navigation.Screen

/**
 * 导航抽屉组件
 * 提供应用的主要导航功能
 */
@Composable
fun NavigationDrawer(
    tags: List<Tag>,
    selectedTags: List<Long>,
    onTagSelected: (Long) -> Unit,
    onTagsCleared: () -> Unit,
    onTodoClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onCloseDrawer: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
    ) {
        // 抽屉头部
        DrawerHeader(onCloseDrawer)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 全部日记
            item {
                DrawerItem(
                    icon = Icons.Default.Home,
                    title = "全部日记",
                    isSelected = selectedTags.isEmpty(),
                    onClick = {
                        onTagsCleared()
                    }
                )
            }

            // 待办事项
            item {
                DrawerItem(
                    icon = Icons.Default.CheckCircle,
                    title = "待办事项",
                    isSelected = false,
                    onClick = {
                        onTodoClick()
                        onCloseDrawer()
                    }
                )
            }

            // 标签管理
            item {
                DrawerItem(
                    icon = Icons.AutoMirrored.Filled.Label,
                    title = "标签管理",
                    isSelected = false,
                    onClick = {
                        navController.navigate(Screen.TagManagement.route)
                        onCloseDrawer()
                    }
                )
            }



            // 标签列表（不显示标签标题）
            items(tags) { tag ->
                TagItem(
                    tag = tag,
                    isSelected = selectedTags.contains(tag.id),
                    onClick = {
                        onTagSelected(tag.id)
                    }
                )
            }

            // 设置和关于
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                DrawerItem(
                    icon = Icons.Default.Settings,
                    title = "设置",
                    isSelected = false,
                    onClick = {
                        onSettingsClick()
                        onCloseDrawer()
                    }
                )
            }

            item {
                DrawerItem(
                    icon = Icons.Default.Info,
                    title = "关于",
                    isSelected = false,
                    onClick = {
                        onAboutClick()
                        onCloseDrawer()
                    }
                )
            }
        }
    }
}

@Composable
private fun DrawerHeader(onCloseDrawer: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = "日记图标",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "LeoHangの日记",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "记录生活的点滴~",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        IconButton(
            onClick = onCloseDrawer,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "关闭",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun TagItem(
    tag: Tag,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        Color(tag.color).copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    val contentColor = if (isSelected) {
        Color(tag.color)
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color(tag.color), MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = tag.name,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}