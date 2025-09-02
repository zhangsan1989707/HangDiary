package com.example.hangdiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.hangdiary.data.repository.CategoryRepository
import com.example.hangdiary.data.repository.DiaryRepository
import com.example.hangdiary.data.repository.TagRepository
import com.example.hangdiary.data.repository.TodoRepository
import com.example.hangdiary.ui.screens.CategoryScreen
import com.example.hangdiary.ui.screens.DiaryDetailScreen
import com.example.hangdiary.ui.screens.DiaryListScreen
import com.example.hangdiary.ui.screens.TodoListScreen
import com.example.hangdiary.viewmodel.CategoryViewModel
import com.example.hangdiary.viewmodel.DiaryListViewModel
import com.example.hangdiary.viewmodel.TagManagementViewModel
import com.example.hangdiary.viewmodel.TodoListViewModel

/**
 * 应用导航图
 * 定义应用的所有导航路径
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    diaryRepository: DiaryRepository,
    categoryRepository: CategoryRepository,
    tagRepository: TagRepository,
    todoRepository: TodoRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.DiaryList.route
    ) {
        composable(Screen.DiaryList.route) {
            val diaryViewModel = DiaryListViewModel(diaryRepository, tagRepository)
            val categoryViewModel = CategoryViewModel(categoryRepository)
            val tagViewModel = TagManagementViewModel(tagRepository)
            
            DiaryListScreen(
                viewModel = diaryViewModel,
                categoryViewModel = categoryViewModel,
                tagViewModel = tagViewModel,
                navController = navController
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
            DiaryDetailScreen(navController, diaryRepository, diaryId)
        }
        
        composable(Screen.Category.route) {
            CategoryScreen(navController, categoryRepository)
        }
        
        composable(Screen.TodoList.route) {
            val todoViewModel = TodoListViewModel(todoRepository)
            TodoListScreen(
                viewModel = todoViewModel,
                navController = navController
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
    object Category : Screen("category")
    object TodoList : Screen("todoList")
}