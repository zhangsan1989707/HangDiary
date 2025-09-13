package com.example.hangdiary.domain.usecase.diary

import com.example.hangdiary.data.model.DiaryWithTags
import com.example.hangdiary.data.repository.DiaryRepository
import com.example.hangdiary.data.repository.TagRepository
import com.example.hangdiary.ui.common.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 根据标签获取日记用例
 * 封装根据标签筛选日记的业务逻辑
 */
class GetDiariesByTagUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val tagRepository: TagRepository
) {
    
    /**
     * 根据标签ID列表获取日记
     * @param tagIds 标签ID列表
     * @return 包含指定标签的日记列表UI状态流
     */
    operator fun invoke(tagIds: List<Long>): Flow<UiState<List<DiaryWithTags>>> {
        return diaryRepository.getDiariesByTags(tagIds)
            .map { diaries ->
                if (diaries.isEmpty()) {
                    UiState.Empty("没有找到包含这些标签的日记")
                } else {
                    // 暂时为所有日记使用空标签列表
                    val diariesWithEmptyTags = diaries.map { diary ->
                        DiaryWithTags(diary, emptyList())
                    }
                    
                    val sortedDiaries = diariesWithEmptyTags
                        .sortedWith(
                            compareByDescending<DiaryWithTags> { it.diary.isPinned }
                                .thenByDescending { it.diary.createdAt }
                        )
                    UiState.Success(sortedDiaries)
                }
            }
            .catch { exception ->
                emit(UiState.Error(exception, "获取标签日记失败"))
            }
    }
}