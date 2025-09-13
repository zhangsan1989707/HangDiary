package com.example.hangdiary.data.model

/**
 * 日记与标签的组合数据类
 * 用于同时表示日记及其关联的标签列表
 */
data class DiaryWithTags(
    val diary: Diary,
    val tags: List<Tag>
)