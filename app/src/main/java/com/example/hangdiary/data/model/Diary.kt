package com.example.hangdiary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 日记实体类
 * 表示用户创建的每一篇日记
 */
@Entity(tableName = "diaries")
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

    /** 分类ID，外键关联Category表 */
    val categoryId: Long? = null,

    /** 心情标签 */
    val mood: String? = null,

    /** 天气标签 */
    val weather: String? = null,

    /** 位置信息 */
    val location: String? = null,

    /** 是否收藏 */
    val isFavorite: Boolean = false,

    /** 是否顶置 */
    val isPinned: Boolean = false,

    /** 图片路径列表 */
    val imagePaths: List<String> = emptyList()
)