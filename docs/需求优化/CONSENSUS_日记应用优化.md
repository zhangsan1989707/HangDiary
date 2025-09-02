# 日记应用优化需求共识文档

## 最终需求描述

### 需求1: 导航抽屉功能 ✅
**具体实现**:
- 使用`ModalNavigationDrawer`替换现有的DropdownMenu
- 抽屉菜单项：
  - "全部日记" - 显示所有日记列表
  - "标签管理" - 管理全局标签
  - "待办事项" - 显示待办列表
  - "设置" - 应用设置页面
- 支持手势从左侧滑出和关闭

### 需求2: 待办功能 ✅
**具体实现**:
- 新增`Todo`数据实体和数据库表
- 在日记列表右下角添加待办悬浮按钮
- 支持创建、编辑、标记完成/未完成
- 待办界面使用独立路由`/todos`

### 需求3: 日记简化 ✅
**具体实现**:
- 移除UI中的心情、分类、天气输入字段
- 保留数据库字段避免数据丢失
- 新建日记界面仅显示：标题、内容输入框
- 标题下方自动显示当前日期格式："2024年12月19日星期四"

### 需求4: 标签功能 ✅
**具体实现**:
- 长按日记项弹出标签管理对话框
- 支持添加、移除多个标签
- 标签为全局共享，支持搜索和重用
- 数据库设计：Tag表 + DiaryTag关联表

### 需求5: 代码清理 ✅
**清理范围**:
- UI层：移除心情、分类、天气相关组件
- ViewModel：移除相关逻辑处理
- Repository：移除相关数据操作
- 保留数据库字段确保向后兼容

### 需求6: README文档 ✅
**文档内容**:
- 项目简介和特色功能
- 技术栈和架构说明
- 安装和使用指南
- 功能演示截图
- 开发规范和贡献指南

## 技术实现方案

### 架构调整
```
现有架构：
MainActivity
├── AppNavGraph
│   ├── DiaryListScreen
│   ├── DiaryDetailScreen
│   └── CategoryScreen

新增架构：
MainActivity
├── AppNavGraph
│   ├── DiaryListScreen (带NavigationDrawer)
│   ├── DiaryDetailScreen (简化版)
│   ├── TodoListScreen (新增)
│   ├── TagManagementScreen (新增)
│   └── SettingsScreen (新增)
```

### 数据库变更
```sql
-- 新增Todo表
CREATE TABLE todos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    content TEXT,
    isCompleted INTEGER NOT NULL DEFAULT 0,
    createdAt TEXT NOT NULL,
    updatedAt TEXT
);

-- 新增Tag表
CREATE TABLE tags (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    color INTEGER,
    createdAt TEXT NOT NULL
);

-- 新增DiaryTag关联表
CREATE TABLE diary_tags (
    diaryId INTEGER NOT NULL,
    tagId INTEGER NOT NULL,
    PRIMARY KEY(diaryId, tagId),
    FOREIGN KEY(diaryId) REFERENCES diaries(id) ON DELETE CASCADE,
    FOREIGN KEY(tagId) REFERENCES tags(id) ON DELETE CASCADE
);
```

### 界面变更
1. **DiaryListScreen**: 添加NavigationDrawer和待办按钮
2. **DiaryDetailScreen**: 简化输入界面
3. **新增TodoListScreen**: 待办事项管理
4. **新增TagManagementScreen**: 标签管理
5. **新增SettingsScreen**: 应用设置

## 任务边界限制

### 明确不包含的内容
- 云同步功能
- 多用户支持
- 富文本编辑
- 图片编辑功能
- 数据导出/导入
- 主题切换功能

### 技术约束
- 最小SDK版本：API 26 (Android 8.0)
- 目标SDK版本：API 34 (Android 14)
- 使用Jetpack Compose 1.5+
- Room数据库版本：2.5+
- 保持单模块架构

## 验收标准

### 功能验收清单
- [ ] 左侧导航抽屉响应手势操作
- [ ] 抽屉菜单四项功能全部可用
- [ ] 待办功能完整CRUD操作
- [ ] 日记创建界面仅含标题和内容
- [ ] 长按日记项弹出标签管理
- [ ] 标签支持多选和搜索
- [ ] 代码清理无残留
- [ ] README文档完整

### 技术验收清单
- [ ] 项目编译成功无警告
- [ ] 所有现有功能正常运行
- [ ] 新增功能单元测试通过
- [ ] 数据库迁移测试通过
- [ ] 界面适配不同屏幕尺寸
- [ ] 暗色主题支持

### 用户体验标准
- [ ] 导航流畅，动画自然
- [ ] 操作反馈及时
- [ ] 错误处理友好
- [ ] 加载状态明确
- [ ] 空状态提示清晰

## 关键决策确认

### 已确认的技术决策
1. **数据库策略**: 保留旧字段，新增独立表
2. **导航方案**: 使用NavigationDrawer而非BottomBar
3. **标签系统**: 全局标签 + 多对多关联
4. **待办分离**: 独立Todo实体，不与日记混合
5. **向后兼容**: 不破坏现有用户数据

### 风险评估
- **低风险**: UI调整、新增功能
- **中风险**: 数据库结构变更
- **缓解措施**: 逐步迁移，保留备份

## 最终交付物

1. **代码变更**:
   - 核心功能实现代码
   - 数据库迁移脚本
   - 单元测试代码

2. **文档**:
   - README.md项目文档
   - 功能使用说明
   - 开发文档更新

3. **测试报告**:
   - 功能测试清单
   - 兼容性测试结果
   - 性能测试报告