package com.example.hangdiary.data.dao

import androidx.room.*
import com.example.hangdiary.data.model.DiaryTagCrossRef
import com.example.hangdiary.data.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * 日记-标签关联数据访问对象
 */
@Dao
interface DiaryTagDao {

    /**
     * 插入日记-标签关联
     */
    @Insert
    suspend fun insertDiaryTag(diaryTag: DiaryTagCrossRef)

    /**
     * 删除日记-标签关联
     */
    @Delete
    suspend fun deleteDiaryTag(diaryTag: DiaryTagCrossRef)

    /**
     * 根据日记ID删除所有关联
     */
    @Query("DELETE FROM diary_tags WHERE diaryId = :diaryId")
    suspend fun deleteAllTagsForDiary(diaryId: Long)

    /**
     * 根据标签ID删除所有关联
     */
    @Query("DELETE FROM diary_tags WHERE tagId = :tagId")
    suspend fun deleteAllDiariesForTag(tagId: Long)

    /**
     * 获取指定日记的所有标签
     */
    @Query("""
        SELECT tags.* FROM tags 
        INNER JOIN diary_tags ON tags.id = diary_tags.tagId 
        WHERE diary_tags.diaryId = :diaryId 
        ORDER BY tags.name ASC
    """)
    fun getTagsForDiary(diaryId: Long): Flow<List<Tag>>

    /**
     * 获取指定标签的所有日记ID
     */
    @Query("SELECT diaryId FROM diary_tags WHERE tagId = :tagId")
    suspend fun getDiaryIdsForTag(tagId: Long): List<Long>

    /**
     * 检查日记是否有关联标签
     */
    @Query("SELECT COUNT(*) > 0 FROM diary_tags WHERE diaryId = :diaryId")
    suspend fun hasTags(diaryId: Long): Boolean

    /**
     * 检查指定标签是否关联到指定日记
     */
    @Query("SELECT COUNT(*) > 0 FROM diary_tags WHERE diaryId = :diaryId AND tagId = :tagId")
    suspend fun hasTag(diaryId: Long, tagId: Long): Boolean
}