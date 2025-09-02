package com.example.hangdiary.data.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 字符串列表转换器
 * 用于Room数据库中List<String>类型与String类型的相互转换
 */
class StringListConverter {
    private val gson = Gson()
    private val type = object : TypeToken<List<String>>() {}.type

    /**
     * 将List<String>转换为JSON字符串
     * @param list 要转换的字符串列表
     * @return JSON格式的字符串
     */
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return gson.toJson(list)
    }

    /**
     * 将JSON字符串转换为List<String>
     * @param value JSON格式的字符串
     * @return 转换后的字符串列表
     */
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            gson.fromJson(value, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}