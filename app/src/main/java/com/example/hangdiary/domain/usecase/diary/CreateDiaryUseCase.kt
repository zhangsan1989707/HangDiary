package com.example.hangdiary.domain.usecase.diary

import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.repository.DiaryRepository
import javax.inject.Inject
import java.time.LocalDateTime

/**
 * 创建日记用例
 * 封装创建新日记的业务逻辑
 */
class CreateDiaryUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    
    /**
     * 创建新日记
     * @param title 日记标题
     * @param content 日记内容
     * @param color 日记颜色（可选）
     * @return 新创建日记的ID
     */
    suspend operator fun invoke(title: String, content: String, color: String? = null): Long {
        val diary = Diary(
            title = title,
            content = content,
            color = color,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        return repository.insert(diary)
    }
}