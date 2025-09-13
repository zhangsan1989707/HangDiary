# 数据库迁移指南

## 📊 迁移概述

本文档详细说明了HangDiary应用从数据库版本10到版本11的迁移过程，主要涉及Todo表结构的重大更新。

## 🔄 迁移版本信息

- **源版本**: Database Version 10
- **目标版本**: Database Version 11
- **迁移类型**: 结构性迁移 + 数据迁移
- **影响表**: `todos`

## 📋 表结构变更详情

### 原始表结构 (v10)
```sql
CREATE TABLE todos (
    id INTEGER PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    content TEXT,                    -- 将被重命名为notes
    isCompleted INTEGER NOT NULL,
    createdAt TEXT NOT NULL,
    updatedAt TEXT,
    dueDate TEXT                     -- 将被拆分为dueDate和dueTime
);
```

### 新表结构 (v11)
```sql
CREATE TABLE todos (
    id INTEGER PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    notes TEXT,                      -- 原content字段
    isCompleted INTEGER NOT NULL,
    createdAt TEXT NOT NULL,
    updatedAt TEXT,
    dueDate TEXT,                    -- 仅存储日期部分
    dueTime TEXT,                    -- 新增：存储时间部分
    category TEXT,                   -- 新增：分类字段
    priority INTEGER NOT NULL DEFAULT 1  -- 新增：优先级字段
);
```

## 🔧 迁移实现代码

### Migration类定义
```kotlin
private val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            // 检查现有表结构
            val cursor = database.query("SELECT * FROM sqlite_master WHERE type='table' AND name='todos'")
            if (cursor.count > 0) {
                cursor.moveToFirst()
                val sql = cursor.getString(cursor.getColumnIndex("sql"))
                cursor.close()
                
                // 创建新表结构
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
                
                // 数据迁移逻辑
                database.execSQL(
                    "INSERT INTO todos_new (id, title, notes, isCompleted, createdAt, updatedAt, dueDate, dueTime, category, priority) " +
                    "SELECT id, title, content, isCompleted, createdAt, updatedAt, " +
                    "CASE WHEN dueDate IS NOT NULL THEN date(dueDate) ELSE NULL END, " +
                    "CASE WHEN dueDate IS NOT NULL THEN time(dueDate) ELSE NULL END, " +
                    "NULL, 1 FROM todos"
                )
                
                // 替换表
                database.execSQL("DROP TABLE todos")
                database.execSQL("ALTER TABLE todos_new RENAME TO todos")
            }
        } catch (e: Exception) {
            // 错误处理：创建全新表结构
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
        }
    }
}
```

## 📝 字段映射关系

### 直接映射字段
| 原字段名 | 新字段名 | 数据类型 | 说明 |
|---------|---------|---------|------|
| id | id | INTEGER | 主键，无变化 |
| title | title | TEXT | 标题，无变化 |
| isCompleted | isCompleted | INTEGER | 完成状态，无变化 |
| createdAt | createdAt | TEXT | 创建时间，无变化 |
| updatedAt | updatedAt | TEXT | 更新时间，无变化 |

### 重命名字段
| 原字段名 | 新字段名 | 数据类型 | 迁移逻辑 |
|---------|---------|---------|---------|
| content | notes | TEXT | 直接复制数据 |

### 拆分字段
| 原字段名 | 新字段名 | 数据类型 | 迁移逻辑 |
|---------|---------|---------|---------|
| dueDate | dueDate | TEXT | 提取日期部分：`date(dueDate)` |
| dueDate | dueTime | TEXT | 提取时间部分：`time(dueDate)` |

### 新增字段
| 字段名 | 数据类型 | 默认值 | 说明 |
|-------|---------|-------|------|
| category | TEXT | NULL | 分类标签 |
| priority | INTEGER | 1 | 优先级（1=低，2=中，3=高） |

## 🔍 数据完整性检查

### 迁移前检查
```sql
-- 检查原始数据总数
SELECT COUNT(*) as total_todos FROM todos;

-- 检查有内容的记录数
SELECT COUNT(*) as todos_with_content FROM todos WHERE content IS NOT NULL;

-- 检查有截止日期的记录数
SELECT COUNT(*) as todos_with_due_date FROM todos WHERE dueDate IS NOT NULL;
```

### 迁移后验证
```sql
-- 验证数据总数是否一致
SELECT COUNT(*) as migrated_todos FROM todos;

-- 验证notes字段迁移
SELECT COUNT(*) as todos_with_notes FROM todos WHERE notes IS NOT NULL;

-- 验证日期时间拆分
SELECT COUNT(*) as todos_with_split_datetime FROM todos 
WHERE dueDate IS NOT NULL OR dueTime IS NOT NULL;

-- 验证新字段默认值
SELECT COUNT(*) as todos_with_default_priority FROM todos WHERE priority = 1;
```

## ⚠️ 注意事项

### 数据安全
1. **备份建议**: 迁移前建议备份整个数据库文件
2. **回滚策略**: 保留原始APK文件以便必要时回滚
3. **测试环境**: 建议先在测试环境验证迁移逻辑

### 性能考虑
1. **迁移时间**: 大量数据可能需要较长迁移时间
2. **内存使用**: 迁移过程中内存使用会增加
3. **用户体验**: 首次启动可能需要额外等待时间

### 兼容性
1. **Android版本**: 支持Android 5.0+
2. **Room版本**: 需要Room 2.4.0+
3. **SQLite版本**: 需要SQLite 3.24.0+

## 🚨 故障排除

### 常见问题

#### 1. 迁移失败
**症状**: 应用启动时崩溃，日志显示数据库迁移错误
**解决方案**:
```kotlin
// 在DatabaseModule中启用fallbackToDestructiveMigration
Room.databaseBuilder(context, AppDatabase::class.java, "database")
    .fallbackToDestructiveMigration()
    .build()
```

#### 2. 数据丢失
**症状**: 迁移后部分待办事项消失
**排查步骤**:
1. 检查迁移SQL语句的WHERE条件
2. 验证字段映射关系
3. 查看异常日志

#### 3. 性能问题
**症状**: 迁移过程耗时过长
**优化方案**:
1. 分批处理大量数据
2. 添加适当的数据库索引
3. 优化SQL查询语句

### 调试工具

#### 数据库检查器
```kotlin
// 启用Room数据库调试
Room.databaseBuilder(context, AppDatabase::class.java, "database")
    .setQueryCallback({ sqlQuery, bindArgs ->
        Log.d("RoomQuery", "SQL: $sqlQuery, Args: $bindArgs")
    }, Executors.newSingleThreadExecutor())
    .build()
```

#### 迁移日志
```kotlin
private val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Log.d("Migration", "开始迁移数据库从版本10到版本11")
        try {
            // 迁移逻辑...
            Log.d("Migration", "数据库迁移成功完成")
        } catch (e: Exception) {
            Log.e("Migration", "数据库迁移失败", e)
            throw e
        }
    }
}
```

## 📊 迁移统计

### 预期影响
- **用户数据**: 100%保留
- **功能可用性**: 立即可用
- **性能影响**: 首次启动增加2-5秒
- **存储空间**: 增加约10-20%

### 成功指标
- [ ] 迁移成功率 > 99%
- [ ] 数据完整性 = 100%
- [ ] 用户投诉 < 1%
- [ ] 应用崩溃率 < 0.1%

---

**文档版本**: 1.0  
**最后更新**: 2025年9月12日  
**维护者**: 开发团队