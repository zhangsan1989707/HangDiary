package com.example.hangdiary.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 日记-标签关联表
 * 支持多对多关系：一篇日记可以有多个标签，一个标签可以用于多篇日记
 */
@Entity(
    tableName = "diary_tags",
    foreignKeys = [
        ForeignKey(
            entity = Diary::class,
            parentColumns = ["id"],
            childColumns = ["diaryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("diaryId"), Index("tagId")]
)
data class DiaryTagCrossRef(
    /** 日记ID */
    val diaryId: Long,
    
    /** 标签ID */
    val tagId: Long,
    
    /** 关联ID，主键 */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)