package com.example.hangdiary.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.data.repository.DiaryRepository
import com.example.hangdiary.data.repository.TagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiaryDetailViewModel(
    private val diaryRepository: DiaryRepository,
    private val tagRepository: TagRepository,
    private val defaultDiaryColor: String? = null
) : ViewModel() {
    
    private val _diaryState = MutableStateFlow<Diary?>(null)
    val diaryState: StateFlow<Diary?> = _diaryState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _allTags = MutableStateFlow<List<Tag>>(emptyList())
    val allTags: StateFlow<List<Tag>> = _allTags.asStateFlow()
    
    var isEditing by mutableStateOf(false)
    var title by mutableStateOf("")
    var content by mutableStateOf("")
    var color by mutableStateOf<String?>(null)
    var imagePaths by mutableStateOf(emptyList<String>())
    var selectedTagIds by mutableStateOf(emptyList<Long>())
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    fun loadDiary(diaryId: Long) {
        if (diaryId == 0L) {
            // 新建日记，应用默认颜色设置
            _diaryState.value = null
            title = ""
            content = ""
            color = defaultDiaryColor
            imagePaths = emptyList()
            selectedTagIds = emptyList()
            isEditing = true
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val diary = diaryRepository.getDiaryById(diaryId)
                _diaryState.value = diary
                diary?.let {
                    title = it.title
                    content = it.content
                    color = it.color
                    imagePaths = it.imagePaths
                    
                    // 加载日记的标签
                    val tags = tagRepository.getTagsForDiary(it.id).first()
                    selectedTagIds = tags.map { tag -> tag.id }
                }
            } catch (e: Exception) {
                _error.value = "加载日记失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    suspend fun saveDiary() {
        try {
            val now = LocalDateTime.now()
            val diary = Diary(
                id = _diaryState.value?.id ?: 0L,
                title = title,
                content = content,
                color = color,
                imagePaths = imagePaths,
                createdAt = _diaryState.value?.createdAt ?: now,
                updatedAt = now
            )
            
            val diaryId = if (diary.id == 0L) {
                // 新建日记
                val newId = diaryRepository.insert(diary)
                if (newId <= 0) {
                    throw Exception("插入日记失败，返回的ID无效: $newId")
                }
                newId
            } else {
                // 更新日记
                val updatedRows = diaryRepository.update(diary)
                if (updatedRows <= 0) {
                    throw Exception("更新日记失败，影响的行数为: $updatedRows")
                }
                diary.id
            }
            
            // 更新日记与标签的关联关系
            try {
                if (diary.id == 0L) {
                    // 新建日记，直接添加标签关联
                    selectedTagIds.forEach { tagId ->
                        tagRepository.addTagToDiary(diaryId, tagId)
                    }
                } else {
                    // 更新日记，先清除所有标签关联，再重新添加
                    tagRepository.clearAllTagsFromDiary(diary.id)
                    selectedTagIds.forEach { tagId ->
                        tagRepository.addTagToDiary(diary.id, tagId)
                    }
                }
            } catch (e: Exception) {
                // 如果标签关联失败，记录错误但不影响日记保存
                println("标签关联失败: ${e.message}")
            }
            
            // 更新状态
            _diaryState.value = diary.copy(id = diaryId)
            isEditing = false
        } catch (e: Exception) {
            _error.value = "保存日记失败: ${e.message}"
            throw e // 重新抛出异常，让UI层处理
        }
    }
    
    suspend fun deleteDiary() {
        _diaryState.value?.let { diary ->
            try {
                // 删除日记与标签的关联关系
                tagRepository.clearAllTagsFromDiary(diary.id)
                // 删除日记
                diaryRepository.delete(diary)
            } catch (e: Exception) {
                _error.value = "删除日记失败: ${e.message}"
            }
        }
    }
    
    fun loadAllTags() {
        viewModelScope.launch {
            try {
                tagRepository.getAllTags().collect { tags ->
                    _allTags.value = tags
                }
            } catch (e: Exception) {
                _error.value = "加载标签失败: ${e.message}"
            }
        }
    }
    
    suspend fun createTag(name: String, color: Int): Tag? {
        return try {
            val tagId = tagRepository.createTag(name, color)
            val newTag = Tag(
                id = tagId,
                name = name,
                color = color,
                createdAt = LocalDateTime.now()
            )
            // 更新标签列表
            _allTags.value = _allTags.value + newTag
            newTag
        } catch (e: Exception) {
            _error.value = "创建标签失败: ${e.message}"
            null
        }
    }
    
    suspend fun getTagsForDiary(diaryId: Long): List<Tag> {
        return try {
            tagRepository.getTagsForDiary(diaryId).first()
        } catch (e: Exception) {
            _error.value = "获取日记标签失败: ${e.message}"
            emptyList()
        }
    }

    /**
     * 更新默认日记颜色
     * @param newDefaultColor 新的默认颜色
     */
    fun updateDefaultColor(newDefaultColor: String?) {
        // 如果是新建日记模式，立即应用新的默认颜色
        if (_diaryState.value == null) {
            color = newDefaultColor
        }
    }
    

}