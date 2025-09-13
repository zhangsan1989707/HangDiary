package com.example.hangdiary.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * HangDiary 设计系统
 * 统一管理应用的视觉设计规范
 */
object DiaryDesignSystem {
    
    /**
     * 颜色系统
     */
    object Colors {
        // 日记卡片颜色映射
        val diaryColorMap = mapOf(
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
        
        // 优先级颜色
        val priorityColors = mapOf(
            1 to Color(0xFF9E9E9E), // 低优先级 - 灰色
            2 to Color(0xFFFF9800), // 中优先级 - 橙色
            3 to Color(0xFFF44336)  // 高优先级 - 红色
        )
        
        // 状态颜色
        val statusColors = mapOf(
            "completed" to Color(0xFF4CAF50),    // 已完成 - 绿色
            "pending" to Color(0xFF2196F3),      // 待处理 - 蓝色
            "overdue" to Color(0xFFF44336),      // 逾期 - 红色
            "today" to Color(0xFFFF9800)         // 今日 - 橙色
        )
        
        /**
         * 获取日记卡片颜色
         */
        @Composable
        fun getDiaryColor(colorName: String?): Color {
            return diaryColorMap[colorName] ?: MaterialTheme.colorScheme.surface
        }
        
        /**
         * 获取优先级颜色
         */
        @Composable
        fun getPriorityColor(priority: Int): Color {
            return priorityColors[priority] ?: MaterialTheme.colorScheme.onSurface
        }
        
        /**
         * 获取状态颜色
         */
        @Composable
        fun getStatusColor(status: String): Color {
            return statusColors[status] ?: MaterialTheme.colorScheme.primary
        }
    }
    
    /**
     * 尺寸系统
     */
    object Dimensions {
        // 间距
        val spacingXXS: Dp = 2.dp
        val spacingXS: Dp = 4.dp
        val spacingS: Dp = 8.dp
        val spacingM: Dp = 12.dp
        val spacingL: Dp = 16.dp
        val spacingXL: Dp = 20.dp
        val spacingXXL: Dp = 24.dp
        val spacingXXXL: Dp = 32.dp
        
        // 卡片
        val cardCornerRadius: Dp = 12.dp
        val cardElevation: Dp = 4.dp
        val cardElevationPressed: Dp = 8.dp
        val cardElevationSelected: Dp = 8.dp
        
        // 按钮
        val buttonHeight: Dp = 48.dp
        val buttonCornerRadius: Dp = 24.dp
        val smallButtonHeight: Dp = 36.dp
        val smallButtonCornerRadius: Dp = 18.dp
        
        // 输入框
        val textFieldCornerRadius: Dp = 16.dp
        val textFieldMinHeight: Dp = 56.dp
        
        // 图标
        val iconSizeS: Dp = 16.dp
        val iconSizeM: Dp = 20.dp
        val iconSizeL: Dp = 24.dp
        val iconSizeXL: Dp = 32.dp
        
        // 头像
        val avatarSizeS: Dp = 32.dp
        val avatarSizeM: Dp = 40.dp
        val avatarSizeL: Dp = 56.dp
        
        // 分割线
        val dividerThickness: Dp = 1.dp
        
        // 最小触摸目标
        val minTouchTarget: Dp = 48.dp
    }
    
    /**
     * 形状系统
     */
    object Shapes {
        val small = RoundedCornerShape(4.dp)
        val medium = RoundedCornerShape(8.dp)
        val large = RoundedCornerShape(12.dp)
        val extraLarge = RoundedCornerShape(16.dp)
        
        // 特殊形状
        val card = RoundedCornerShape(Dimensions.cardCornerRadius)
        val button = RoundedCornerShape(Dimensions.buttonCornerRadius)
        val textField = RoundedCornerShape(Dimensions.textFieldCornerRadius)
        val chip = RoundedCornerShape(16.dp)
        val fab = RoundedCornerShape(16.dp)
    }
    
    /**
     * 动画系统
     */
    object Animations {
        // 动画时长
        const val durationShort = 150
        const val durationMedium = 300
        const val durationLong = 500
        
        // 动画延迟
        const val delayShort = 50
        const val delayMedium = 100
        const val delayLong = 200
        
        // 缓动函数
        const val easeInOut = "cubic-bezier(0.4, 0.0, 0.2, 1.0)"
        const val easeOut = "cubic-bezier(0.0, 0.0, 0.2, 1.0)"
        const val easeIn = "cubic-bezier(0.4, 0.0, 1.0, 1.0)"
    }
    
    /**
     * 阴影系统
     */
    object Shadows {
        val none = 0.dp
        val small = 2.dp
        val medium = 4.dp
        val large = 8.dp
        val extraLarge = 16.dp
    }
    
    /**
     * 透明度系统
     */
    object Alpha {
        const val disabled = 0.38f
        const val inactive = 0.60f
        const val medium = 0.74f
        const val high = 0.87f
        const val full = 1.0f
    }
}