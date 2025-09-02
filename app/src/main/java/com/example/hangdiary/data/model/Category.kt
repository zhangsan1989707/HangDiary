package com.example.hangdiary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 分类实体类
 * 用于对日记进行分类管理
 */
@Entity(tableName = "categories")
data class Category(
    /** 分类ID，主键，自动生成 */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 分类名称 */
    val name: String,

    /** 分类图标资源ID */
    val iconResId: Int? = null,

    /** 分类颜色 */
    val color: Int? = null
)