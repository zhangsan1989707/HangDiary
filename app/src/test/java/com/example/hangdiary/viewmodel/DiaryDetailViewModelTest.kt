package com.example.hangdiary.viewmodel


import com.example.hangdiary.data.model.Diary
import com.example.hangdiary.data.model.Tag
import com.example.hangdiary.data.repository.DiaryRepository
import com.example.hangdiary.data.repository.TagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import java.time.LocalDateTime

/**
 * DiaryDetailViewModel单元测试类
 * 测试日记详情页面的ViewModel功能，包括颜色选择和保存功能
 */
@RunWith(MockitoJUnitRunner::class)
class DiaryDetailViewModelTest {
    
    @Mock
    private lateinit var mockDiaryRepository: DiaryRepository
    
    @Mock
    private lateinit var mockTagRepository: TagRepository
    
    private lateinit var viewModel: DiaryDetailViewModel
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = DiaryDetailViewModel(mockDiaryRepository, mockTagRepository)
    }
    
    @Test
    fun testInitialState() {
        // 验证初始状态
        assertEquals("", viewModel.title)
        assertEquals("", viewModel.content)
        assertNull(viewModel.color)
        assertTrue(viewModel.imagePaths.isEmpty())
        assertTrue(viewModel.selectedTagIds.isEmpty())
        assertFalse(viewModel.isEditing)
    }
    
    @Test
    fun testLoadNewDiary() = runBlocking {
        // 测试加载新日记
        viewModel.loadDiary(0L)
        
        // 验证状态
        assertEquals("", viewModel.title)
        assertEquals("", viewModel.content)
        assertNull(viewModel.color)
        assertTrue(viewModel.imagePaths.isEmpty())
        assertTrue(viewModel.selectedTagIds.isEmpty())
        assertTrue(viewModel.isEditing)
    }
    
    @Test
    fun testLoadExistingDiary() = runBlocking {
        // 准备测试数据
        val testDiary = Diary(
            id = 1L,
            title = "测试日记",
            content = "测试内容",
            color = "blue",
            imagePaths = listOf("path1", "path2"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val testTags = listOf(
            Tag(id = 1L, name = "工作", color = 0xFF0000.toInt(), createdAt = LocalDateTime.now()),
            Tag(id = 2L, name = "生活", color = 0x00FF00.toInt(), createdAt = LocalDateTime.now())
        )
        
        // 模拟Repository行为
        `when`(mockDiaryRepository.getDiaryById(1L)).thenReturn(testDiary)
        `when`(mockTagRepository.getTagsForDiary(1L)).thenReturn(MutableStateFlow(testTags))
        
        // 加载日记
        viewModel.loadDiary(1L)
        
        // 验证状态
        assertEquals("测试日记", viewModel.title)
        assertEquals("测试内容", viewModel.content)
        assertEquals("blue", viewModel.color)
        assertEquals(listOf("path1", "path2"), viewModel.imagePaths)
        assertEquals(listOf(1L, 2L), viewModel.selectedTagIds)
        assertFalse(viewModel.isEditing)
    }
    
    @Test
    fun testSaveNewDiaryWithColor() = runBlocking {
        // 准备测试数据
        val testTags = listOf(
            Tag(id = 1L, name = "工作", color = 0xFF0000.toInt(), createdAt = LocalDateTime.now())
        )
        
        // 模拟Repository行为
        `when`(mockDiaryRepository.insert(org.mockito.kotlin.any())).thenReturn(1L)
        
        // 设置ViewModel状态
        viewModel.title = "新日记"
        viewModel.content = "新内容"
        viewModel.color = "red"
        viewModel.selectedTagIds = listOf(1L)
        
        // 保存日记
        viewModel.saveDiary()
        
        // 验证Repository调用
        verify(mockDiaryRepository).insert(org.mockito.kotlin.argThat { diary ->
            diary.title == "新日记" &&
            diary.content == "新内容" &&
            diary.color == "red"
        })
        
        verify(mockTagRepository).addTagToDiary(1L, 1L)
        
        // 验证状态
        assertFalse(viewModel.isEditing)
    }
    
    @Test
    fun testUpdateExistingDiaryWithColor() = runBlocking {
        // 准备测试数据
        val existingDiary = Diary(
            id = 1L,
            title = "旧日记",
            content = "旧内容",
            color = "green",
            imagePaths = emptyList(),
            createdAt = LocalDateTime.now().minusDays(1),
            updatedAt = LocalDateTime.now().minusDays(1)
        )
        
        // 模拟Repository行为
        `when`(mockDiaryRepository.update(org.mockito.kotlin.any())).thenReturn(1)
        `when`(mockDiaryRepository.getDiaryById(1L)).thenReturn(existingDiary)
        
        // 加载现有日记
        viewModel.loadDiary(1L)
        
        // 更新ViewModel状态
        viewModel.title = "更新日记"
        viewModel.content = "更新内容"
        viewModel.color = "blue"
        
        // 保存日记
        viewModel.saveDiary()
        
        // 验证Repository调用
        verify(mockDiaryRepository).update(org.mockito.kotlin.argThat { diary ->
            diary.id == 1L &&
            diary.title == "更新日记" &&
            diary.content == "更新内容" &&
            diary.color == "blue"
        })
        
        // 验证状态
        assertFalse(viewModel.isEditing)
    }
    
    @Test
    fun testColorSelection() {
        // 测试颜色选择
        assertNull(viewModel.color)
        
        // 设置颜色
        viewModel.color = "red"
        assertEquals("red", viewModel.color)
        
        // 更改颜色
        viewModel.color = "blue"
        assertEquals("blue", viewModel.color)
        
        // 清除颜色
        viewModel.color = null
        assertNull(viewModel.color)
    }
    
    @Test
    fun testDeleteDiary() = runBlocking {
        // 准备测试数据
        val testDiary = Diary(
            id = 1L,
            title = "测试日记",
            content = "测试内容",
            color = "red",
            imagePaths = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 模拟Repository行为
        `when`(mockDiaryRepository.getDiaryById(1L)).thenReturn(testDiary)
        
        // 加载日记
        viewModel.loadDiary(1L)
        
        // 删除日记
        viewModel.deleteDiary()
        
        // 验证Repository调用
        verify(mockTagRepository).clearAllTagsFromDiary(1L)
        verify(mockDiaryRepository).delete(testDiary)
    }
}