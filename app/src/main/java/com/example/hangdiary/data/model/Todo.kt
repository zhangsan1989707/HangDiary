package com.example.hangdiary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
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

    /** 待办内容描述/备注 */
    val notes: String? = null,

    /** 是否已完成 */
    val isCompleted: Boolean = false,

    /** 创建时间 */
    val createdAt: LocalDateTime,

    /** 最后更新时间 */
    val updatedAt: LocalDateTime? = null,

    /** 截止日期 */
    val dueDate: LocalDate? = null,

    /** 截止时间 */
    val dueTime: LocalTime? = null,

    /** 分类/类别 */
    val category: String? = null,

    /** 优先级 (1-低, 2-中, 3-高) */
    val priority: Int = 1
) {
    // 兼容性属性，保持向后兼容
    val content: String?
        get() = notes
}