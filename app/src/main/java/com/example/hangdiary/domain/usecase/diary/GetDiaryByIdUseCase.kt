package com.example.hangdiary.domain.usecase.diary

import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.repository.DiaryRepository
import javax.inject.Inject

/**
 * 根据ID获取日记用例
 * 封装根据ID查询单篇日记的业务逻辑
 */
class GetDiaryByIdUseCase @Inject constructor(
    private val repository: DiaryRepository
) {
    
    /**
     * 根据ID获取日记
     * @param id 日记ID
     * @return 日记对象，如果不存在则为null
     */
    suspend operator fun invoke(id: Long): Diary? {
        return repository.getDiaryById(id)
    }
}