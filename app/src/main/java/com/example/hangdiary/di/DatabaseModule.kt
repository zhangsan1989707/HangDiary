package com.example.hangdiary.di

import android.content.Context
import androidx.room.Room
import com.example.hangdiary.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库依赖注入模块
 * 提供AppDatabase实例
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * 提供AppDatabase实例
     * @param context 应用上下文
     * @return AppDatabase实例
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "hang_diary_database"
        )
            .fallbackToDestructiveMigration() // 数据库版本升级时销毁旧数据
            .build()
    }

    /**
     * 提供DiaryDao实例
     * @param database AppDatabase实例
     * @return DiaryDao实例
     */
    @Provides
    fun provideDiaryDao(database: AppDatabase) = database.diaryDao()

    /**
     * 提供CategoryDao实例
     * @param database AppDatabase实例
     * @return CategoryDao实例
     */
    @Provides
    fun provideCategoryDao(database: AppDatabase) = database.categoryDao()

    /**
     * 提供TodoDao实例
     * @param database AppDatabase实例
     * @return TodoDao实例
     */
    @Provides
    fun provideTodoDao(database: AppDatabase) = database.todoDao()

    /**
     * 提供TagDao实例
     * @param database AppDatabase实例
     * @return TagDao实例
     */
    @Provides
    fun provideTagDao(database: AppDatabase) = database.tagDao()

    /**
     * 提供DiaryTagDao实例
     * @param database AppDatabase实例
     * @return DiaryTagDao实例
     */
    @Provides
    fun provideDiaryTagDao(database: AppDatabase) = database.diaryTagDao()
}