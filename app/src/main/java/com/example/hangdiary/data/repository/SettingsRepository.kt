package com.example.hangdiary.data.repository

import com.example.hangdiary.data.dao.SettingsDao
import com.example.hangdiary.data.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 设置仓库
 * 负责协调对设置数据的访问
 */
@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao
) {
    /**
     * 插入设置
     * @param settings 要插入的设置
     * @return 插入后的设置ID
     */
    suspend fun insert(settings: Settings): Long {
        return settingsDao.insert(settings)
    }

    /**
     * 更新设置
     * @param settings 要更新的设置
     * @return 更新的行数
     */
    suspend fun update(settings: Settings): Int {
        return settingsDao.update(settings)
    }

    /**
     * 删除设置
     * @param settings 要删除的设置
     * @return 删除的行数
     */
    suspend fun delete(settings: Settings): Int {
        return settingsDao.delete(settings)
    }

    /**
     * 根据ID获取设置
     * @param id 设置ID
     * @return 设置对象，若不存在则为null
     */
    suspend fun getSettingsById(id: Long): Settings? {
        return settingsDao.getById(id)
    }

    /**
     * 获取所有设置
     * @return 设置列表的Flow
     */
    fun getAllSettings(): Flow<List<Settings>> {
        return settingsDao.getAllSettings()
    }

    /**
     * 获取第一个设置（通常只有一个设置记录）
     * @return 设置对象的Flow
     */
    fun getFirstSettings(): Flow<Settings?> {
        return settingsDao.getFirstSettings()
    }

    /**
     * 获取或创建默认设置
     * @return 设置对象
     */
    suspend fun getOrCreateDefaultSettings(): Settings {
        // 获取第一个设置，如果没有则创建默认设置
        val firstSettings = settingsDao.getFirstSettings().first()
        return firstSettings ?: settingsDao.insert(Settings.getDefaultSettings()).let {
            settingsDao.getById(it) ?: Settings.getDefaultSettings()
        }
    }

    /**
     * 更新视图设置
     * @param id 设置ID
     * @param viewMode 视图设置
     * @return 更新的行数
     */
    suspend fun updateViewMode(id: Long, viewMode: String): Int {
        return settingsDao.updateViewMode(id, viewMode)
    }



    /**
     * 更新暗黑模式设置
     * @param id 设置ID
     * @param isDarkMode 是否启用暗黑模式
     * @return 更新的行数
     */
    suspend fun updateDarkMode(id: Long, isDarkMode: Boolean): Int {
        return settingsDao.updateDarkMode(id, isDarkMode)
    }

    /**
     * 更新卡片视图设置
     * @param id 设置ID
     * @param isCardView 是否启用卡片视图
     * @return 更新的行数
     */
    suspend fun updateCardView(id: Long, isCardView: Boolean): Int {
        return settingsDao.updateCardView(id, isCardView)
    }















    /**
     * 更新默认日记颜色设置
     * @param id 设置ID
     * @param defaultDiaryColor 默认日记颜色
     * @return 更新的行数
     */
    suspend fun updateDefaultDiaryColor(id: Long, defaultDiaryColor: String?): Int {
        return settingsDao.updateDefaultDiaryColor(id, defaultDiaryColor)
    }
}