package com.example.hangdiary.domain.usecase.diary

import com.example.hangdiary.data.model.DiaryWithTags
import com.example.hangdiary.data.repository.DiaryRepository
import com.example.hangdiary.ui.common.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * 搜索日记用例
 * 提供高级搜索功能，支持多种搜索条件
 */
class SearchDiariesUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    
    /**
     * 搜索日记
     * @param criteria 搜索条件
     * @return 搜索结果的UI状态流
     */
    operator fun invoke(criteria: SearchCriteria): Flow<UiState<List<DiaryWithTags>>> {
        return repository.getAllDiariesWithTags()
            .map { diaries ->
                val filteredDiaries = diaries.filter { diaryWithTags ->
                    matchesCriteria(diaryWithTags, criteria)
                }
                
                if (filteredDiaries.isEmpty()) {
                    UiState.Empty("没有找到匹配的日记")
                } else {
                    val sortedDiaries = when (criteria.sortBy) {
                        SortOption.DATE_DESC -> filteredDiaries.sortedByDescending { it.diary.createdAt }
                        SortOption.DATE_ASC -> filteredDiaries.sortedBy { it.diary.createdAt }
                        SortOption.TITLE_ASC -> filteredDiaries.sortedBy { it.diary.title }
                        SortOption.TITLE_DESC -> filteredDiaries.sortedByDescending { it.diary.title }
                        SortOption.RELEVANCE -> filteredDiaries.sortedByDescending { 
                            calculateRelevanceScore(it, criteria.keyword)
                        }
                    }
                    UiState.Success(sortedDiaries)
                }
            }
            .catch { exception ->
                emit(UiState.Error(exception, "搜索失败"))
            }
    }
    
    /**
     * 快速搜索（仅关键词）
     * @param keyword 搜索关键词
     * @return 搜索结果的UI状态流
     */
    fun quickSearch(keyword: String): Flow<UiState<List<DiaryWithTags>>> {
        return invoke(SearchCriteria(keyword = keyword))
    }
    
    /**
     * 检查日记是否匹配搜索条件
     */
    private fun matchesCriteria(diaryWithTags: DiaryWithTags, criteria: SearchCriteria): Boolean {
        val diary = diaryWithTags.diary
        val tags = diaryWithTags.tags
        
        // 关键词匹配
        if (criteria.keyword.isNotBlank()) {
            val keyword = criteria.keyword.lowercase()
            val titleMatch = diary.title.lowercase().contains(keyword)
            val contentMatch = diary.content.lowercase().contains(keyword)
            val tagMatch = tags.any { it.name.lowercase().contains(keyword) }
            
            if (!titleMatch && !contentMatch && !tagMatch) {
                return false
            }
        }
        
        // 标签匹配
        if (criteria.tags.isNotEmpty()) {
            val diaryTagNames = tags.map { it.name }
            val hasMatchingTag = criteria.tags.any { searchTag ->
                diaryTagNames.any { diaryTag -> 
                    diaryTag.lowercase().contains(searchTag.lowercase())
                }
            }
            if (!hasMatchingTag) {
                return false
            }
        }
        
        // 日期范围匹配
        criteria.dateRange?.let { range ->
            val diaryDate = diary.createdAt.toLocalDate()
            if (diaryDate.isBefore(range.startDate) || diaryDate.isAfter(range.endDate)) {
                return false
            }
        }
        
        // 颜色匹配
        criteria.color?.let { searchColor ->
            if (diary.color != searchColor) {
                return false
            }
        }
        
        // 顶置状态匹配
        criteria.isPinned?.let { pinned ->
            if (diary.isPinned != pinned) {
                return false
            }
        }
        
        return true
    }
    
    /**
     * 计算相关性评分
     */
    private fun calculateRelevanceScore(diaryWithTags: DiaryWithTags, keyword: String): Int {
        if (keyword.isBlank()) return 0
        
        val diary = diaryWithTags.diary
        val tags = diaryWithTags.tags
        val lowerKeyword = keyword.lowercase()
        
        var score = 0
        
        // 标题匹配得分更高
        val titleMatches = diary.title.lowercase().split(" ").count { it.contains(lowerKeyword) }
        score += titleMatches * 10
        
        // 内容匹配
        val contentMatches = diary.content.lowercase().split(" ").count { it.contains(lowerKeyword) }
        score += contentMatches * 5
        
        // 标签匹配
        val tagMatches = tags.count { it.name.lowercase().contains(lowerKeyword) }
        score += tagMatches * 15
        
        // 完全匹配加分
        if (diary.title.lowercase() == lowerKeyword) score += 50
        if (tags.any { it.name.lowercase() == lowerKeyword }) score += 30
        
        return score
    }
}

/**
 * 搜索条件
 */
data class SearchCriteria(
    val keyword: String = "",
    val tags: List<String> = emptyList(),
    val dateRange: DateRange? = null,
    val color: String? = null,
    val isPinned: Boolean? = null,
    val sortBy: SortOption = SortOption.RELEVANCE
)

/**
 * 日期范围
 */
data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
)

/**
 * 排序选项
 */
enum class SortOption {
    DATE_DESC,    // 日期降序（最新优先）
    DATE_ASC,     // 日期升序（最旧优先）
    TITLE_ASC,    // 标题升序
    TITLE_DESC,   // 标题降序
    RELEVANCE     // 相关性排序
}