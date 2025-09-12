package com.example.hangdiary.data.util

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.LocalTime
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

    /**
     * 将LocalDate转换为String
     * @param value 要转换的LocalDate对象
     * @return 转换后的字符串
     */
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    /**
     * 将String转换为LocalDate
     * @param value 要转换的字符串
     * @return 转换后的LocalDate对象
     */
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {
            LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }

    /**
     * 将LocalTime转换为String
     * @param value 要转换的LocalTime对象
     * @return 转换后的字符串
     */
    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? {
        return value?.format(DateTimeFormatter.ISO_LOCAL_TIME)
    }

    /**
     * 将String转换为LocalTime
     * @param value 要转换的字符串
     * @return 转换后的LocalTime对象
     */
    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let {
            LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
        }
    }
}