package com.example.hangdiary.data.repository

import com.example.hangdiary.data.dao.DiaryDao
import com.example.hangdiary.data.model.Diary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 日记仓库
 * 负责协调对日记数据的访问
 */
@Singleton
class DiaryRepository @Inject constructor(
    private val diaryDao: DiaryDao
) {
    /**
     * 插入日记
     * @param diary 要插入的日记
     * @return 插入后的日记ID
     */
    suspend fun insert(diary: Diary): Long {
        return diaryDao.insert(diary)
    }

    /**
     * 更新日记
     * @param diary 要更新的日记
     * @return 更新的行数
     */
    suspend fun update(diary: Diary): Int {
        return diaryDao.update(diary)
    }

    /**
     * 删除日记
     * @param diary 要删除的日记
     * @return 删除的行数
     */
    suspend fun delete(diary: Diary): Int {
        return diaryDao.delete(diary)
    }

    /**
     * 根据ID获取日记
     * @param id 日记ID
     * @return 日记对象，若不存在则为null
     */
    suspend fun getDiaryById(id: Long): Diary? {
        return diaryDao.getById(id)
    }

    /**
     * 获取所有日记
     * @return 日记列表的Flow
     */
    fun getAllDiaries(): Flow<List<Diary>> {
        return diaryDao.getAllDiaries()
    }

    /**
     * 根据分类获取日记
     * @param categoryId 分类ID
     * @return 该分类下的日记列表Flow
     */
    fun getDiariesByCategory(categoryId: Long): Flow<List<Diary>> {
        return diaryDao.getDiariesByCategory(categoryId)
    }

    /**
     * 搜索日记
     * @param keyword 搜索关键词
     * @return 匹配的日记列表Flow
     */
    fun searchDiaries(keyword: String): Flow<List<Diary>> {
        return diaryDao.searchDiaries(keyword)
    }

    /**
     * 获取收藏的日记
     * @return 收藏的日记列表Flow
     */
    fun getFavoriteDiaries(): Flow<List<Diary>> {
        return diaryDao.getFavoriteDiaries()
    }

    /**
     * 根据日期范围获取日记
     * @param start 开始日期
     * @param end 结束日期
     * @return 该日期范围内的日记列表Flow
     */
    fun getDiariesByDateRange(start: LocalDateTime, end: LocalDateTime): Flow<List<Diary>> {
        return diaryDao.getDiariesByDateRange(start, end)
    }

    /**
     * 根据标签获取日记
     * @param tagIds 标签ID列表
     * @return 包含指定标签的日记列表Flow
     */
    fun getDiariesByTags(tagIds: List<Long>): Flow<List<Diary>> {
        return diaryDao.getDiariesByTags(tagIds, tagIds.size)
    }

    /**
     * 更新日记的收藏状态
     * @param id 日记ID
     * @param isFavorite 是否收藏
     * @return 更新的行数
     */
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean): Int {
        return diaryDao.updateFavoriteStatus(id, isFavorite)
    }

    /**
     * 更新日记的顶置状态
     * @param id 日记ID
     * @param isPinned 是否顶置
     * @return 更新的行数
     */
    suspend fun updatePinnedStatus(id: Long, isPinned: Boolean): Int {
        return diaryDao.updatePinnedStatus(id, isPinned)
    }

    /**
     * 获取置顶的日记
     * @return 置顶的日记列表Flow
     */
    fun getPinnedDiaries(): Flow<List<Diary>> {
        return diaryDao.getPinnedDiaries()
    }

}