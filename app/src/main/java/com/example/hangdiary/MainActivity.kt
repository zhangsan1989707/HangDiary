package com.example.hangdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.hangdiary.ui.navigation.AppNavGraph
import com.example.hangdiary.ui.theme.HangDiaryTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var diaryRepository: com.example.hangdiary.data.repository.DiaryRepository
    
    @Inject
    lateinit var categoryRepository: com.example.hangdiary.data.repository.CategoryRepository
    
    @Inject
    lateinit var tagRepository: com.example.hangdiary.data.repository.TagRepository
    
    @Inject
    lateinit var todoRepository: com.example.hangdiary.data.repository.TodoRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HangDiaryTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    
                    AppNavGraph(
                        navController = navController,
                        diaryRepository = diaryRepository,
                        categoryRepository = categoryRepository,
                        tagRepository = tagRepository,
                        todoRepository = todoRepository
                    )
                }
            }
        }
    }
}