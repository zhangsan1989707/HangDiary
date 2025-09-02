package com.example.hangdiary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 标签实体类
 * 用于给日记和待办事项打标签
 */
@Entity(tableName = "tags")
data class Tag(
    /** 标签ID，主键，自动生成 */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 标签名称 */
    val name: String,

    /** 标签颜色（ARGB格式） */
    val color: Int,

    /** 创建时间 */
    val createdAt: LocalDateTime
)