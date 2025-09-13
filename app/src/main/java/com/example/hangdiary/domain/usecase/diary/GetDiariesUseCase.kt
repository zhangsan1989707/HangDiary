package com.example.hangdiary.domain.usecase.diary

import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.model.DiaryWithTags
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.data.repository.DiaryRepository
import com.example.hangdiary.ui.common.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 获取日记列表用例
 * 封装获取日记列表的业务逻辑，包括排序、筛选等
 */
class GetDiariesUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    
    /**
     * 获取所有日记（带标签）
     * @return 日记列表的UI状态流
     */
    operator fun invoke(): Flow<UiState<List<DiaryWithTags>>> {
        return repository.getAllDiariesWithTags()
            .map { diaries ->
                if (diaries.isEmpty()) {
                    UiState.Empty("还没有日记，开始记录你的生活吧！")
                } else {
                    // 按顶置状态和创建时间排序
                    val sortedDiaries = diaries.sortedWith(
                        compareByDescending<DiaryWithTags> { it.diary.isPinned }
                            .thenByDescending { it.diary.createdAt }
                    )
                    UiState.Success(sortedDiaries)
                }
            }
            .catch { exception ->
                emit(UiState.Error(exception, "获取日记列表失败"))
            }
    }
    
    /**
     * 获取指定数量的最新日记
     * @param limit 限制数量
     * @return 日记列表的UI状态流
     */
    fun getRecentDiaries(limit: Int): Flow<UiState<List<DiaryWithTags>>> {
        return repository.getAllDiariesWithTags()
            .map { diaries ->
                if (diaries.isEmpty()) {
                    UiState.Empty("还没有日记")
                } else {
                    val recentDiaries = diaries
                        .sortedByDescending { it.diary.createdAt }
                        .take(limit)
                    UiState.Success(recentDiaries)
                }
            }
            .catch { exception ->
                emit(UiState.Error(exception, "获取最新日记失败"))
            }
    }
    
    /**
     * 获取顶置的日记
     * @return 顶置日记列表的UI状态流
     */
    fun getPinnedDiaries(): Flow<UiState<List<DiaryWithTags>>> {
        return repository.getAllDiariesWithTags()
            .map { diaries ->
                val pinnedDiaries = diaries
                    .filter { it.diary.isPinned }
                    .sortedByDescending { it.diary.createdAt }
                
                if (pinnedDiaries.isEmpty()) {
                    UiState.Empty("没有顶置的日记")
                } else {
                    UiState.Success(pinnedDiaries)
                }
            }
            .catch { exception ->
                emit(UiState.Error(exception, "获取顶置日记失败"))
            }
    }
}