package com.example.hangdiary.data.repository

import com.example.hangdiary.data.dao.DiaryDao
import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.model.DiaryWithTags
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.data.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 日记仓库
 * 负责协调对日记数据的访问
 */
@Singleton
class DiaryRepository @Inject constructor(
    private val diaryDao: DiaryDao,
    private val tagRepository: TagRepository
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
     * 获取所有带标签的日记
     * @return 带标签的日记列表Flow
     */
    fun getAllDiariesWithTags(): Flow<List<DiaryWithTags>> {
        return getAllDiaries().map { diaries ->
            diaries.map { diary ->
                // We'll return DiaryWithTags with empty tags for now
                // The actual tags will be loaded in the UseCase layer
                DiaryWithTags(diary, emptyList())
            }
        }
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
     * 更新日记的顶置状态
     * @param id 日记ID
     * @param isPinned 是否顶置
     * @return 更新的行数
     */
    suspend fun updatePinnedStatus(id: Long, isPinned: Boolean): Int {
        return diaryDao.updatePinnedStatus(id, isPinned)
    }

    /**
     * 更新日记的颜色
     * @param id 日记ID
     * @param color 日记颜色
     * @return 更新的行数
     */
    suspend fun updateDiaryColor(id: Long, color: String?): Int {
        return diaryDao.updateDiaryColor(id, color)
    }

    /**
     * 批量更新日记的颜色
     * @param ids 日记ID列表
     * @param color 日记颜色
     * @return 更新的行数
     */
    suspend fun updateDiariesColor(ids: List<Long>, color: String?): Int {
        return diaryDao.updateDiariesColor(ids, color)
    }

    /**
     * 批量删除日记
     * @param ids 日记ID列表
     * @return 删除的行数
     */
    suspend fun deleteDiariesByIds(ids: List<Long>): Int {
        return diaryDao.deleteDiariesByIds(ids)
    }

    /**
     * 获取置顶的日记
     * @return 置顶的日记列表Flow
     */
    fun getPinnedDiaries(): Flow<List<Diary>> {
        return diaryDao.getPinnedDiaries()
    }

}