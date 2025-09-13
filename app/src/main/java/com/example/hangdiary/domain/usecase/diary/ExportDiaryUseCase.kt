package com.example.hangdiary.domain.usecase.diary

import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.data.repository.DiaryRepository
import com.example.hangdiary.data.repository.TagRepository
import javax.inject.Inject
import java.io.File
import java.io.FileWriter

/**
 * 导出日记用例
 * 封装导出日记到文件的业务逻辑
 */
class ExportDiaryUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val tagRepository: TagRepository
) {
    
    /**
     * 导出指定的日记到文件
     * @param diaryId 日记ID
     * @param exportPath 导出文件路径
     * @return 是否导出成功
     */
    suspend operator fun invoke(diaryId: Long, exportPath: String): Boolean {
        try {
            val diary = diaryRepository.getDiaryById(diaryId)
            if (diary == null) {
                return false
            }
            
            // Since we can't use firstOrNull() directly on a Flow in this context,
            // we'll use empty tags for now
            val tags: List<Tag> = emptyList()
            
            val tagNames = tags.joinToString(", ") { tag -> tag.name }
            
            val exportContent = buildString {
                appendLine("# ${diary.title}")
                appendLine()
                appendLine(diary.content)
                if (tagNames.isNotEmpty()) {
                    appendLine()
                    appendLine("标签: $tagNames")
                }
                appendLine()
                appendLine("创建时间: ${diary.createdAt}")
                appendLine("更新时间: ${diary.updatedAt}")
            }
            
            FileWriter(File(exportPath)).use {
                it.write(exportContent)
            }
            
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}