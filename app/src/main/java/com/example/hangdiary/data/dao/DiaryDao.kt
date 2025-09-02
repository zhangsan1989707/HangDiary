package com.example.hangdiary.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hangdiary.data.model.Diary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * 日记数据访问对象
 * 定义对日记表的数据库操作
 */
@Dao
interface DiaryDao {
    /**
     * 插入日记
     * @param diary 要插入的日记对象
     * @return 插入后的日记ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diary: Diary): Long

    /**
     * 更新日记
     * @param diary 要更新的日记对象
     * @return 更新的行数
     */
    @Update
    suspend fun update(diary: Diary): Int

    /**
     * 删除日记
     * @param diary 要删除的日记对象
     * @return 删除的行数
     */
    @Delete
    suspend fun delete(diary: Diary): Int

    /**
     * 根据ID查询日记
     * @param id 日记ID
     * @return 对应的日记对象，若不存在则为null
     */
    @Query("SELECT * FROM diaries WHERE id = :id")
    suspend fun getById(id: Long): Diary?

    /**
     * 查询所有日记
     * 按顶置状态降序，创建时间降序排序
     * @return 日记列表的Flow
     */
    @Query("SELECT * FROM diaries ORDER BY isPinned DESC, createdAt DESC")
    fun getAllDiaries(): Flow<List<Diary>>

    /**
     * 根据分类ID查询日记
     * 按顶置状态降序，创建时间降序排序
     * @param categoryId 分类ID
     * @return 该分类下的日记列表Flow
     */
    @Query("SELECT * FROM diaries WHERE categoryId = :categoryId ORDER BY isPinned DESC, createdAt DESC")
    fun getDiariesByCategory(categoryId: Long): Flow<List<Diary>>

    /**
     * 根据关键词搜索日记
     * 按顶置状态降序，创建时间降序排序
     * @param keyword 搜索关键词
     * @return 匹配的日记列表Flow
     */
    @Query("SELECT * FROM diaries WHERE title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%' ORDER BY isPinned DESC, createdAt DESC")
    fun searchDiaries(keyword: String): Flow<List<Diary>>

    /**
     * 查询收藏的日记
     * 按顶置状态降序，创建时间降序排序
     * @return 收藏的日记列表Flow
     */
    @Query("SELECT * FROM diaries WHERE isFavorite = 1 ORDER BY isPinned DESC, createdAt DESC")
    fun getFavoriteDiaries(): Flow<List<Diary>>

    /**
     * 根据日期范围查询日记
     * 按顶置状态降序，创建时间降序排序
     * @param start 开始日期
     * @param end 结束日期
     * @return 该日期范围内的日记列表Flow
     */
    @Query("SELECT * FROM diaries WHERE createdAt BETWEEN :start AND :end ORDER BY isPinned DESC, createdAt DESC")
    fun getDiariesByDateRange(start: LocalDateTime, end: LocalDateTime): Flow<List<Diary>>

    /**
     * 更新日记的收藏状态
     * @param id 日记ID
     * @param isFavorite 是否收藏
     * @return 更新的行数
     */
    @Query("UPDATE diaries SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean): Int

    /**
     * 更新日记的顶置状态
     * @param id 日记ID
     * @param isPinned 是否顶置
     * @return 更新的行数
     */
    @Query("UPDATE diaries SET isPinned = :isPinned WHERE id = :id")
    suspend fun updatePinnedStatus(id: Long, isPinned: Boolean): Int

    /**
     * 查询置顶的日记
     * 按创建时间降序排序
     * @return 置顶的日记列表Flow
     */
    @Query("SELECT * FROM diaries WHERE isPinned = 1 ORDER BY createdAt DESC")
    fun getPinnedDiaries(): Flow<List<Diary>>

    /**
     * 根据标签查询日记
     * @param tagIds 标签ID列表
     * @return 包含指定标签的日记列表Flow
     */
    @Query("""
        SELECT DISTINCT d.* FROM diaries d
        INNER JOIN diary_tags dt ON d.id = dt.diaryId
        WHERE dt.tagId IN (:tagIds)
        GROUP BY d.id
        HAVING COUNT(DISTINCT dt.tagId) = :tagCount
        ORDER BY d.isPinned DESC, d.createdAt DESC
    """)
    fun getDiariesByTags(tagIds: List<Long>, tagCount: Int): Flow<List<Diary>>
}