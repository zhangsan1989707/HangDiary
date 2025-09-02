package com.example.hangdiary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 待办事项实体类
 * 表示用户的待办任务
 */
@Entity(tableName = "todos")
data class Todo(
    /** 待办ID，主键，自动生成 */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 待办标题 */
    val title: String,

    /** 待办内容描述 */
    val content: String? = null,

    /** 是否已完成 */
    val isCompleted: Boolean = false,

    /** 创建时间 */
    val createdAt: LocalDateTime,

    /** 最后更新时间 */
    val updatedAt: LocalDateTime? = null,

    /** 截止日期 */
    val dueDate: LocalDateTime? = null
)