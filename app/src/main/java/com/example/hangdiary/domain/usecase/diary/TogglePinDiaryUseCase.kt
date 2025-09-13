package com.example.hangdiary.domain.usecase.diary

import com.example.hangdiary.data.repository.DiaryRepository
import javax.inject.Inject

/**
 * 切换日记顶置状态用例
 * 封装切换日记顶置状态的业务逻辑
 */
class TogglePinDiaryUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    
    /**
     * 切换日记的顶置状态
     * @param diaryId 日记ID
     * @param isPinned 是否顶置
     * @return 更新的行数
     */
    suspend operator fun invoke(diaryId: Long, isPinned: Boolean): Int {
        return repository.updatePinnedStatus(diaryId, isPinned)
    }
}