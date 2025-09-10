package com.example.hangdiary.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
        // 数据库迁移：从版本5到版本6，添加color字段支持
        val MIGRATION_5_6 = object : Migration(5, 6) {
            @SuppressLint("Range")
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    // 检查color列是否已存在
                    val cursor = database.query("SELECT * FROM sqlite_master WHERE type='table' AND name='diaries'")
                    if (cursor.count > 0) {
                        cursor.moveToFirst()
                        val sql = cursor.getString(cursor.getColumnIndex("sql"))
                        cursor.close()
                        
                        // 如果SQL中不包含color列，则添加它
                        if (!sql.contains("color")) {
                            database.execSQL("ALTER TABLE diaries ADD COLUMN color TEXT")
                        }
                    }
                } catch (e: Exception) {
                    // 如果出错，尝试直接添加列
                    try {
                        database.execSQL("ALTER TABLE diaries ADD COLUMN color TEXT")
                    } catch (ex: Exception) {
                        // 如果仍然出错，忽略错误，因为列可能已经存在
                    }
                }
            }
        }
        
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "hang_diary_database"
        )
            .addMigrations(MIGRATION_5_6) // 添加迁移逻辑
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

    /**
     * 提供SettingsDao实例
     * @param database AppDatabase实例
     * @return SettingsDao实例
     */
    @Provides
    fun provideSettingsDao(database: AppDatabase) = database.settingsDao()
    
    /**
     * 提供DatabaseInitializer实例
     * @param context 应用上下文
     * @param diaryRepository 日记仓库
     * @return DatabaseInitializer实例
     */
    @Provides
    @Singleton
    fun provideDatabaseInitializer(
        @ApplicationContext context: Context,
        diaryRepository: com.example.hangdiary.data.repository.DiaryRepository
    ): com.example.hangdiary.data.DatabaseInitializer {
        return com.example.hangdiary.data.DatabaseInitializer(context, diaryRepository)
    }
}

