package com.example.hangdiary.domain.usecase.diary

import com.example.hangdiary.domain.usecase.diary.GetDiariesUseCase
import com.example.hangdiary.domain.usecase.diary.GetDiaryByIdUseCase
import com.example.hangdiary.domain.usecase.diary.CreateDiaryUseCase
import com.example.hangdiary.domain.usecase.diary.UpdateDiaryUseCase
import com.example.hangdiary.domain.usecase.diary.DeleteDiaryUseCase
import com.example.hangdiary.domain.usecase.diary.SearchDiariesUseCase
import com.example.hangdiary.domain.usecase.diary.GetDiariesByTagUseCase
import com.example.hangdiary.domain.usecase.diary.TogglePinDiaryUseCase
import com.example.hangdiary.domain.usecase.diary.UpdateDiaryColorUseCase
import com.example.hangdiary.domain.usecase.diary.ExportDiaryUseCase
import com.example.hangdiary.domain.usecase.diary.ImportDiaryUseCase

/**
 * 日记相关用例集合
 * 封装所有日记相关的业务逻辑
 */
data class DiaryUseCases(
    val getDiaries: GetDiariesUseCase,
    val getDiaryById: GetDiaryByIdUseCase,
    val createDiary: CreateDiaryUseCase,
    val updateDiary: UpdateDiaryUseCase,
    val deleteDiary: DeleteDiaryUseCase,
    val searchDiaries: SearchDiariesUseCase,
    val getDiariesByTag: GetDiariesByTagUseCase,
    val togglePinDiary: TogglePinDiaryUseCase,
    val updateDiaryColor: UpdateDiaryColorUseCase,
    val exportDiary: ExportDiaryUseCase,
    val importDiary: ImportDiaryUseCase
)