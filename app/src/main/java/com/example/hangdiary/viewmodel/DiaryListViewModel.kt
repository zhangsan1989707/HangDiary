package com.example.hangdiary.viewmodel

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * 日记列表视图模型
 * 负责管理日记列表的UI状态和业务逻辑
 */
class DiaryListViewModel(
    private val diaryRepository: DiaryRepository,
    private val tagRepository: TagRepository
) : ViewModel() {
    
    // 日记和标签组合数据类
    data class DiaryWithTags(
        val diary: Diary,
        val tags: List<Tag>
    )
    
    // 日记列表状态
    private val _diaryListState = MutableStateFlow<List<DiaryWithTags>>(emptyList())
    val diaryListState: StateFlow<List<DiaryWithTags>> = _diaryListState
    
    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // 当前搜索关键词
    private var currentSearchKeyword: String? = null
    
    // 标签筛选
    private var currentTagIds: List<Long> = emptyList()
    
    init {
        // 初始化时加载所有日记
        loadAllDiaries()
    }
    
    /**
     * 加载所有日记
     */
    fun loadAllDiaries() {
        viewModelScope.launch {
            _isLoading.value = true
            currentSearchKeyword = null
            currentTagIds = emptyList()
            
            diaryRepository.getAllDiaries().collectLatest { diaries ->
                val diaryFlows = diaries.map { diary ->
                    tagRepository.getTagsForDiary(diary.id).map { tags ->
                        DiaryWithTags(diary, tags)
                    }
                }
                
                if (diaryFlows.isNotEmpty()) {
                    combine(diaryFlows) { diaryWithTagsArray ->
                        diaryWithTagsArray.toList()
                    }.collect { diaryWithTagsList ->
                        _diaryListState.value = diaryWithTagsList
                        _isLoading.value = false
                    }
                } else {
                    _diaryListState.value = emptyList()
                    _isLoading.value = false
                }
            }
        }
    }
    
    /**
     * 搜索日记
     * @param keyword 搜索关键词
     */
    fun searchDiaries(keyword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            currentSearchKeyword = keyword
            currentTagIds = emptyList()
            
            if (keyword.isBlank()) {
                loadAllDiaries()
            } else {
                diaryRepository.searchDiaries(keyword).collectLatest { diaries ->
                    val diaryFlows = diaries.map { diary ->
                        tagRepository.getTagsForDiary(diary.id).map { tags ->
                            DiaryWithTags(diary, tags)
                        }
                    }
                    
                    if (diaryFlows.isNotEmpty()) {
                        combine(diaryFlows) { diaryWithTagsArray ->
                            diaryWithTagsArray.toList()
                        }.collect { diaryWithTagsList ->
                            _diaryListState.value = diaryWithTagsList
                            _isLoading.value = false
                        }
                    } else {
                        _diaryListState.value = emptyList()
                        _isLoading.value = false
                    }
                }
            }
        }
    }
    
    /**
     * 根据日期范围加载日记
     * @param start 开始日期
     * @param end 结束日期
     */
    fun loadDiariesByDateRange(start: LocalDateTime, end: LocalDateTime) {
        viewModelScope.launch {
            _isLoading.value = true
            currentSearchKeyword = null
            currentTagIds = emptyList()
            
            diaryRepository.getDiariesByDateRange(start, end).collectLatest { diaries ->
                val diaryFlows = diaries.map { diary ->
                    tagRepository.getTagsForDiary(diary.id).map { tags ->
                        DiaryWithTags(diary, tags)
                    }
                }
                
                if (diaryFlows.isNotEmpty()) {
                    combine(diaryFlows) { diaryWithTagsArray ->
                        diaryWithTagsArray.toList()
                    }.collect { diaryWithTagsList ->
                        _diaryListState.value = diaryWithTagsList
                        _isLoading.value = false
                    }
                } else {
                    _diaryListState.value = emptyList()
                    _isLoading.value = false
                }
            }
        }
    }
    
    /**
     * 切换日记的顶置状态
     * @param diary 要切换顶置状态的日记
     */
    fun togglePinned(diary: Diary) {
        viewModelScope.launch {
            diaryRepository.updatePinnedStatus(diary.id, !diary.isPinned)
            // 刷新当前列表
            refreshCurrentList()
        }
    }

    /**
     * 更新日记的颜色
     * @param diaryId 日记ID
     * @param color 日记颜色
     */
    fun updateDiaryColor(diaryId: Long, color: String?) {
        viewModelScope.launch {
            diaryRepository.updateDiaryColor(diaryId, color)
            // 刷新当前列表
            refreshCurrentList()
        }
    }

    /**
     * 批量更新日记的颜色
     * @param diaryIds 日记ID列表
     * @param color 日记颜色
     */
    fun updateDiariesColor(diaryIds: List<Long>, color: String?) {
        viewModelScope.launch {
            diaryRepository.updateDiariesColor(diaryIds, color)
            // 刷新当前列表
            refreshCurrentList()
        }
    }
    
    /**
     * 批量删除日记
     * @param diaryIds 日记ID列表
     */
    fun deleteDiaries(diaryIds: List<Long>) {
        viewModelScope.launch {
            diaryRepository.deleteDiariesByIds(diaryIds)
            // 列表会自动刷新，因为Room会触发Flow更新
        }
    }
    
    /**
     * 删除日记
     * @param diary 要删除的日记
     */
    fun deleteDiary(diary: Diary) {
        viewModelScope.launch {
            diaryRepository.delete(diary)
            // 列表会自动刷新，因为Room会触发Flow更新
        }
    }
    
    /**
     * 根据标签筛选日记
     * @param tagIds 标签ID列表
     */
    fun loadDiariesByTags(tagIds: List<Long>) {
        viewModelScope.launch {
            currentSearchKeyword = null
            currentTagIds = tagIds
            if (tagIds.isEmpty()) {
                loadAllDiaries()
            } else {
                diaryRepository.getDiariesByTags(tagIds).collectLatest { diaries ->
                    val diaryFlows = diaries.map { diary ->
                        tagRepository.getTagsForDiary(diary.id).map { tags ->
                            DiaryWithTags(diary, tags)
                        }
                    }
                    
                    if (diaryFlows.isNotEmpty()) {
                        combine(diaryFlows) { diaryWithTagsArray ->
                            diaryWithTagsArray.toList()
                        }.collect { diaryWithTagsList ->
                            _diaryListState.value = diaryWithTagsList
                        }
                    } else {
                        _diaryListState.value = emptyList()
                    }
                }
            }
        }
    }

    /**
     * 加载置顶的日记
     */
    fun loadPinnedDiaries() {
        viewModelScope.launch {
            currentSearchKeyword = null
            currentTagIds = emptyList()
            
            diaryRepository.getPinnedDiaries().collectLatest { diaries ->
                val diaryFlows = diaries.map { diary ->
                    tagRepository.getTagsForDiary(diary.id).map { tags ->
                        DiaryWithTags(diary, tags)
                    }
                }
                
                if (diaryFlows.isNotEmpty()) {
                    combine(diaryFlows) { diaryWithTagsArray ->
                        diaryWithTagsArray.toList()
                    }.collect { diaryWithTagsList ->
                        _diaryListState.value = diaryWithTagsList
                    }
                } else {
                    _diaryListState.value = emptyList()
                }
            }
        }
    }

    /**
     * 获取日记的标签列表
     * @param diaryId 日记ID
     */
    fun getTagsForDiary(diaryId: Long) = tagRepository.getTagsForDiary(diaryId)

    /**
     * 刷新当前列表
     * 根据当前的搜索关键词或标签ID重新加载数据
     */
    private fun refreshCurrentList() {
        when {
            currentSearchKeyword != null -> searchDiaries(currentSearchKeyword!!)
            currentTagIds.isNotEmpty() -> loadDiariesByTags(currentTagIds)
            else -> loadAllDiaries()
        }
    }
}