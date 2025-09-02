# LeoDiary - Android日记应用

HangDiary是一款简洁优雅的日记应用，支持记录日记、待办事项，并提供标签管理功能。

## 功能特性

- 📝 **日记记录**：轻松创建和编辑日记，支持标题、内容和标签
- 📅 **日历视图**：通过直观的日历界面查看和管理日记
- 🔍 **搜索功能**：快速查找特定内容的日记
- 🏷️ **标签管理**：为日记添加标签，方便分类和筛选
- ⭐ **收藏功能**：标记重要的日记为收藏
- 📊 **统计功能**：查看日记数量和写作习惯统计
- 👤 **个人资料**：用户信息管理和个性化设置

## 技术栈

- **开发语言**：Kotlin
- **Android SDK**：最低支持 29，目标SDK 33
- **构建系统**：Gradle 7.3.1
- **数据库**：Room 2.5.2
- **异步编程**：Kotlin Coroutines 1.6.4
- **UI组件**：AndroidX、Material Components
- **导航**：Navigation Component
- **图像处理**：CircleImageView
- **日期时间**：ThreeTenABP

## 项目结构

```
├── app/                          # 应用主模块
│   ├── src/main/java/            # 源代码目录
│   │   └── com/example/leodiary/ # 包名目录
│   │       ├── data/             # 数据层（Room数据库相关）
│   │       ├── ui/               # UI层（Activity、Fragment等）
│   │       └── ...               # 其他组件
│   ├── src/main/res/             # 资源文件
│   └── build.gradle              # 模块构建配置
├── .github/workflows/            # GitHub Actions工作流
│   └── build-apk.yml             # APK构建工作流
├── gradle/                       # Gradle包装器
├── docs/                         # 文档
│   └── github-actions-apk-build-guide.md # CI/CD构建指南
├── build.gradle                  # 项目级构建配置
├── settings.gradle               # 项目设置
└── .gitignore                    # Git忽略规则
```

## 快速开始

### 前提条件

- Android Studio 最新稳定版
- Android SDK 29+ (Android 10+)
- JDK 17
- Gradle 7.3.1

### 构建和运行

1. 克隆项目
   ```bash
   git clone https://github.com/[your-username]/LeoDiary.git
   ```

2. 打开Android Studio并导入项目

3. 等待Gradle同步完成

4. 点击运行按钮或使用快捷键 `Shift+F10` 构建并安装应用

## 使用指南

### 创建日记
1. 在主界面点击悬浮按钮 (+) 
2. 输入标题、内容和标签
3. 点击保存按钮完成创建

### 查看和编辑日记
1. 在列表或日历视图中点击想要查看的日记
2. 点击编辑按钮进行修改
3. 修改完成后点击保存

### 使用搜索功能
1. 在主界面点击搜索图标
2. 输入关键词进行搜索
3. 查看搜索结果

### 使用标签筛选
1. 在搜索结果中点击标签进行筛选
2. 或者在特定界面使用标签过滤功能

## GitHub Actions CI/CD

该项目配置了GitHub Actions自动构建APK文件。每当代码推送到master分支时，工作流会自动执行以下步骤：

1. 检查代码
2. 设置JDK 17环境
3. 构建调试和发布版本的APK
4. 上传构建产物

详细配置请参阅 [GitHub Actions APK构建指南](docs/github-actions-apk-build-guide.md)

## 注意事项

1. 应用使用Room数据库本地存储日记数据
2. 数据仅存储在设备本地，不会自动同步到云端
3. 为保证数据安全，建议定期备份应用数据
4. 当前工作流配置使用JDK 17，以兼容GitHub Actions环境中的Gradle 9.0.0

## 贡献指南

1. Fork 项目仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 许可证

[MIT](LICENSE) - 详细信息请查看LICENSE文件

## 致谢

感谢所有为这个项目做出贡献的开发者和设计师。

## 联系我们

如有问题或建议，请通过以下方式联系我：

- GitHub: [https://github.com/[zhangsan1989707]/LeoDiary](https://github.com/[zhangsan1989707]/LeoDiary)