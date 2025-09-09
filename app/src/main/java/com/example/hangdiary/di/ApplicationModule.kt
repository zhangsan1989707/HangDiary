package com.example.hangdiary.di

import com.example.hangdiary.data.dao.DiaryDao
import com.example.hangdiary.data.dao.DiaryTagDao
import com.example.hangdiary.data.dao.SettingsDao
import com.example.hangdiary.data.dao.TagDao
import com.example.hangdiary.data.dao.TodoDao
import com.example.hangdiary.data.repository.DiaryRepository
import com.example.hangdiary.data.repository.SettingsRepository
import com.example.hangdiary.data.repository.TagRepository
import com.example.hangdiary.data.repository.TodoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 仓库依赖注入模块
 * 提供各种Repository实例
 */
@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    /**
     * 提供DiaryRepository实例
     * @param diaryDao DiaryDao实例
     * @return DiaryRepository实例
     */
    @Provides
    @Singleton
    fun provideDiaryRepository(diaryDao: DiaryDao): DiaryRepository {
        return DiaryRepository(diaryDao)
    }

    /**
     * 提供TagRepository实例
     * @param tagDao TagDao实例
     * @param diaryTagDao DiaryTagDao实例
     * @return TagRepository实例
     */
    @Provides
    @Singleton
    fun provideTagRepository(tagDao: TagDao, diaryTagDao: DiaryTagDao): TagRepository {
        return TagRepository(tagDao, diaryTagDao)
    }

    /**
     * 提供TodoRepository实例
     * @param todoDao TodoDao实例
     * @return TodoRepository实例
     */
    @Provides
    @Singleton
    fun provideTodoRepository(todoDao: TodoDao): TodoRepository {
        return TodoRepository(todoDao)
    }

    /**
     * 提供SettingsRepository实例
     * @param settingsDao SettingsDao实例
     * @return SettingsRepository实例
     */
    @Provides
    @Singleton
    fun provideSettingsRepository(settingsDao: SettingsDao): SettingsRepository {
        return SettingsRepository(settingsDao)
    }
}