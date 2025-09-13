package com.example.hangdiary.domain.usecase.diary

import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.repository.DiaryRepository
import javax.inject.Inject

/**
 * 删除日记用例
 * 封装删除日记的业务逻辑
 */
class DeleteDiaryUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    
    /**
     * 删除日记
     * @param diary 要删除的日记对象
     * @return 删除的行数
     */
    suspend operator fun invoke(diary: Diary): Int {
        return repository.delete(diary)
    }
}