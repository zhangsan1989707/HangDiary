package com.example.hangdiary.domain.usecase.diary

import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.data.repository.DiaryRepository
import com.example.hangdiary.data.repository.TagRepository
import javax.inject.Inject
import java.io.File
import java.time.LocalDateTime
import androidx.compose.ui.graphics.Color

/**
 * 导入日记用例
 * 封装从文件导入日记的业务逻辑
 */
class ImportDiaryUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val tagRepository: TagRepository
) {
    
    /**
     * 从文件导入日记
     * @param importPath 导入文件路径
     * @return 导入的日记ID，如果导入失败则为-1
     */
    suspend operator fun invoke(importPath: String): Long {
        try {
            val fileContent = File(importPath).readText()
            
            // 简单的解析逻辑，提取标题、内容和标签
            val lines = fileContent.lines()
            val title = if (lines.isNotEmpty() && lines[0].startsWith("# ")) {
                lines[0].substring(2)
            } else {
                "导入的日记"
            }
            
            val contentBuilder = StringBuilder()
            var tagsSection = ""
            
            var inContent = false
            for (line in lines.drop(1)) {
                if (line.startsWith("标签: ")) {
                    tagsSection = line.substring(4)
                    inContent = false
                } else if (line.startsWith("创建时间: ") || line.startsWith("更新时间: ")) {
                    inContent = false
                } else if (line.isNotEmpty()) {
                    if (contentBuilder.isNotEmpty()) {
                        contentBuilder.append("\n")
                    }
                    contentBuilder.append(line)
                    inContent = true
                } else if (inContent) {
                    contentBuilder.append("\n")
                }
            }
            
            val diary = Diary(
                title = title,
                content = contentBuilder.toString(),
                color = null,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            val diaryId = diaryRepository.insert(diary)
            
            // 处理标签
            if (tagsSection.isNotEmpty()) {
                val tagNames = tagsSection.split(", ")
                for (tagName in tagNames) {
                    val existingTag = tagRepository.getTagByName(tagName)
                    val tagId = existingTag?.id ?: tagRepository.createTag(tagName, Color.Blue.hashCode())
                    tagRepository.addTagToDiary(diaryId, tagId)
                }
            }
            
            return diaryId
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }
}