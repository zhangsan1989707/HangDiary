# 设置Gradle缓存目录为项目内的.gradle_cache
$env:GRADLE_USER_HOME = "$PSScriptRoot\.gradle_cache"

# 输出使用的缓存目录信息
Write-Host "使用自定义Gradle缓存目录: $env:GRADLE_USER_HOME"

# 运行Gradle构建命令，添加--no-daemon和--stacktrace参数以获取详细错误信息
& '.\gradlew' clean assembleDebug --no-daemon --stacktrace