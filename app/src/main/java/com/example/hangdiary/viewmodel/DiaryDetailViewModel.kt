package com.example.hangdiary.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiaryDetailViewModel(private val repository: DiaryRepository) : ViewModel() {
    
    private val _diaryState = MutableStateFlow<Diary?>(null)
    val diaryState: StateFlow<Diary?> = _diaryState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    var isEditing by mutableStateOf(false)
    var title by mutableStateOf("")
    var content by mutableStateOf("")
    var categoryId by mutableStateOf(1L)
    var mood by mutableStateOf("")
    var weather by mutableStateOf("")
    var imagePaths by mutableStateOf(emptyList<String>())
    
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    fun loadDiary(diaryId: Long) {
        if (diaryId == 0L) {
            // 新建日记
            _diaryState.value = null
            title = ""
            content = ""
            categoryId = 1L
            mood = ""
            weather = ""
            imagePaths = emptyList()
            isEditing = true
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val diary = repository.getDiaryById(diaryId)
                _diaryState.value = diary
                diary?.let {
                    title = it.title
                    content = it.content
                    categoryId = it.categoryId ?: 1L
                    mood = it.mood ?: ""
                    weather = it.weather ?: ""
                    imagePaths = it.imagePaths
                }
            } catch (e: Exception) {
                _error.value = "加载日记失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveDiary() {
        try {
            val now = LocalDateTime.now()
            val diary = Diary(
                id = _diaryState.value?.id ?: 0L,
                title = title,
                content = content,
                categoryId = categoryId,
                mood = mood.ifEmpty { null },
                weather = weather.ifEmpty { null },
                imagePaths = imagePaths,
                createdAt = _diaryState.value?.createdAt ?: now,
                updatedAt = now
            )
            
            if (diary.id == 0L) {
                repository.insert(diary)
            } else {
                repository.update(diary)
            }
            
            _diaryState.value = diary
            isEditing = false
        } catch (e: Exception) {
            _error.value = "保存日记失败: ${e.message}"
        }
    }
    
    suspend fun deleteDiary() {
        _diaryState.value?.let { diary ->
            try {
                repository.delete(diary)
            } catch (e: Exception) {
                _error.value = "删除日记失败: ${e.message}"
            }
        }
    }
}