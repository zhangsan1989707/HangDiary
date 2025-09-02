package com.example.hangdiary.data.repository

import com.example.hangdiary.data.dao.DiaryTagDao
import com.example.hangdiary.data.dao.TagDao
import com.example.hangdiary.data.model.DiaryTagCrossRef
import com.example.hangdiary.data.model.Tag
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 标签仓库
 * 管理标签的CRUD操作和与日记的关联关系
 */
@Singleton
class TagRepository @Inject constructor(
    private val tagDao: TagDao,
    private val diaryTagDao: DiaryTagDao
) {

    /**
     * 获取所有标签
     */
    fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()

    /**
     * 获取带使用统计的标签
     */
    fun getTagsWithUsageCount(): Flow<List<Tag>> = tagDao.getTagsWithUsageCount()

    /**
     * 根据ID获取标签
     */
    suspend fun getTagById(id: Long): Tag? = tagDao.getTagById(id)

    /**
     * 根据名称获取标签
     */
    suspend fun getTagByName(name: String): Tag? = tagDao.getTagByName(name)

    /**
     * 创建新标签
     */
    suspend fun createTag(name: String, color: Int): Long {
        val tag = Tag(
            name = name,
            color = color,
            createdAt = LocalDateTime.now()
        )
        return tagDao.insertTag(tag)
    }

    /**
     * 更新标签
     */
    suspend fun updateTag(tag: Tag) {
        tagDao.updateTag(tag)
    }

    /**
     * 删除标签（会级联删除关联关系）
     */
    suspend fun deleteTag(tag: Tag) {
        tagDao.deleteTag(tag)
    }

    /**
     * 搜索标签
     */
    fun searchTags(query: String): Flow<List<Tag>> = tagDao.searchTags(query)

    /**
     * 获取日记的所有标签
     */
    fun getTagsForDiary(diaryId: Long): Flow<List<Tag>> = diaryTagDao.getTagsForDiary(diaryId)

    /**
     * 给日记添加标签
     */
    suspend fun addTagToDiary(diaryId: Long, tagId: Long) {
        val crossRef = DiaryTagCrossRef(diaryId = diaryId, tagId = tagId)
        diaryTagDao.insertDiaryTag(crossRef)
    }

    /**
     * 从日记移除标签
     */
    suspend fun removeTagFromDiary(diaryId: Long, tagId: Long) {
        val crossRef = DiaryTagCrossRef(diaryId = diaryId, tagId = tagId)
        diaryTagDao.deleteDiaryTag(crossRef)
    }

    /**
     * 检查日记是否有指定标签
     */
    suspend fun hasTag(diaryId: Long, tagId: Long): Boolean = diaryTagDao.hasTag(diaryId, tagId)

    /**
     * 获取日记的标签数量
     */
    suspend fun getDiaryTagCount(diaryId: Long) {
        return diaryTagDao.getTagsForDiary(diaryId).collect { it.size }
    }

    /**
     * 清除日记的所有标签
     */
    suspend fun clearAllTagsFromDiary(diaryId: Long) {
        diaryTagDao.deleteAllTagsForDiary(diaryId)
    }
}