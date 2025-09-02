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
    
    // 当前搜索关键词
    private var currentSearchKeyword: String? = null
    
    // 当前选中的分类ID
    private var currentCategoryId: Long? = null
    
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
            currentSearchKeyword = null
            currentCategoryId = null
            currentTagIds = emptyList()
            
            diaryRepository.getAllDiaries().collectLatest { diaries ->
                val diaryWithTagsList = mutableListOf<DiaryWithTags>()
                
                // 为每个日记获取标签
                diaries.forEach { diary ->
                    val tags = tagRepository.getTagsForDiary(diary.id)
                    tags.collect { tagList ->
                        diaryWithTagsList.add(DiaryWithTags(diary, tagList))
                        if (diaryWithTagsList.size == diaries.size) {
                            _diaryListState.value = diaryWithTagsList
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 根据分类加载日记
     * @param categoryId 分类ID
     */
    fun loadDiariesByCategory(categoryId: Long) {
        viewModelScope.launch {
            currentSearchKeyword = null
            currentCategoryId = categoryId
            currentTagIds = emptyList()
            
            diaryRepository.getDiariesByCategory(categoryId).collectLatest { diaries ->
                val diaryWithTagsList = mutableListOf<DiaryWithTags>()
                
                // 为每个日记获取标签
                diaries.forEach { diary ->
                    val tags = tagRepository.getTagsForDiary(diary.id)
                    tags.collect { tagList ->
                        diaryWithTagsList.add(DiaryWithTags(diary, tagList))
                        if (diaryWithTagsList.size == diaries.size) {
                            _diaryListState.value = diaryWithTagsList
                        }
                    }
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
            currentSearchKeyword = keyword
            currentCategoryId = null
            currentTagIds = emptyList()
            
            if (keyword.isBlank()) {
                loadAllDiaries()
            } else {
                diaryRepository.searchDiaries(keyword).collectLatest { diaries ->
                    val diaryWithTagsList = mutableListOf<DiaryWithTags>()
                    
                    // 为每个日记获取标签
                    diaries.forEach { diary ->
                        val tags = tagRepository.getTagsForDiary(diary.id)
                        tags.collect { tagList ->
                            diaryWithTagsList.add(DiaryWithTags(diary, tagList))
                            if (diaryWithTagsList.size == diaries.size) {
                                _diaryListState.value = diaryWithTagsList
                            }
                        }
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
            currentSearchKeyword = null
            currentCategoryId = null
            currentTagIds = emptyList()
            
            diaryRepository.getDiariesByDateRange(start, end).collectLatest { diaries ->
                val diaryWithTagsList = mutableListOf<DiaryWithTags>()
                
                // 为每个日记获取标签
                diaries.forEach { diary ->
                    val tags = tagRepository.getTagsForDiary(diary.id)
                    tags.collect { tagList ->
                        diaryWithTagsList.add(DiaryWithTags(diary, tagList))
                        if (diaryWithTagsList.size == diaries.size) {
                            _diaryListState.value = diaryWithTagsList
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 切换日记的收藏状态
     * @param diary 要切换收藏状态的日记
     */
    fun toggleFavorite(diary: Diary) {
        viewModelScope.launch {
            diaryRepository.updateFavoriteStatus(diary.id, !diary.isFavorite)
            // 刷新当前列表
            refreshCurrentList()
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
            currentCategoryId = null
            currentTagIds = tagIds
            if (tagIds.isEmpty()) {
                loadAllDiaries()
            } else {
                diaryRepository.getDiariesByTags(tagIds).collectLatest { diaries ->
                    val diaryWithTagsList = mutableListOf<DiaryWithTags>()
                    
                    // 为每个日记获取标签
                    diaries.forEach { diary ->
                        val tags = tagRepository.getTagsForDiary(diary.id)
                        tags.collect { tagList ->
                            diaryWithTagsList.add(DiaryWithTags(diary, tagList))
                            if (diaryWithTagsList.size == diaries.size) {
                                _diaryListState.value = diaryWithTagsList
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 加载收藏的日记
     */
    fun loadFavoriteDiaries() {
        viewModelScope.launch {
            currentSearchKeyword = null
            currentCategoryId = null
            currentTagIds = emptyList()
            
            diaryRepository.getFavoriteDiaries().collectLatest { diaries ->
                val diaryWithTagsList = mutableListOf<DiaryWithTags>()
                
                // 为每个日记获取标签
                diaries.forEach { diary ->
                    val tags = tagRepository.getTagsForDiary(diary.id)
                    tags.collect { tagList ->
                        diaryWithTagsList.add(DiaryWithTags(diary, tagList))
                        if (diaryWithTagsList.size == diaries.size) {
                            _diaryListState.value = diaryWithTagsList
                        }
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
            currentCategoryId = null
            currentTagIds = emptyList()
            
            diaryRepository.getPinnedDiaries().collectLatest { diaries: List<Diary> ->
                val diaryWithTagsList = mutableListOf<DiaryWithTags>()
                
                // 为每个日记获取标签
                diaries.forEach { diary: Diary ->
                    val tags = tagRepository.getTagsForDiary(diary.id)
                    tags.collect { tagList: List<Tag> ->
                        diaryWithTagsList.add(DiaryWithTags(diary, tagList))
                        if (diaryWithTagsList.size == diaries.size) {
                            _diaryListState.value = diaryWithTagsList
                        }
                    }
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
     * 根据当前的搜索关键词或分类ID重新加载数据
     */
    private fun refreshCurrentList() {
        when {
            currentSearchKeyword != null -> searchDiaries(currentSearchKeyword!!)
            currentCategoryId != null -> loadDiariesByCategory(currentCategoryId!!)
            currentTagIds.isNotEmpty() -> loadDiariesByTags(currentTagIds)
            else -> loadAllDiaries()
        }
    }
}