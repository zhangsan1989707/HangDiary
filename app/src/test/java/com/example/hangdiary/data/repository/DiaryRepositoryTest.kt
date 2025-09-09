package com.example.hangdiary.data.repository


import com.example.hangdiary.data.dao.DiaryDao
import com.example.hangdiary.data.model.Diary
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import java.time.LocalDateTime

/**
 * DiaryRepository单元测试类
 * 测试日记数据仓库的功能，包括颜色相关的数据库操作
 */
@RunWith(MockitoJUnitRunner::class)
class DiaryRepositoryTest {
    
    @Mock
    private lateinit var mockDiaryDao: DiaryDao
    
    private lateinit var repository: DiaryRepository
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = DiaryRepository(mockDiaryDao)
    }
    
    @Test
    fun testInsertDiaryWithColor() = runBlocking {
        // 准备测试数据
        val testDiary = Diary(
            id = 0L,
            title = "测试日记",
            content = "测试内容",
            color = "red",
            imagePaths = listOf("path1", "path2"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 模拟DAO行为
        `when`(mockDiaryDao.insert(testDiary)).thenReturn(1L)
        
        // 调用Repository方法
        val result = repository.insert(testDiary)
        
        // 验证结果
        assertEquals(1L, result)
        verify(mockDiaryDao).insert(testDiary)
    }
    
    @Test
    fun testUpdateDiaryWithColor() = runBlocking {
        // 准备测试数据
        val testDiary = Diary(
            id = 1L,
            title = "更新日记",
            content = "更新内容",
            color = "blue",
            imagePaths = listOf("path1", "path2"),
            createdAt = LocalDateTime.now().minusDays(1),
            updatedAt = LocalDateTime.now()
        )
        
        // 模拟DAO行为
        `when`(mockDiaryDao.update(testDiary)).thenReturn(1)
        
        // 调用Repository方法
        val result = repository.update(testDiary)
        
        // 验证结果
        assertEquals(1, result)
        verify(mockDiaryDao).update(testDiary)
    }
    
    @Test
    fun testUpdateDiaryColor() = runBlocking {
        // 准备测试数据
        val diaryId = 1L
        val color = "green"
        
        // 模拟DAO行为
        `when`(mockDiaryDao.updateDiaryColor(diaryId, color)).thenReturn(1)
        
        // 调用Repository方法
        val result = repository.updateDiaryColor(diaryId, color)
        
        // 验证结果
        assertEquals(1, result)
        verify(mockDiaryDao).updateDiaryColor(diaryId, color)
    }
    
    @Test
    fun testBatchUpdateDiaryColors() = runBlocking {
        // 准备测试数据
        val diaryIds = listOf(1L, 2L, 3L)
        val color = "yellow"
        
        // 模拟DAO行为
        `when`(mockDiaryDao.updateDiariesColor(diaryIds, color)).thenReturn(3)
        
        // 调用Repository方法
        val result = repository.updateDiariesColor(diaryIds, color)
        
        // 验证结果
        assertEquals(3, result)
        verify(mockDiaryDao).updateDiariesColor(diaryIds, color)
    }
    
    @Test
    fun testGetDiaryById() = runBlocking {
        // 准备测试数据
        val diaryId = 1L
        val testDiary = Diary(
            id = diaryId,
            title = "测试日记",
            content = "测试内容",
            color = "red",
            imagePaths = listOf("path1", "path2"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 模拟DAO行为
        `when`(mockDiaryDao.getById(diaryId)).thenReturn(testDiary)
        
        // 调用Repository方法
        val result = repository.getDiaryById(diaryId)
        
        // 验证结果
        assertEquals(testDiary, result)
        verify(mockDiaryDao).getById(diaryId)
    }
    
    @Test
    fun testDeleteDiary() = runBlocking {
        // 准备测试数据
        val testDiary = Diary(
            id = 1L,
            title = "测试日记",
            content = "测试内容",
            color = "red",
            imagePaths = listOf("path1", "path2"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 调用Repository方法
        repository.delete(testDiary)
        
        // 验证DAO调用
        verify(mockDiaryDao).delete(testDiary)
    }
    
    @Test
    fun testGetAllDiaries() = runBlocking {
        // 准备测试数据
        val testDiaries = listOf(
            Diary(
                id = 1L,
                title = "日记1",
                content = "内容1",
                color = "red",
                imagePaths = emptyList(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Diary(
                id = 2L,
                title = "日记2",
                content = "内容2",
                color = "blue",
                imagePaths = emptyList(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
        
        // 模拟DAO行为 - 返回Flow
        `when`(mockDiaryDao.getAllDiaries()).thenReturn(kotlinx.coroutines.flow.flowOf(testDiaries))
        
        // 调用Repository方法
        val result = repository.getAllDiaries()
        
        // 验证结果
        assertEquals(testDiaries, result.first())
        verify(mockDiaryDao).getAllDiaries()
    }
}