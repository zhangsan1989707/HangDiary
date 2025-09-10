package com.example.hangdiary.data

import android.content.Context
import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.repository.DiaryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 数据库初始化器
 * 负责在应用首次启动时插入示例数据
 */
@Singleton
class DatabaseInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val diaryRepository: DiaryRepository
) {
    
    /**
     * 初始化数据库，插入示例数据
     */
    fun initialize() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 检查是否已有日记数据
                val existingDiaries = diaryRepository.getAllDiaries().collect { diaries ->
                    if (diaries.isEmpty()) {
                        insertSampleData()
                    }
                }
            } catch (e: Exception) {
                // 如果出错，静默处理，不影响应用启动
            }
        }
    }
    
    /**
     * 插入示例日记数据
     */
    private suspend fun insertSampleData() {
        val sampleDiaries = listOf(
            Diary(
                title = "欢迎使用HangDiary",
                content = "这是您的第一条日记！\n\nHangDiary是一个简洁优雅的日记应用，支持：\n• 创建和管理日记\n• 添加标签分类\n• 设置提醒事项\n• 多种视图模式\n• 深色/浅色主题\n\n开始记录您的美好生活吧！",
                color = "#FF6B6B",
                isPinned = true,
                createdAt = LocalDateTime.now().minusDays(1),
                updatedAt = LocalDateTime.now().minusDays(1)
            ),
            Diary(
                title = "今日感悟",
                content = "今天是一个美好的日子，阳光温暖，心情也很好。\n\n记录生活中的点点滴滴，让每一天都变得有意义。",
                color = "#4ECDC4",
                isPinned = false,
                createdAt = LocalDateTime.now().minusHours(6),
                updatedAt = LocalDateTime.now().minusHours(6)
            ),
            Diary(
                title = "工作记录",
                content = "今天完成了项目的重要功能：\n\n1. 修复了用户反馈的bug\n2. 优化了界面响应速度\n3. 添加了新的数据统计功能\n\n明天继续加油！",
                color = "#45B7D1",
                isPinned = false,
                createdAt = LocalDateTime.now().minusHours(2),
                updatedAt = LocalDateTime.now().minusHours(2)
            )
        )
        
        sampleDiaries.forEach { diary ->
            diaryRepository.insert(diary)
        }
    }
}