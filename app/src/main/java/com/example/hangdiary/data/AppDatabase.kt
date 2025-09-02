package com.example.hangdiary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.hangdiary.data.dao.*
import com.example.hangdiary.data.model.*
import com.example.hangdiary.data.util.LocalDateTimeConverter
import com.example.hangdiary.data.util.StringListConverter

/**
 * 应用数据库类
 * 管理Room数据库实例和版本
 */
@Database(
    entities = [
        Diary::class, 
        Category::class,
        Todo::class,
        Tag::class,
        DiaryTagCrossRef::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(LocalDateTimeConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    // 获取日记DAO
    abstract fun diaryDao(): DiaryDao

    // 获取分类DAO
    abstract fun categoryDao(): CategoryDao

    // 获取待办DAO
    abstract fun todoDao(): TodoDao

    // 获取标签DAO
    abstract fun tagDao(): TagDao

    // 获取日记标签关联DAO
    abstract fun diaryTagDao(): DiaryTagDao

    companion object {
        // 单例实例
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * 获取数据库实例
         * @param context 上下文
         * @return 数据库实例
         */
        fun getDatabase(context: Context): AppDatabase {
            // 如果实例已存在，直接返回
            return INSTANCE ?: synchronized(this) {
                // 双重检查锁定
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hang_diary_database"
                )
                    .fallbackToDestructiveMigration() // 数据库版本升级时销毁旧数据
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}