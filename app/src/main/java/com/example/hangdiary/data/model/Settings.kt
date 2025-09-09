package com.example.hangdiary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 设置实体类
 * 存储用户的应用设置
 */
@Entity(tableName = "settings")
data class Settings(
    /** 设置ID，主键 */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 视图设置 */
    val viewMode: String = "list",



    /** 是否启用暗黑模式 */
    val isDarkMode: Boolean = false,

    /** 是否启用卡片视图 */
    val isCardView: Boolean = true,



    /** 默认日记颜色 */
    val defaultDiaryColor: String? = null
) {
    companion object {
        /**
         * 获取默认设置
         * @return 默认设置对象
         */
        fun getDefaultSettings(): Settings {
            return Settings(
            viewMode = "list",
            isDarkMode = false,
                isCardView = true,
                defaultDiaryColor = null
            )
        }
    }
}