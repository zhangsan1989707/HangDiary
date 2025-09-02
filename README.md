# HangDiary - Android日记应用

HangDiary是一款简洁优雅的Android日记应用，基于Jetpack Compose构建，支持记录日记、管理待办事项，并提供标签和分类管理功能。

## 功能特性

- 📝 **日记记录**：轻松创建和编辑日记，支持标题、内容和标签
- 🏷️ **标签管理**：为日记添加自定义标签，方便分类和筛选
- 📂 **分类系统**：通过分类整理日记，提高管理效率
- 🔍 **搜索功能**：快速查找特定内容的日记
- ⭐ **收藏功能**：标记重要的日记为收藏
- 📌 **顶置功能**：将重要日记顶置在列表顶部
- ✅ **待办事项**：管理个人任务和待办事项
- 🖼️ **图片支持**：在日记中添加图片
- 🌤️ **心情和天气**：记录日记时的心情和天气状态

## 技术栈

- **开发语言**：Kotlin
- **UI框架**：Jetpack Compose
- **架构模式**：MVVM (Model-View-ViewModel)
- **数据库**：Room
- **依赖注入**：Hilt
- **异步处理**：Kotlin Coroutines
- **导航**：Jetpack Navigation Compose
- **图片加载**：Coil
- **JSON处理**：Gson
- **构建系统**：Gradle (Kotlin DSL)

## 项目结构

```
app/src/main/
├── java/com/example/hangdiary/
│   ├── data/                   # 数据层
│   │   ├── dao/                # 数据访问对象
│   │   ├── model/              # 数据模型
│   │   ├── repository/         # 数据仓库
│   │   └── util/               # 数据工具类
│   ├── di/                     # 依赖注入
│   ├── ui/                     # UI层
│   │   ├── components/         # 可复用组件
│   │   ├── navigation/         # 导航
│   │   ├── screens/            # 屏幕界面
│   │   └── theme/              # 主题和样式
│   ├── viewmodel/              # 视图模型
│   ├── HangDiaryApplication.kt # 应用程序类
│   └── MainActivity.kt         # 主活动
└── res/                        # 资源文件
```

## 数据模型

- **Diary**: 日记实体，包含标题、内容、创建时间等信息
- **Category**: 分类实体，用于对日记进行分类
- **Tag**: 标签实体，可以添加到日记中
- **Todo**: 待办事项实体，包含标题、内容、完成状态等
- **DiaryTagCrossRef**: 日记和标签的多对多关联

## 主要界面

- **日记列表**: 显示所有日记，支持搜索、筛选和排序
- **日记详情**: 查看和编辑日记内容
- **分类管理**: 创建和管理日记分类
- **待办事项**: 管理个人任务和待办事项
- **标签管理**: 创建和管理标签

## 快速开始

### 前提条件

- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 11 或更高版本
- Android SDK 24+ (Android 7.0+)

### 构建和运行

1. 克隆项目
   ```bash
   git clone https://github.com/zhangsan1989707/HangDiary.git
   ```

2. 在Android Studio中打开项目

3. 等待Gradle同步完成

4. 点击运行按钮或使用快捷键 `Shift+F10` 构建并安装应用

## 使用指南

### 创建日记
1. 在主界面点击右下角的 "+" 按钮
2. 输入标题和内容
3. 可选择添加分类、标签、心情和天气
4. 点击保存按钮完成创建

### 管理待办事项
1. 在主界面点击右下角的待办事项按钮
2. 创建新的待办事项
3. 标记完成或删除已完成的待办事项

### 使用标签和分类
1. 通过侧边栏访问分类和标签
2. 创建新的分类和标签
3. 为日记添加标签和分类
4. 使用标签和分类筛选日记

## 贡献指南

1. Fork 项目仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 许可证

[MIT](LICENSE) - 详细信息请查看LICENSE文件

## 联系方式

如有问题或建议，请通过以下方式联系：

- GitHub: [https://github.com/zhangsan1989707/HangDiary](https://github.com/zhangsan1989707/HangDiary)