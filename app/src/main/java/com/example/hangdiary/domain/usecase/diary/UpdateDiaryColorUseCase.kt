package com.example.hangdiary.domain.usecase.diary

import com.example.hangdiary.data.repository.DiaryRepository
import javax.inject.Inject

/**
 * 更新日记颜色用例
 * 封装更新日记颜色的业务逻辑
 */
class UpdateDiaryColorUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    
    /**
     * 更新日记的颜色
     * @param diaryId 日记ID
     * @param color 新的颜色值（可以为null表示清除颜色）
     * @return 更新的行数
     */
    suspend operator fun invoke(diaryId: Long, color: String?): Int {
        // 先检查DiaryRepository是否有updateDiaryColor方法
        // 如果没有，我们需要在后续步骤中添加这个方法
        return repository.updateDiaryColor(diaryId, color)
    }
}