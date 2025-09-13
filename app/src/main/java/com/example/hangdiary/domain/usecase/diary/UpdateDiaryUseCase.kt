package com.example.hangdiary.domain.usecase.diary

import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.repository.DiaryRepository
import javax.inject.Inject
import java.time.LocalDateTime

/**
 * 更新日记用例
 * 封装更新现有日记的业务逻辑
 */
class UpdateDiaryUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    
    /**
     * 更新日记
     * @param diary 要更新的日记对象
     * @return 更新的行数
     */
    suspend operator fun invoke(diary: Diary): Int {
        // 更新时间戳
        val updatedDiary = diary.copy(updatedAt = LocalDateTime.now())
        return repository.update(updatedDiary)
    }
}