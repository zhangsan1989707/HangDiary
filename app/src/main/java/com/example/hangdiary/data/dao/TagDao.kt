package com.example.hangdiary.data.dao

import androidx.room.*
import com.example.hangdiary.data.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * 标签数据访问对象
 */
@Dao
interface TagDao {

    /**
     * 获取所有标签，按名称升序排列
     */
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<Tag>>

    /**
     * 根据ID获取单个标签
     */
    @Query("SELECT * FROM tags WHERE id = :id")
    suspend fun getTagById(id: Long): Tag?

    /**
     * 根据名称获取标签
     */
    @Query("SELECT * FROM tags WHERE name = :name")
    suspend fun getTagByName(name: String): Tag?

    /**
     * 插入新的标签
     */
    @Insert
    suspend fun insertTag(tag: Tag): Long

    /**
     * 更新标签
     */
    @Update
    suspend fun updateTag(tag: Tag)

    /**
     * 删除标签
     */
    @Delete
    suspend fun deleteTag(tag: Tag)

    /**
     * 根据ID删除标签
     */
    @Query("DELETE FROM tags WHERE id = :id")
    suspend fun deleteTagById(id: Long)

    /**
     * 搜索标签
     */
    @Query("SELECT * FROM tags WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchTags(query: String): Flow<List<Tag>>

    /**
     * 获取标签使用统计（按使用次数降序）
     */
    @RewriteQueriesToDropUnusedColumns
    @Query("""
        SELECT tags.*, COUNT(diary_tags.diaryId) as usageCount 
        FROM tags 
        LEFT JOIN diary_tags ON tags.id = diary_tags.tagId 
        GROUP BY tags.id 
        ORDER BY usageCount DESC, name ASC
    """)
    fun getTagsWithUsageCount(): Flow<List<Tag>>
}