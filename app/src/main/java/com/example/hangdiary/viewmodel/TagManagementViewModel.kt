package com.example.hangdiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.data.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 标签管理ViewModel
 * 管理标签的CRUD操作和与日记的关联关系
 */
@HiltViewModel
class TagManagementViewModel @Inject constructor(
    private val tagRepository: TagRepository
) : ViewModel() {

    // UI状态
    data class TagManagementState(
        val tags: List<Tag> = emptyList(),
        val selectedTags: List<Tag> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val searchQuery: String = "",
        val showCreateDialog: Boolean = false
    )

    // 状态流
    private val _state = MutableStateFlow(TagManagementState())
    val state: StateFlow<TagManagementState> = _state.asStateFlow()

    // 事件
    sealed class TagManagementEvent {
        data class CreateTag(val name: String, val color: Int) : TagManagementEvent()
        data class UpdateTag(val tag: Tag) : TagManagementEvent()
        data class DeleteTag(val tag: Tag) : TagManagementEvent()
        data class SearchTags(val query: String) : TagManagementEvent()
        data class LoadTagsForDiary(val diaryId: Long) : TagManagementEvent()
        data class ToggleTagForDiary(val diaryId: Long, val tag: Tag) : TagManagementEvent()
        object ShowCreateDialog : TagManagementEvent()
        object HideCreateDialog : TagManagementEvent()
    }

    init {
        loadAllTags()
    }

    /**
     * 加载指定日记的标签
     */
    fun loadTagsForDiary(diaryId: Long) {
        viewModelScope.launch {
            try {
                tagRepository.getTagsForDiary(diaryId)
                    .collect { tags ->
                        _state.update { it.copy(selectedTags = tags) }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    /**
     * 创建新标签
     */
    fun createTag(name: String, color: Int) {
        viewModelScope.launch {
            try {
                tagRepository.createTag(name, color)
                // 创建成功后重新加载标签列表
                loadAllTags()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    /**
     * 给日记添加标签
     */
    fun addTagToDiary(diaryId: Long, tagId: Long) {
        viewModelScope.launch {
            try {
                tagRepository.addTagToDiary(diaryId, tagId)
                // 添加成功后重新加载日记的标签
                loadTagsForDiary(diaryId)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    /**
     * 从日记移除标签
     */
    fun removeTagFromDiary(diaryId: Long, tagId: Long) {
        viewModelScope.launch {
            try {
                tagRepository.removeTagFromDiary(diaryId, tagId)
                // 移除成功后重新加载日记的标签
                loadTagsForDiary(diaryId)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    /**
     * 加载所有标签
     */
    fun loadAllTags() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                tagRepository.getAllTags()
                    .collect { tags ->
                        _state.update { it.copy(tags = tags, isLoading = false) }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    /**
     * 处理事件
     */
    fun handleEvent(event: TagManagementEvent) {
        viewModelScope.launch {
            try {
                when (event) {
                    is TagManagementEvent.CreateTag -> {
                        tagRepository.createTag(event.name, event.color)
                    }
                    is TagManagementEvent.UpdateTag -> {
                        tagRepository.updateTag(event.tag)
                    }
                    is TagManagementEvent.DeleteTag -> {
                        tagRepository.deleteTag(event.tag)
                    }
                    is TagManagementEvent.SearchTags -> {
                        _state.update { it.copy(searchQuery = event.query) }
                        if (event.query.isBlank()) {
                            loadAllTags()
                        } else {
                            tagRepository.searchTags(event.query)
                                .collect { tags ->
                                    _state.update { it.copy(tags = tags) }
                                }
                        }
                    }
                    is TagManagementEvent.LoadTagsForDiary -> {
                        loadTagsForDiary(event.diaryId)
                    }
                    is TagManagementEvent.ToggleTagForDiary -> {
                        toggleTagForDiary(event.diaryId, event.tag)
                    }
                    TagManagementEvent.ShowCreateDialog -> {
                        _state.update { it.copy(showCreateDialog = true) }
                    }
                    TagManagementEvent.HideCreateDialog -> {
                        _state.update { it.copy(showCreateDialog = false) }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }



    /**
     * 切换日记的标签状态
     */
    private suspend fun toggleTagForDiary(diaryId: Long, tag: Tag) {
        val hasTag = tagRepository.hasTag(diaryId, tag.id)
        if (hasTag) {
            tagRepository.removeTagFromDiary(diaryId, tag.id)
        } else {
            tagRepository.addTagToDiary(diaryId, tag.id)
        }
    }

    /**
     * 清除错误状态
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    /**
     * 检查标签名称是否已存在
     */
    suspend fun isTagNameExists(name: String): Boolean {
        return tagRepository.getTagByName(name) != null
    }

    /**
     * 生成随机标签颜色
     */
    fun generateRandomTagColor(): Int {
        val colors = listOf(
            0xFFE57373, // 红色
            0xFF81C784, // 绿色
            0xFF64B5F6, // 蓝色
            0xFFFFB74D, // 橙色
            0xFFBA68C8, // 紫色
            0xFF4FC3F7, // 天蓝色
            0xFFFFF176, // 黄色
            0xFFFF8A65  // 珊瑚色
        )
        return colors.random().toInt()
    }
}