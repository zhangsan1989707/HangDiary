package com.example.hangdiary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDateTime

/**
 * 日记实体类
 * 表示用户创建的每一篇日记
 */
@Entity(tableName = "diaries")
@TypeConverters(com.example.hangdiary.data.util.StringListConverter::class)
data class Diary(
    /** 日记ID，主键，自动生成 */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 日记标题 */
    val title: String,

    /** 日记内容 */
    val content: String,

    /** 日记创建时间 */
    val createdAt: LocalDateTime,

    /** 日记最后修改时间 */
    val updatedAt: LocalDateTime?,



    /** 是否顶置 */
    val isPinned: Boolean = false,

    /** 图片路径列表 */
    val imagePaths: List<String> = emptyList(),

    /** 日记卡片颜色 */
    val color: String? = null
)