package com.example.hangdiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.hangdiary.data.repository.DiaryRepository
import com.example.hangdiary.data.repository.TagRepository
import com.example.hangdiary.data.repository.TodoRepository

import com.example.hangdiary.ui.screens.DiaryDetailScreen
import com.example.hangdiary.ui.screens.DiaryListScreen
import com.example.hangdiary.ui.screens.TagManagementScreen
import com.example.hangdiary.ui.screens.TodoListScreen
import com.example.hangdiary.ui.screens.SettingsScreen
import com.example.hangdiary.ui.screens.AboutScreen

import com.example.hangdiary.viewmodel.DiaryListViewModel
import com.example.hangdiary.viewmodel.TagManagementViewModel
import com.example.hangdiary.viewmodel.TodoListViewModel
import com.example.hangdiary.viewmodel.SettingsViewModel

/**
 * 应用导航图
 * 定义应用的所有导航路径
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    diaryRepository: DiaryRepository,
    tagRepository: TagRepository,
    todoRepository: TodoRepository,
    darkTheme: MutableState<Boolean>
) {
    NavHost(
        navController = navController,
        startDestination = Screen.DiaryList.route
    ) {
        composable(Screen.DiaryList.route) {
            val diaryViewModel = DiaryListViewModel(diaryRepository, tagRepository)
            val tagViewModel = TagManagementViewModel(tagRepository)
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            
            DiaryListScreen(
                viewModel = diaryViewModel,
                tagViewModel = tagViewModel,
                settingsViewModel = settingsViewModel,
                navController = navController,
                darkTheme = darkTheme
            )
        }
        
        composable(
            route = Screen.DiaryDetail.route,
            arguments = listOf(
                navArgument("diaryId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val diaryId = backStackEntry.arguments?.getLong("diaryId") ?: 0L
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            DiaryDetailScreen(navController, diaryRepository, tagRepository, settingsViewModel, diaryId)
        }
        

        
        composable(Screen.TodoList.route) {
            val todoViewModel = TodoListViewModel(todoRepository)
            TodoListScreen(
                viewModel = todoViewModel,
                navController = navController
            )
        }
        
        composable(Screen.TagManagement.route) {
            val tagViewModel = TagManagementViewModel(tagRepository)
            TagManagementScreen(
                viewModel = tagViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                darkTheme = darkTheme,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.About.route) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * 屏幕路由定义
 */
sealed class Screen(val route: String) {
    object DiaryList : Screen("diaryList")
    object DiaryDetail : Screen("diaryDetail/{diaryId}")
    object TodoList : Screen("todoList")
    object TagManagement : Screen("tagManagement")
    object Settings : Screen("settings")
    object About : Screen("about")
}