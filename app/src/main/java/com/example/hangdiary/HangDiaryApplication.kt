package com.example.hangdiary

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * HangDiary应用程序类
 * 使用@HiltAndroidApp注解启用Hilt依赖注入
 */
@HiltAndroidApp
class HangDiaryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 应用程序初始化逻辑
    }
}