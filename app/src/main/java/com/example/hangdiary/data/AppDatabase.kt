package com.example.hangdiary.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.TypeConverters
import com.example.hangdiary.data.dao.DiaryDao
import com.example.hangdiary.data.dao.DiaryTagDao
import com.example.hangdiary.data.dao.SettingsDao
import com.example.hangdiary.data.dao.TagDao
import com.example.hangdiary.data.dao.TodoDao
import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.model.DiaryTagCrossRef
import com.example.hangdiary.data.model.Settings
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.data.model.Todo
import com.example.hangdiary.data.util.LocalDateTimeConverter
import com.example.hangdiary.data.util.StringListConverter

/**
 * 应用数据库类
 * 管理Room数据库实例和版本
 */
@Database(
    entities = [
        Diary::class, 
        Todo::class,
        Tag::class,
        DiaryTagCrossRef::class,
        Settings::class
    ],
    version = 11,
    exportSchema = false
)
@TypeConverters(LocalDateTimeConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    // 获取日记DAO
    abstract fun diaryDao(): DiaryDao

    // 获取待办DAO
    abstract fun todoDao(): TodoDao

    // 获取标签DAO
    abstract fun tagDao(): TagDao

    // 获取日记标签关联DAO
    abstract fun diaryTagDao(): DiaryTagDao

    // 获取设置DAO
    abstract fun settingsDao(): SettingsDao

    companion object {
        // 单例实例
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 数据库迁移：从版本5到版本6，添加color字段支持
        private val MIGRATION_5_6 = object : Migration(5, 6) {
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

        // 数据库迁移：从版本6到版本7，添加Settings表支持
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    // 检查settings表是否已存在
                    val cursor = database.query("SELECT * FROM sqlite_master WHERE type='table' AND name='settings'")
                    if (cursor.count == 0) {
                        cursor.close()
                        // 创建settings表
                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS settings (" +
                            "id INTEGER PRIMARY KEY NOT NULL, " +
                            "theme TEXT NOT NULL, " +
                            "view_mode TEXT NOT NULL, " +
                            "font_size INTEGER NOT NULL, " +
                            "created_at TEXT NOT NULL, " +
                            "updated_at TEXT NOT NULL)"
                        )
                        
                        // 插入默认设置
                        database.execSQL(
                            "INSERT INTO settings (id, theme, view_mode, font_size, created_at, updated_at) " +
                            "VALUES (1, 'SYSTEM_DEFAULT', 'GRID', 16, datetime('now'), datetime('now'))"
                        )
                    } else {
                        cursor.close()
                    }
                } catch (e: Exception) {
                    // 如果出错，尝试直接创建表
                    try {
                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS settings (" +
                            "id INTEGER PRIMARY KEY NOT NULL, " +
                            "theme TEXT NOT NULL, " +
                            "view_mode TEXT NOT NULL, " +
                            "font_size INTEGER NOT NULL, " +
                            "created_at TEXT NOT NULL, " +
                            "updated_at TEXT NOT NULL)"
                        )
                        
                        // 插入默认设置
                        database.execSQL(
                            "INSERT INTO settings (id, theme, view_mode, font_size, created_at, updated_at) " +
                            "VALUES (1, 'SYSTEM_DEFAULT', 'GRID', 16, datetime('now'), datetime('now'))"
                        )
                    } catch (ex: Exception) {
                        // 如果仍然出错，忽略错误，因为表可能已经存在
                    }
                }
            }
        }

        // 数据库迁移：从版本7到版本8，更新Settings表移除theme字段
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    // 检查settings表是否存在theme字段
                    val cursor = database.query("SELECT * FROM sqlite_master WHERE type='table' AND name='settings'")
                    if (cursor.count > 0) {
                        cursor.moveToFirst()
                        val sql = cursor.getString(cursor.getColumnIndex("sql"))
                        cursor.close()
                        
                        // 如果SQL中包含theme列，则需要移除它
                        if (sql.contains("theme")) {
                            // 由于SQLite不支持直接删除列，我们需要创建新表并复制数据
                            database.execSQL(
                                "CREATE TABLE IF NOT EXISTS settings_new (" +
                                "id INTEGER PRIMARY KEY NOT NULL, " +
                                "view_mode TEXT NOT NULL, " +
                                "font_size INTEGER NOT NULL, " +
                                "is_dark_mode INTEGER NOT NULL, " +
                                "created_at TEXT NOT NULL, " +
                                "updated_at TEXT NOT NULL)"
                            )
                            
                            // 复制数据（排除theme列）
                        database.execSQL(
                            "INSERT INTO settings_new (id, view_mode, font_size, is_dark_mode, created_at, updated_at) " +
                            "SELECT id, view_mode, font_size, 0, created_at, updated_at FROM settings"
                        )
                            
                            // 删除旧表
                            database.execSQL("DROP TABLE settings")
                            
                            // 重命名新表
                            database.execSQL("ALTER TABLE settings_new RENAME TO settings")
                        }
                    }
                } catch (e: Exception) {
                    // 如果出错，尝试直接创建新表结构
                    try {
                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS settings (" +
                            "id INTEGER PRIMARY KEY NOT NULL, " +
                            "view_mode TEXT NOT NULL, " +
                            "font_size INTEGER NOT NULL, " +
                            "is_dark_mode INTEGER NOT NULL, " +
                            "created_at TEXT NOT NULL, " +
                            "updated_at TEXT NOT NULL)"
                        )
                        
                        // 插入默认设置
                        database.execSQL(
                            "INSERT INTO settings (id, view_mode, font_size, is_dark_mode, created_at, updated_at) " +
                            "VALUES (1, 'GRID', 16, 0, datetime('now'), datetime('now'))"
                        )
                    } catch (ex: Exception) {
                        // 如果仍然出错，忽略错误，因为表可能已经存在
                    }
                }
            }
        }

        // 数据库迁移：从版本9到版本10，移除diaries表中的mood、weather、location字段
        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    // 检查diaries表是否存在mood、weather、location字段
                    val cursor = database.query("SELECT * FROM sqlite_master WHERE type='table' AND name='diaries'")
                    if (cursor.count > 0) {
                        cursor.moveToFirst()
                        val sql = cursor.getString(cursor.getColumnIndex("sql"))
                        cursor.close()
                        
                        // 如果SQL中包含mood、weather、location任一列，则需要移除它们
                        if (sql.contains("mood") || sql.contains("weather") || sql.contains("location")) {
                            // 由于SQLite不支持直接删除列，我们需要创建新表并复制数据
                            database.execSQL(
                                "CREATE TABLE IF NOT EXISTS diaries_new (" +
                                "id INTEGER PRIMARY KEY NOT NULL, " +
                                "title TEXT NOT NULL, " +
                                "content TEXT NOT NULL, " +
                                "color TEXT, " +
                                "created_at TEXT NOT NULL, " +
                                "updated_at TEXT NOT NULL)"
                            )
                            
                            // 复制数据（排除mood、weather、location列）
                            database.execSQL(
                                "INSERT INTO diaries_new (id, title, content, color, created_at, updated_at) " +
                                "SELECT id, title, content, color, created_at, updated_at FROM diaries"
                            )
                            
                            // 删除旧表
                            database.execSQL("DROP TABLE diaries")
                            
                            // 重命名新表
                            database.execSQL("ALTER TABLE diaries_new RENAME TO diaries")
                        }
                    }
                } catch (e: Exception) {
                    // 如果出错，尝试直接创建新表结构
                    try {
                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS diaries_new (" +
                            "id INTEGER PRIMARY KEY NOT NULL, " +
                            "title TEXT NOT NULL, " +
                            "content TEXT NOT NULL, " +
                            "color TEXT, " +
                            "created_at TEXT NOT NULL, " +
                            "updated_at TEXT NOT NULL)"
                        )
                        
                        // 复制数据（排除mood、weather、location列）
                        database.execSQL(
                            "INSERT INTO diaries_new (id, title, content, color, created_at, updated_at) " +
                            "SELECT id, title, content, color, created_at, updated_at FROM diaries"
                        )
                        
                        // 删除旧表
                        database.execSQL("DROP TABLE diaries")
                        
                        // 重命名新表
                        database.execSQL("ALTER TABLE diaries_new RENAME TO diaries")
                    } catch (ex: Exception) {
                        // 如果仍然出错，忽略错误，因为表可能已经存在
                    }
                }
            }
        }

        // 数据库迁移：从版本10到版本11，更新Todo表结构
        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    // 检查todos表是否存在新字段
                    val cursor = database.query("SELECT * FROM sqlite_master WHERE type='table' AND name='todos'")
                    if (cursor.count > 0) {
                        cursor.moveToFirst()
                        val sql = cursor.getString(cursor.getColumnIndex("sql"))
                        cursor.close()
                        
                        // 创建新的todos表结构
                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS todos_new (" +
                            "id INTEGER PRIMARY KEY NOT NULL, " +
                            "title TEXT NOT NULL, " +
                            "notes TEXT, " +
                            "isCompleted INTEGER NOT NULL, " +
                            "createdAt TEXT NOT NULL, " +
                            "updatedAt TEXT, " +
                            "dueDate TEXT, " +
                            "dueTime TEXT, " +
                            "category TEXT, " +
                            "priority INTEGER NOT NULL DEFAULT 1)"
                        )
                        
                        // 复制现有数据，将content映射到notes
                        database.execSQL(
                            "INSERT INTO todos_new (id, title, notes, isCompleted, createdAt, updatedAt, dueDate, dueTime, category, priority) " +
                            "SELECT id, title, content, isCompleted, createdAt, updatedAt, " +
                            "CASE WHEN dueDate IS NOT NULL THEN date(dueDate) ELSE NULL END, " +
                            "CASE WHEN dueDate IS NOT NULL THEN time(dueDate) ELSE NULL END, " +
                            "NULL, 1 FROM todos"
                        )
                        
                        // 删除旧表
                        database.execSQL("DROP TABLE todos")
                        
                        // 重命名新表
                        database.execSQL("ALTER TABLE todos_new RENAME TO todos")
                    }
                } catch (e: Exception) {
                    // 如果出错，尝试直接创建新表结构
                    try {
                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS todos (" +
                            "id INTEGER PRIMARY KEY NOT NULL, " +
                            "title TEXT NOT NULL, " +
                            "notes TEXT, " +
                            "isCompleted INTEGER NOT NULL, " +
                            "createdAt TEXT NOT NULL, " +
                            "updatedAt TEXT, " +
                            "dueDate TEXT, " +
                            "dueTime TEXT, " +
                            "category TEXT, " +
                            "priority INTEGER NOT NULL DEFAULT 1)"
                        )
                    } catch (ex: Exception) {
                        // 如果仍然出错，忽略错误，因为表可能已经存在
                    }
                }
            }
        }

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
                    .addMigrations(MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_9_10, MIGRATION_10_11) // 添加迁移逻辑
                    .fallbackToDestructiveMigration() // 数据库版本升级时销毁旧数据
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

private fun RoomDatabase.Builder<AppDatabase>.fallbackToDestructiveMigration(bool: Boolean) {}
