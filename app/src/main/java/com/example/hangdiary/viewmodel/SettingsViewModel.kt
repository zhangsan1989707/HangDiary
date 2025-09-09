package com.example.hangdiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hangdiary.data.model.Settings
import com.example.hangdiary.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 设置视图模型
 * 负责管理设置的UI状态和业务逻辑
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    // 设置状态
    private val _settingsState = MutableStateFlow<Settings?>(null)
    val settingsState: StateFlow<Settings?> = _settingsState.asStateFlow()
    
    // 向后兼容的设置属性
    val settings: StateFlow<Settings?> = settingsState

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 错误状态
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // 初始化时加载设置
        loadSettings()
    }

    /**
     * 加载设置
     */
    fun loadSettings() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val settings = settingsRepository.getOrCreateDefaultSettings()
                _settingsState.value = settings
            } catch (e: Exception) {
                _error.value = e.message ?: "加载设置失败"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 更新视图设置
     * @param viewMode 视图设置
     */
    fun updateViewMode(viewMode: String) {
        viewModelScope.launch {
            val currentSettings = _settingsState.value
            if (currentSettings != null) {
                try {
                    settingsRepository.updateViewMode(currentSettings.id, viewMode)
                    // 重新加载设置以更新状态
                    loadSettings()
                } catch (e: Exception) {
                    _error.value = e.message ?: "更新视图设置失败"
                }
            }
        }
    }



    /**
     * 更新暗黑模式设置
     * @param isDarkMode 是否启用暗黑模式
     */
    fun updateDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            val currentSettings = _settingsState.value
            if (currentSettings != null) {
                try {
                    settingsRepository.updateDarkMode(currentSettings.id, isDarkMode)
                    // 重新加载设置以更新状态
                    loadSettings()
                } catch (e: Exception) {
                    _error.value = e.message ?: "更新暗黑模式设置失败"
                }
            }
        }
    }

    /**
     * 更新卡片视图设置
     * @param isCardView 是否启用卡片视图
     */
    fun updateCardView(isCardView: Boolean) {
        viewModelScope.launch {
            val currentSettings = _settingsState.value
            if (currentSettings != null) {
                try {
                    settingsRepository.updateCardView(currentSettings.id, isCardView)
                    // 重新加载设置以更新状态
                    loadSettings()
                } catch (e: Exception) {
                    _error.value = e.message ?: "更新卡片视图设置失败"
                }
            }
        }
    }















    /**
     * 更新默认日记颜色设置
     * @param defaultDiaryColor 默认日记颜色
     */
    fun updateDefaultDiaryColor(defaultDiaryColor: String?) {
        viewModelScope.launch {
            val currentSettings = _settingsState.value
            if (currentSettings != null) {
                try {
                    settingsRepository.updateDefaultDiaryColor(currentSettings.id, defaultDiaryColor)
                    // 重新加载设置以更新状态
                    loadSettings()
                } catch (e: Exception) {
                    _error.value = e.message ?: "更新默认日记颜色设置失败"
                }
            }
        }
    }

    /**
     * 清除错误状态
     */
    fun clearError() {
        _error.value = null
    }
}