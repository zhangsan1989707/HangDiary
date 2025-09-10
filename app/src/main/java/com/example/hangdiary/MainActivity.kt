package com.example.hangdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.hangdiary.ui.navigation.AppNavGraph
import com.example.hangdiary.ui.theme.HangDiaryTheme
import com.example.hangdiary.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var diaryRepository: com.example.hangdiary.data.repository.DiaryRepository
    
    @Inject
    lateinit var tagRepository: com.example.hangdiary.data.repository.TagRepository
    
    @Inject
    lateinit var todoRepository: com.example.hangdiary.data.repository.TodoRepository
    
    @Inject
    lateinit var settingsRepository: com.example.hangdiary.data.repository.SettingsRepository
    
    @Inject
    lateinit var databaseInitializer: com.example.hangdiary.data.DatabaseInitializer
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化数据库，插入示例数据
        databaseInitializer.initialize()
        
        // 禁用启动画面
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            // 主题状态管理
            val settingsViewModel: SettingsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            val settingsState = settingsViewModel.settingsState.collectAsStateWithLifecycle().value
            val darkTheme = remember { mutableStateOf(settingsState?.isDarkMode ?: false) }
            val coroutineScope = rememberCoroutineScope()
            
            // 监听设置变化并更新主题
            androidx.compose.runtime.LaunchedEffect(settingsState) {
                settingsState?.let { settings ->
                    darkTheme.value = settings.isDarkMode
                }
            }
            
            HangDiaryTheme(darkTheme = darkTheme.value) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    
                    AppNavGraph(
                        navController = navController,
                        diaryRepository = diaryRepository,
                        tagRepository = tagRepository,
                        todoRepository = todoRepository,
                        darkTheme = darkTheme
                    )
                }
            }
        }
    }
}
