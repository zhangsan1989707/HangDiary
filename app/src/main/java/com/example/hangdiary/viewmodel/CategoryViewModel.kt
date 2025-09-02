package com.example.hangdiary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hangdiary.data.model.Category
import com.example.hangdiary.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 分类视图模型
 * 管理分类的UI状态和业务逻辑
 */
class CategoryViewModel(private val categoryRepository: CategoryRepository) : ViewModel() {
    // 分类列表状态
    private val _categoryListState = MutableStateFlow<List<Category>>(emptyList())
    val categoryListState: StateFlow<List<Category>> = _categoryListState

    // 编辑模式状态
    var isEditing by mutableStateOf(false)
        private set

    // 当前编辑的分类
    var currentCategory by mutableStateOf<Category?>(null)
        private set

    // 编辑状态下的分类名称
    var editCategoryName by mutableStateOf("")
        private set

    // 初始化加载所有分类
    init {
        loadAllCategories()
    }

    /**
     * 加载所有分类
     */
    fun loadAllCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collectLatest {
                _categoryListState.value = it
            }
        }
    }

    /**
     * 进入编辑模式
     * @param category 要编辑的分类，若为null则表示创建新分类
     */
    fun startEditing(category: Category? = null) {
        currentCategory = category
        editCategoryName = category?.name ?: ""
        isEditing = true
    }

    /**
     * 取消编辑
     */
    fun cancelEditing() {
        isEditing = false
        currentCategory = null
        editCategoryName = ""
    }

    /**
     * 更新编辑状态下的分类名称
     * @param name 新的分类名称
     */
    fun updateEditCategoryName(name: String) {
        editCategoryName = name
    }

    /**
     * 保存分类修改
     */
    fun saveCategory() {
        if (editCategoryName.isBlank()) {
            // 分类名称不能为空
            return
        }

        viewModelScope.launch {
            if (currentCategory != null) {
                // 更新现有分类
                val updatedCategory = currentCategory!!.copy(name = editCategoryName)
                categoryRepository.update(updatedCategory)
            } else {
                // 创建新分类
                val newCategory = Category(name = editCategoryName)
                categoryRepository.insert(newCategory)
            }

            // 保存后退出编辑模式
            cancelEditing()
        }
    }

    /**
     * 删除分类
     * @param category 要删除的分类
     */
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.delete(category)
        }
    }

    /**
     * 添加新分类
     * @param categoryName 分类名称
     */
    fun addCategory(categoryName: String) {
        viewModelScope.launch {
            val newCategory = Category(name = categoryName)
            categoryRepository.insert(newCategory)
            loadAllCategories() // 重新加载分类列表
        }
    }
}