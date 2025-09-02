package com.example.hangdiary.data.util

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 日期时间转换器
 * 用于Room数据库中LocalDateTime类型与String类型的相互转换
 */
class LocalDateTimeConverter {
    // 日期时间格式化器
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    /**
     * 将LocalDateTime转换为String
     * @param value 要转换的LocalDateTime对象
     * @return 转换后的字符串
     */
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    /**
     * 将String转换为LocalDateTime
     * @param value 要转换的字符串
     * @return 转换后的LocalDateTime对象
     */
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            LocalDateTime.parse(it, formatter)
        }
    }
}